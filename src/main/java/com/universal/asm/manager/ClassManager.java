package com.universal.asm.manager;

import com.universal.asm.changes.IClassChange;
import com.universal.asm.changes.IResourceChange;
import com.universal.asm.file.IOutputFile;
import com.universal.asm.file.ResourceFile;
import jdk.jfr.Description;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <h6>ClassManager manages a collection of classes and resources from a JAR file.
 * <p>It provides methods to read a JAR file, apply changes to classes and resources,
 * and generate an output ZIP file containing modified classes and resources.
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
public class ClassManager implements IClassManager {
    private String fileName;
    private final HashMap<String, byte[]> resources = new HashMap<>();
    private final ArrayList<ClassNode> classes = new ArrayList<>();

    /**
     * <h6>Reads a JAR file and populates the {@linkplain #classes} and {@linkplain #resources} collections.
     * <p>If the JAR file contains classes (.class files), they are parsed using ASM library
     * and stored as {@linkplain ClassNode} objects in the {@linkplain #classes}. Other resources are stored
     * in the {@linkplain #resources} map.
     *
     * @param fileInput The input File object representing the JAR file to be read.
     */
    @Override
    public void readJarFile(File fileInput) {
        Objects.requireNonNull(fileInput, "You cannot have a NULL file as an input.");

        if (!fileInput.getName().endsWith(".jar")) {
            throw new RuntimeException("Input File HAS to be a Jar file, or end with .jar!");
        }

        if (fileInput.getName().contains("\\")) {
            this.fileName = fileInput.getName().substring(fileInput.getName().lastIndexOf('\\'));
            this.fileName = fileName.replace("\\", "");
        }

        try (JarFile jar = new JarFile(fileInput)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                /* Adding classes */
                if (entry.getName().endsWith(".class")) {
                    ClassNode node = new ClassNode(Opcodes.ASM9);
                    ClassReader currentClassReader = new ClassReader(jar.getInputStream(entry));
                    currentClassReader.accept(node, 0);
                    classes.add(node);
                    continue;
                }

                /* Adding non-class resources */
                resources.put(entry.getName(), this.toByteArray(jar.getInputStream(entry)));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <h6>Applies changes to the classes and resources based on the provided arrays of changes.
     * <p>It iterates over the {@linkplain #classes} and {@linkplain #resources} collections, applying changes using
     * {@linkplain IClassChange} and {@linkplain IResourceChange} implementations respectively.
     *
     * @param classChanges   Array of {@linkplain IClassChange} implementations for modifying classes.
     * @param resourceChanges Array of {@linkplain IResourceChange} implementations for modifying resources.
     */
    @Override
    public void applyChanges(IClassChange[] classChanges, IResourceChange[] resourceChanges) {
        Objects.requireNonNull(classChanges, "The value 'classChanges' cannot be NULL.");
        if (classes.isEmpty()) {
            return;
        }

        /* Applying class changes and replacing them */
        List<ClassNode> classesCopy = new ArrayList<>(classes);
        Iterator<ClassNode> iterator = classesCopy.iterator();
        while (iterator.hasNext()) {
            ClassNode aClass = iterator.next();

            if (aClass == null) {
                continue;
            }

            for (IClassChange change : classChanges) {
                if (change == null) {
                    continue;
                }

                // apply class change
                ClassNode tempNode = change.applyChanges(aClass);
                iterator.remove();
                if (!classes.contains(tempNode)) {
                    classes.add(tempNode);
                }
            }
        }

        /* Applying resource changes and replacing them */
        if (resourceChanges != null && !resources.isEmpty()) {
            Map<String, byte[]> modifiedResources = new HashMap<>();
            for (Map.Entry<String, byte[]> entry : resources.entrySet()) {
                if (modifiedResources.containsKey(entry.getKey())) {
                    continue;
                }

                if (resourceChanges.length == 0) {
                    modifiedResources.put(entry.getKey(), entry.getValue());
                    continue;
                }

                for (IResourceChange resourceChange : resourceChanges) {
                    if (resourceChange == null) {
                        continue;
                    }

                    // apply resource change
                    ResourceFile apply = resourceChange.applyResourceChange(entry.getKey(), entry.getValue());
                    modifiedResources.put(apply.getKey(), apply.getValue());
                }
            }

            // update resources with modified values
            resources.clear();
            resources.putAll(modifiedResources);
        }
    }

    /**
     * <h6>Generates an {@linkplain IOutputFile} containing modified classes and resources.
     * <p>It creates a ZIP file in memory and adds modified classes and resources to it.
     * The output file can be retrieved as a byte array.
     *
     * @return An instance of {@linkplain IOutputFile} representing the generated output file.
     */
    @Override
    public IOutputFile outputFile() { // I hate this maybe fix later
        return new IOutputFile() {
            @Override
            public String getFileName() {
                return ClassManager.this.getFileName();
            }

            @Override
            public byte[] getFileInBytes() {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
                    // add classes to the zip output stream
                    for (ClassNode classNode : classes) {
                        String entryName = classNode.name + ".class";
                        zipOutputStream.putNextEntry(new ZipEntry(entryName));

                        // use ClassWriter to obtain bytecode
                        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                        classNode.accept(classWriter);
                        byte[] classBytes = classWriter.toByteArray();

                        zipOutputStream.write(classBytes);
                        zipOutputStream.closeEntry();
                    }

                    // add resources to the zip output stream
                    for (Map.Entry<String, byte[]> entry : resources.entrySet()) {
                        if (entry.getValue() == null) {
                            continue;
                        }

                        zipOutputStream.putNextEntry(new ZipEntry(entry.getKey()));
                        zipOutputStream.write(entry.getValue());
                        zipOutputStream.closeEntry();
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error creating output file", e);
                }

                return byteArrayOutputStream.toByteArray();
            }
        };
    }


    /**
     * <h6>Closes resources and clears internal collections.
     * <p>It resets {@linkplain #fileName}, clears {@linkplain #classes} list, and clears {@linkplain #resources} map.
     */
    @Override
    public void close() {
        this.fileName = null;
        classes.clear();
        resources.clear();
    }

    @Description("Going to move this to another file like a Util soon")
    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    @Description("Going to move this to another file like a Util soon")
    private void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[8192]; // 8KB buffer size (adjust as needed)
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public String getFileName() {
        return fileName;
    }
}
