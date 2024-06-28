package com.universal.asm.manager;

import com.universal.asm.changes.IClassChange;
import com.universal.asm.changes.IResourceChange;
import com.universal.asm.file.IOutputFile;
import com.universal.asm.file.ResourceFile;
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
 * and generate an {@linkplain IOutputFile} containing modified classes and resources.
 *
 * <h6>Usage</h6>
 *
 * <pre>{@code
 * public static void main(String[] args) {
 *     // Create instance of ClassManager.
 *     ClassManager classManager = new ClassManager();
 *
 *     // Locate a file you want to read.
 *     File file = new File("Random.jar");
 *
 *     classManager.readJarFile(file); // This reads and populates both the classes list and the resources map.
 *
 *     // There are multiple ways to create IClassChanges and even IResourceChanges.
 *     // Today I will be doing them the simplest way just for this JavaDoc.
 *     classManager.applyChanges(classNode -> {
 *         classNode.name = "hiiii";
 *         return classNode;
 *     });
 *
 *     classManager.applyChanges((name, bytes) -> {
 *         name = "testOutput";
 *         return new ResourceFile(name, bytes);
 *     });
 *
 *     // Then create an IOutputFile.
 *     IOutputFile outputFile = classManager.outputFile();
 *
 *     // Then we want to close our ClassManager.
 *     classManager.close();
 * }
 * }</pre>
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
public class ClassManager implements IClassManager {
    /**
     * Represents the name of the JAR file inputted.
     */
    private String fileName;
    /**
     * Represents the resources of the JAR file inputted.
     */
    private final HashMap<String, byte[]> resources = new HashMap<>();
    /**
     * Represents the classes of the JAR file inputted.
     */
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
     * <h6>Applies changes to classes based on a provided array of {@linkplain IClassChange}.
     * <p>This method iterates through the {@linkplain #classes} list, applying changes using the list of {@linkplain IClassChange} provided.
     *
     * @param classChanges Array of {@linkplain IClassChange} implementations for modifying classes.
     */
    @Override
    public void applyChanges(IClassChange... classChanges) {
        if (classChanges == null || classChanges.length == 0) {
            return;
        }

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

    }

    /**
     * <h6>Applies changes to resources based on a provided array of {@linkplain IResourceChange}.
     * <p>This method iterates through the {@linkplain #resources} map, applying changes using the list of {@linkplain IResourceChange} provided.
     *
     * @param resourceChanges Array of {@linkplain IResourceChange} implementations for modifying resources.
     */
    @Override
    public void applyChanges(IResourceChange... resourceChanges) {
        if (resourceChanges == null || resourceChanges.length == 0) {
            return;
        }

        if (resources.isEmpty()) {
            return;
        }

        Map<String, byte[]> modifiedResources = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : resources.entrySet()) {
            if (modifiedResources.containsKey(entry.getKey())) {
                continue;
            }

            for (IResourceChange resourceChange : resourceChanges) {
                if (resourceChange == null) {
                    continue;
                }

                // apply resource change
                ResourceFile resourceFile = resourceChange.applyChange(entry.getKey(), entry.getValue());
                modifiedResources.put(resourceFile.getKey(), resourceFile.getValue());
            }
        }

        // update resources with modified values
        resources.clear();
        resources.putAll(modifiedResources);
    }

    /**
     * <h6>Applies changes to the classes and resources based on the provided arrays of changes.
     * <p>It delegates to {@linkplain #applyChanges(IClassChange...)} and {@linkplain #applyChanges(IResourceChange...)} methods to process the classes and resources
     *
     * @param classChanges   Array of {@linkplain IClassChange} implementations for modifying classes.
     * @param resourceChanges Array of {@linkplain IResourceChange} implementations for modifying resources.
     */
    @Override
    public void applyChanges(IClassChange[] classChanges, IResourceChange[] resourceChanges) {
        applyChanges(classChanges);
        applyChanges(resourceChanges);
    }

    /**
     * <h6>Generates an {@linkplain IOutputFile} containing modified classes and resources.
     * <p>It creates a ZIP file in memory and adds modified classes and resources to it.
     * The output file can be retrieved as a byte array.
     *
     * @return An instance of {@linkplain IOutputFile} representing the generated output file.
     */
    @Override
    public IOutputFile outputFile() {
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


    /**
     * <h6>Going to move to a "common" project for the Universal Loader
     */
    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * <h6>Going to move to a "common" project for the Universal Loader
     */
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
