package com.universal.asm.manager.thread;

import com.universal.asm.changes.IClassChange;
import com.universal.asm.changes.IResourceChange;
import com.universal.asm.file.ClassFile;
import com.universal.asm.file.IOutputFile;
import com.universal.asm.file.ResourceFile;
import com.universal.asm.manager.ClassManager;
import com.universal.asm.manager.IClassManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <h6>{@linkplain SafeClassManager} is an implementation of {@linkplain IClassManager} designed to manage a collection
 * of classes and resources from a JAR file in a thread-safe manner.
 * <p>It provides methods to read JAR files, apply changes to both classes and resources, and generate an
 * {@linkplain IOutputFile} that encapsulates the modifications made to the JAR file.
 *
 * <p><b>Thread Safety:</b>
 * <ul>
 *   <li>{@linkplain SafeClassManager} ensures thread safety through synchronized methods and the use of concurrent data structures (specifically {@linkplain ConcurrentHashMap} for {@linkplain #classes} and {@linkplain #resources}).
 *       This approach prevents issues related to simultaneous data modification when accessed across multiple threads.</li>
 *   <li>All critical operations such as reading JAR files ({@linkplain #readJarFile}), applying class and resource changes ({@linkplain #applyChanges}), generating output files ({@linkplain #outputFile}), and closing the session ({@linkplain #close}) are synchronized to maintain thread safety.</li>
 *   <li>The use of parallel streams for processing entries further enhances concurrency, optimizing performance while adhering to thread-safe practices.</li>
 * </ul>
 *
 * <p><b>Performance:</b>
 * <ul>
 *   <li>By leveraging concurrent data structures and parallel processing, {@linkplain SafeClassManager} significantly improves performance compared to the original {@linkplain ClassManager}.
 *       These optimizations ensure efficient thread management and streamlined data access, making it suitable for environments with high concurrent access demands.</li>
 * </ul>
 *
 * <p><b>Future Development:</b>
 * <ul>
 *   <li>Since version 1.1.3, {@linkplain SafeClassManager} has implemented robust thread safety measures, surpassing those available in {@linkplain ClassManager}.
 *       Ongoing updates aim to further refine these capabilities, ensuring seamless support for concurrent environments.</li>
 *   <li>It's important to note that {@linkplain ClassManager} remains inherently non-thread-safe, and there are no plans to retrofit it with thread-safe features.</li>
 * </ul>
 *
 * <p>If you encounter any issues related to thread safety or have suggestions for further improvements, please submit an issue ticket.
 * Your feedback plays a crucial role in enhancing the reliability and performance of {@linkplain SafeClassManager}.</p>
 *
 * <p><b>Usage:</b></p>
 * <pre>{@code
 *    public static void main(String[] args) {
 *        // Creating an instance of SafeClassManager.
 *        SafeClassManager classManager = new SafeClassManager();
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
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.1.3
 */
public class SafeClassManager implements IClassManager {
    /**
     * Represents the current file name being processed.
     */
    private String fileName;
    /**
     * Represents the classes read from the input file.
     * <p>We utilize a {@linkplain ConcurrentHashMap} to enhance performance and ensure synchronized access.</p>
     */
    private final ConcurrentHashMap<String, byte[]> classes = new ConcurrentHashMap<>();
    /**
     * Represents the non-class resources read from the JAR file.
     */
    private final ConcurrentHashMap<String, byte[]> resources = new ConcurrentHashMap<>();

    /**
     * <h6>Reads a JAR file and populates the {@linkplain #classes} and {@linkplain #resources} collections.
     * <p>If the JAR file contains classes (.class files), they are parsed using ASM library
     * and stored as byte arrays in the {@linkplain #classes} map. Other resources are stored in the
     * {@linkplain #resources} map.
     *
     * <p><b>Thread Safety:</b>
     * <ul>
     *   <li>This method is synchronized to ensure thread safety during the file reading and data population process.</li>
     *   <li>It utilizes a {@linkplain ConcurrentHashMap} for {@linkplain #classes} and {@linkplain #resources} to prevent issues related to simultaneous data modification from multiple threads.</li>
     *   <li>Parallel processing of JAR entries ensures efficient handling of file contents while maintaining thread-safe practices.</li>
     * </ul>
     *
     * @param fileInput The input File object representing the JAR file to be read. Must not be null.
     * @throws RuntimeException If the provided file is not a JAR file or if an I/O error occurs.
     */
    @Override
    public synchronized void readJarFile(File fileInput) {
        Objects.requireNonNull(fileInput);

        // We only use contains because it is proven to be faster seen here:
        // https://stackoverflow.com/questions/28208793/in-java-which-is-faster-string-containssome-text-or-regex-that-looks-for
        if (!fileInput.getName().contains(".jar") || fileInput.isDirectory()) {
            throw new RuntimeException("Input File HAS to be a Jar file, or end with .jar!");
        }

        this.fileName = fileInput.getName();

        try (JarFile jarFile = new JarFile(fileInput)) {
            List<JarEntry> entries = Collections.list(jarFile.entries());

            // Process each entry in parallel.
            entries.parallelStream().forEach(jarEntry -> {
                String entryName = jarEntry.getName();
                if (entryName.endsWith("/")) { // Can't read folders
                    return;
                }

                try {
                    /* Creating streams */
                    // We need to make sure we have all the streams available, so we can close them later on. (saving performance)
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    byte[] value = this.toByteArray(inputStream, byteArrayOutputStream);

                    /* Adding classes */
                    if (entryName.contains(".class")) {
                        if (classes.containsKey(entryName)) {
                            return;
                        }

                        classes.putIfAbsent(entryName, value); // don't know why I am doing this here.
                    } else {
                        /* Adding non-class entries */
                        resources.putIfAbsent(entryName, value);
                    }

                    // Closing streams to free resources.
                    inputStream.close();
                    byteArrayOutputStream.close();
                } catch (IOException ignored) {
                    // Ignore exceptions during processing.
                }
            });

        } catch (IOException e) {
            throw new RuntimeException("Error reading JAR file: " + e.getMessage(), e);
        }
    }

    /**
     * <h6>Applies a series of class changes to the classes stored in {@linkplain #classes}.
     * <p>This method synchronizes on {@linkplain #classes} to ensure thread safety during
     * modification of class nodes. It iterates through each class entry's and applies all specified
     * {@linkplain IClassChange} objects. If a change modifies a class entry, it updates the entry in the
     * {@linkplain #classes} map.
     *
     * <p><b>Thread Safety:</b>
     * <ul>
     *   <li>This method ensures thread safety by synchronizing on the {@linkplain #classes} map during modification operations.</li>
     *   <li>It uses a {@linkplain ConcurrentHashMap} to store intermediate modified data, allowing safe concurrent updates.</li>
     *   <li>Sequential processing of class changes further ensures data consistency and avoids race conditions.</li>
     * </ul>
     * @param classChanges One or more {@linkplain IClassChange} objects representing the changes to be applied to the
     *                     classes.
     */
    @Override
    public void applyChanges(IClassChange... classChanges) {
        if (classChanges == null || classChanges.length == 0) {
            return;
        }

        if (classes.isEmpty()) {
            return;
        }

        // Use a ConcurrentHashMap to store intermediate modified data.
        ConcurrentHashMap<String, byte[]> tempHashMap = new ConcurrentHashMap<>(classes);

        // Apply changes sequentially.
        for (IClassChange change : classChanges) {
            ConcurrentHashMap<String, byte[]> updatedTempHashMap = new ConcurrentHashMap<>();

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

        // Replace the original classes map with the final modified data.
        synchronized (classes) {
            classes.clear();
            classes.putAll(tempHashMap);
        }
    }

    /**
     * <h6>Applies a series of {@linkplain IResourceChange}'s to the resources stored in {@linkplain #resources}.
     * <p>This method synchronizes on {@linkplain #resources} to ensure thread safety during
     * modification of resource entries. It iterates through each resource entry and applies all specified
     * {@linkplain IResourceChange} objects. If a change modifies a resource, it updates the resource in the
     * {@linkplain #resources} map.
     *
     * <p><b>Thread Safety:</b>
     * <ul>
     *   <li>This method ensures thread safety by synchronizing on the {@linkplain #resources} map during modification operations.</li>
     *   <li>It utilizes a {@linkplain ConcurrentHashMap} to store intermediate modified data, allowing safe concurrent updates.</li>
     *   <li>Sequential processing of resource changes further ensures data consistency and avoids race conditions.</li>
     * </ul>
     *
     * @param resourceChanges One or more {@linkplain IResourceChange} objects representing the changes to be applied to
     *                        the resources.
     */
    @Override
    public void applyChanges(IResourceChange... resourceChanges) {
        if (resourceChanges == null || resourceChanges.length == 0) {
            return;
        }

        if (resources.isEmpty()) {
            return;
        }

        // Use a ConcurrentHashMap to store intermediate modified data.
        ConcurrentHashMap<String, byte[]> tempHashMap = new ConcurrentHashMap<>(resources);

        // Apply changes sequentially.
        for (IResourceChange change : resourceChanges) {
            ConcurrentHashMap<String, byte[]> updatedTempHashMap = new ConcurrentHashMap<>();

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

        // Replace the original resources map with the final modified data.
        synchronized (resources) {
            resources.clear();
            resources.putAll(tempHashMap);
        }
    }

    /**
     * <h6>Creates and returns an {@linkplain IOutputFile} representing the output file containing classes and resources.
     * <p>An {@linkplain IOutputFile} is generated from the current state of {@linkplain #classes} and
     * {@linkplain #resources}.
     * <p>This method is synchronized to ensure thread safety during the creation of the output file.
     * It collects all class files (stored as byte arrays) from {@linkplain #classes} and all resources (also stored as byte arrays)
     * from {@linkplain #resources}, compresses them into a ZIP file format, and returns an {@linkplain IOutputFile} object representing
     * the generated output.
     *
     * @return An {@linkplain IOutputFile} object representing the generated output file. The output file contains all
     * classes and resources from the current state of {@linkplain #classes} and {@linkplain #resources}.
     * @throws RuntimeException If an error occurs during the creation of the output file, such as IOException during
     *                          ZIP file operations.
     */
    @Override
    public synchronized IOutputFile outputFile() {
        return new IOutputFile() {
            @Override
            public String getFileName() {
                return fileName;
            }

            @Override
            public byte[] getFileInBytes(int compression) {

                synchronized (classes) {
                    synchronized (resources) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
                            zipOutputStream.setLevel(compression);
                            // Add classes to the zip output stream.
                            for (Map.Entry<String, byte[]> entry : classes.entrySet()) {
                                String entryName = entry.getKey();
                                if (!entryName.contains(".class")) {
                                    entryName = entryName.concat(".class");
                                }

                                byte[] entryData = entry.getValue();

                                zipOutputStream.putNextEntry(new ZipEntry(entryName));
                                zipOutputStream.write(entryData);
                                zipOutputStream.closeEntry();
                            }

                            // Add resources to the zip output stream.
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
                }

            }
        };
    }

    /**
     * <h6>Closes the current session by clearing all stored classes, resources, and resetting the file name.
     * <p>This method ensures thread safety by synchronizing on the instance itself and on the internal
     * collections {@linkplain #classes} and {@linkplain #resources}. It sequentially clears the contents of both
     * collections and sets {@linkplain #fileName} to {@code null}.
     * <p>It effectively releases resources held by this session and prepares the instance for potential
     * reuse or garbage collection. This method should be called when the session is no longer needed to free up memory
     * and ensure proper cleanup of internal state.
     */
    @Override
    public synchronized void close() {
        synchronized (classes) {
            this.classes.clear();
        }

        synchronized (resources) {
            this.resources.clear();
        }
        this.fileName = null;
    }

    private byte[] toByteArray(InputStream inputStream, ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
