package com.universal.asm.manager;

import com.universal.asm.changes.IClassChange;
import com.universal.asm.changes.IResourceChange;
import com.universal.asm.file.ClassFile;
import com.universal.asm.file.IOutputFile;
import com.universal.asm.file.ResourceFile;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <h6>{@linkplain ClassManager} manages a collection of classes and resources from a JAR file.
 * <p>It provides methods to read a JAR file, apply changes to classes and resources,
 * and generate an {@linkplain IOutputFile} containing modified classes and resources.
 *
 * <p><b>Usage:</b></p>
 * <pre>{@code
 *    public static void main(String[] args) {
 *        // Creating an instance of ClassManager.
 *        ClassManager classManager = new ClassManager();
 *
 *        classManager.readJarFile(new File("Random.jar")); // JAR you want to read.
 *
 *        // Applying changes to classes.
 *        classManager.applyChanges((IClassChange) (name, classBytes) -> { // This is the new way of applying changes.
 *            // You have to set up your own ClassReader and ClassWriter.
 *            // Then in this example we are accepting a class that extends ClassVisitor.
 *            ClassReader cr = new ClassReader(classBytes);
 *            ClassWriter writer = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
 *            cr.accept(new TestVisitor(Opcodes.ASM9, writer), ClassReader.EXPAND_FRAMES);
 *            // You have to change the name separately as of right now.
 *
 *            return new ClassFile("Modified" + name, writer.toByteArray()); // Returns a ClassFile.
 *        });
 *
 *        // Applying changes to resources
 *        classManager.applyChanges((IResourceChange) (name, data) -> { // This is the new way of applying changes.
 *            name = "Monkey"; // This will overwrite every entry and cause issues don't use this as code.
 *            return new ResourceFile(name, data); // Returns a ResourceFile.
 *        });
 *
 *        IOutputFile outputFile = classManager.outputFile(); // This returns the Output data.
 *
 *        byte[] outputBytes = outputFile.getFileInBytes(Deflater.DEFLATED); // This is how you set the Compression Level.
 *    }
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
     * Represents the classes of the JAR file inputted.
     */
    private final HashMap<String, byte[]> classes = new HashMap<>();
    /**
     * Represents the resources of the JAR file inputted.
     */
    private final HashMap<String, byte[]> resources = new HashMap<>();

    /**
     * <h6>Reads a JAR file and populates the {@linkplain #classes} and {@linkplain #resources} collections.
     * <p>If the JAR file contains classes (.class files), they are parsed using the ASM library
     * and stored as byte arrays in the {@linkplain #classes} map. Other resources are stored
     * in the {@linkplain #resources} map.
     *
     * @param fileInput The input File object representing the JAR file to be read. Must not be null.
     * @throws NullPointerException If the provided file is null.
     * @throws RuntimeException If the provided file is not a JAR file or if an I/O error occurs.
     */
    @Override
    public void readJarFile(File fileInput) {
        Objects.requireNonNull(fileInput, "You cannot have a NULL file as an input.");

        if (!fileInput.getName().endsWith(".jar")) {
            throw new RuntimeException("Input File HAS to be a Jar file, or end with .jar!");
        }

        this.fileName = fileInput.getName();

        try (JarFile jar = new JarFile(fileInput)) {
            List<JarEntry> classEntries = Collections.list(jar.entries());

            for (JarEntry classEntry : classEntries) {
                String name = classEntry.getName();
                if (name.endsWith("/")) { // Can't read folders
                    continue;
                }

                try {
                    /* Creating streams */
                    // We need to make sure we have all the streams available, so we can close them later on. (saving performance)
                    InputStream stream = jar.getInputStream(classEntry);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                    byte[] value = this.toByteArray(stream, outputStream);

                    if (name.contains(".class")) {
                        /* Adding classes */
                        if (classes.containsKey(name)) {
                            return;
                        }

                        classes.putIfAbsent(name, value);
                    } else {
                        /* Adding non-class entries */
                        if (resources.containsKey(name)) {
                            return;
                        }

                        resources.putIfAbsent(name, value);
                    }

                    // Closing streams to free resources.
                    stream.close();
                    outputStream.close();
                } catch (IOException ignored) {
                    // Ignore exceptions during processing.
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading JAR file: " + e.getMessage(), e);
        }

    }

    /**
     * <h6>Applies changes to classes based on a provided array of {@linkplain IClassChange}.
     * <p>This method iterates through the {@linkplain #classes} map, applying changes using the array of {@linkplain IClassChange} implementations provided.
     *
     * @param classChanges Array of {@linkplain IClassChange} implementations for modifying classes.
     *                     Must not be null.
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
        HashMap<String, byte[]> tempHashMap = new HashMap<>(classes);

        for (IClassChange change : classChanges) {
            HashMap<String, byte[]> updatedTempHashMap = new HashMap<>();

            // Process each class entry.
            tempHashMap.forEach((className, classData) -> {
                // Apply the current change to the class data.
                ClassFile modifiedClassFile = change.applyChange(className, classData);

                if (modifiedClassFile != null) {
                    // Update the intermediate map with the modified data.
                    updatedTempHashMap.put(modifiedClassFile.getKey(), modifiedClassFile.getValue());
                }
            });

            // Update the intermediate map for the next iteration.
            tempHashMap = updatedTempHashMap;
        }

        // update classes with modified values
        classes.clear();
        classes.putAll(tempHashMap);

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

        /* Applying resource changes and replacing them */
        HashMap<String, byte[]> tempHashMap = new HashMap<>(resources);

        for (IResourceChange change : resourceChanges) {
            HashMap<String, byte[]> updatedTempHashMap = new HashMap<>();

            // Process each resource entry.
            tempHashMap.forEach((resourceName, resourceData) -> {
                // Apply the current change to the resource data.
                ResourceFile modifiedResourceFile = change.applyChange(resourceName, resourceData);

                if (modifiedResourceFile != null) {
                    // Update the intermediate map with the modified data.
                    updatedTempHashMap.put(modifiedResourceFile.getKey(), modifiedResourceFile.getValue());
                }
            });

            // Update the intermediate map for the next iteration.
            tempHashMap = updatedTempHashMap;
        }

        // Update resources with modified values.
        resources.clear();
        resources.putAll(tempHashMap);
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
            public byte[] getFileInBytes(int compression) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
                    zipOutputStream.setLevel(compression);
                    // add classes to the zip output stream
                    for (Map.Entry<String, byte[]> entry : classes.entrySet()) {
                        String entryName = entry.getKey(); // todo add check to make sure it ends in .class
                        byte[] entryData = entry.getValue();

                        zipOutputStream.putNextEntry(new ZipEntry(entryName));
                        zipOutputStream.write(entryData);
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
     * <p>It resets {@linkplain #fileName}, clears {@linkplain #classes} map, and clears {@linkplain #resources} map.
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
    private byte[] toByteArray(InputStream inputStream, ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        return byteArrayOutputStream.toByteArray();
    }

    public String getFileName() {
        return fileName;
    }
}
