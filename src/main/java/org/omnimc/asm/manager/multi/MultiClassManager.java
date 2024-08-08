/*
 * MIT License
 *
 * Copyright (c) 2024 OmniMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.omnimc.asm.manager.multi;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.omnimc.asm.changes.IClassChange;
import org.omnimc.asm.changes.IResourceChange;
import org.omnimc.asm.common.ByteUtil;
import org.omnimc.asm.common.exception.ExceptionHandler;
import org.omnimc.asm.file.ClassFile;
import org.omnimc.asm.file.IOutputFile;
import org.omnimc.asm.file.ResourceFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <h6>{@linkplain MultiClassManager} is a class designed to manage a collection of classes and resources from multiple
 * JAR files.
 * <p>It provides methods to read JAR files, apply changes to both classes and resources, and generate an
 * {@linkplain IOutputFile} that encapsulates the modifications made to the JAR files.
 *
 * <p><b>Thread Safety:</b>
 * <ul>
 *   <li>{@linkplain MultiClassManager} ensures thread safety through the use of concurrent data structures ({@linkplain ConcurrentHashMap})
 *       for {@linkplain #classes} and {@linkplain #resources}. This prevents issues related to simultaneous data modification when accessed across multiple threads.</li>
 *   <li>Operations such as reading JAR files ({@linkplain #readJarFiles}), applying class and resource changes ({@linkplain #applyChanges}), and creating output files ({@linkplain #createOutputs})
 *       are designed to handle concurrent access safely, optimizing performance in multi-threaded environments.</li>
 * </ul>
 *
 * <p><b>Usage:</b></p>
 * <pre>{@code
 *     public static void main(String[] args) {
 *         MultiClassManager classManager = new MultiClassManager(); // Creating an instance.
 *
 *         // Remember performance will be hindered if JARs are big or if they have a lot of files.
 *         // You can add as many jar files here.
 *         classManager.readJarFiles(new File("Random.jar"), new File("Random1.jar"), new File("Random2.jar"));
 *
 *         classManager.applyChanges((IClassChange) (name, classBytes) -> { // This will apply changes to all loaded JarFiles.
 *             name = "RandomName1231232131";
 *             return new ClassFile(name, classBytes);
 *         });
 *
 *         // You can target the changes by calling this method.
 *         classManager.applyTargetedChanges("Random.jar", (IClassChange) (name, classBytes) -> { // You NEED the absolute name because it only saves in absolute names.
 *             name = "This_Random_Jar";
 *             return new ClassFile(name, classBytes);
 *         });
 *
 *         // For resources, you can do these.
 *         classManager.applyChanges((IResourceChange) (name, resourceData) -> {
 *             name = "Testing All data.json";
 *             return new ResourceFile(name, resourceData);
 *         });
 *
 *         classManager.applyTargetedChanges("Random1.jar", (IResourceChange) (name, resourceData) -> {
 *             name = "Random1 Test.png";
 *             return new ResourceFile(name, resourceData);
 *         });
 *
 *         // When creating IResourceChange, or an IClassChange it is best to put them in a separate class and just implement them.
 *
 *         IOutputFile[] outputs = classManager.createOutputs(); // This gives you access to all the ClassData.
 *
 *         // For example here I will show you how to access one.
 *         outputs[0].getFileInBytes(Deflater.DEFLATED); // This is how you get the File in bytes, Deflater is the compression level, you choose what you want.
 *
 *         IOutputFile targetedOutputFile = classManager.createTargetedOutputFile("Random.jar"); // This is how you create a targeted Output file, so you only output the file you want.
 *     }
 * }</pre>
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
@ApiStatus.NonExtendable
public class MultiClassManager {

    private final ArrayList<String> fileNames = new ArrayList<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, byte[]>> classes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, byte[]>> resources = new ConcurrentHashMap<>();

    /**
     * <h6>Reads multiple JAR files and populates {@linkplain #classes} and {@linkplain #resources} collections.
     *
     * <p><b>Thread Safety:</b>
     * <ul>
     *   <li>This method ensures thread safety by utilizing concurrent data structures ({@linkplain ConcurrentHashMap})
     *       for storing class and resource data from each JAR file.</li>
     *   <li>Parallel processing of JAR entries optimizes performance while maintaining thread-safe practices.</li>
     * </ul>
     *
     * @param fileInputs Array of File objects representing the JAR files to be read. Must not be null.
     * @throws RuntimeException If any of the input files are not JAR files or an I/O error occurs during file reading.
     */
    public void readJarFiles(@NotNull File... fileInputs) {
        Objects.requireNonNull(fileInputs); //TODO why double null check...?

        for (File fileInput : fileInputs) {
            if (!fileInput.getName().endsWith(".jar")) {
                ExceptionHandler.handleException(new IllegalArgumentException("Input File have to be a JAR file!"));
                continue;
            }

            fileNames.add(fileInput.getName());

            try (JarFile jar = new JarFile(fileInput)) {
                List<JarEntry> classEntries = Collections.list(jar.entries());

                ConcurrentHashMap<String, byte[]> classTemp = new ConcurrentHashMap<>();
                ConcurrentHashMap<String, byte[]> resourceTemp = new ConcurrentHashMap<>();

                classEntries.parallelStream().forEach(jarEntry -> {
                    String entryName = jarEntry.getName();
                    if (entryName.endsWith("/")) {
                        return;
                    }

                    try {
                        InputStream stream = jar.getInputStream(jarEntry);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                        byte[] value = ByteUtil.toByteArray(stream, outputStream);

                        if (entryName.contains(".class")) {
                            classTemp.putIfAbsent(entryName, value);
                        } else {
                            resourceTemp.putIfAbsent(entryName, value);

                        }
                    } catch (IOException e) {
                        ExceptionHandler.handleException("Failure to read class/resource bytes, could be a corrupted JAR or I/O issues.", e);
                    }
                });

                classes.put(fileInput.getName(), classTemp);
                resources.put(fileInput.getName(), resourceTemp);

            } catch (IOException e) {
                ExceptionHandler.handleException("Failed to read JAR file, potentially could be a corrupted JAR or not actually a JAR file.", e);
            }
        }
    }

    /**
     * <h6>Applies targeted class changes to classes read from a specific JAR file.
     *
     * @param fileName     The name of the JAR file to which changes are applied.
     * @param classChanges One or more {@linkplain IClassChange} objects representing the changes to be applied to
     *                     classes.
     */
    public void applyTargetedChanges(String fileName, IClassChange... classChanges) {
        if (!fileNames.contains(fileName)) {
            return;
        }

        ConcurrentHashMap<String, byte[]> classTemp = classes.get(fileName);

        for (IClassChange change : classChanges) {
            ConcurrentHashMap<String, byte[]> updatedTempHashMap = new ConcurrentHashMap<>();

            classTemp.forEach((className, classData) -> {
                ClassFile modifiedClassFile = change.applyChange(className, classData);

                if (modifiedClassFile != null) {
                    updatedTempHashMap.put(modifiedClassFile.getKey(), modifiedClassFile.getValue());
                }
            });

            classTemp = updatedTempHashMap;
        }

        classes.put(fileName, classTemp);
    }

    /**
     * <h6>Applies class changes to classes read from all loaded JAR files.
     *
     * @param classChanges One or more {@linkplain IClassChange} objects representing the changes to be applied to
     *                     classes.
     */
    public void applyChanges(IClassChange... classChanges) {
        for (String fileName : fileNames) {
            this.applyTargetedChanges(fileName, classChanges);
        }
    }

    /**
     * <h6>Applies targeted resource changes to resources read from a specific JAR file.
     *
     * @param fileName        The name of the JAR file to which changes are applied.
     * @param resourceChanges One or more {@linkplain IResourceChange} objects representing the changes to be applied to
     *                        resources.
     */
    public void applyTargetedChanges(String fileName, IResourceChange... resourceChanges) {
        if (!fileNames.contains(fileName)) {
            return;
        }

        ConcurrentHashMap<String, byte[]> resourceTemp = resources.get(fileName);

        for (IResourceChange change : resourceChanges) {
            ConcurrentHashMap<String, byte[]> updatedTempHashMap = new ConcurrentHashMap<>();

            resourceTemp.forEach((resourceName, resourceData) -> {
                ResourceFile modifiedResourceFile = change.applyChange(resourceName, resourceData);

                if (modifiedResourceFile != null) {
                    updatedTempHashMap.put(modifiedResourceFile.getKey(), modifiedResourceFile.getValue());
                }
            });

            resourceTemp = updatedTempHashMap;
        }

        resources.put(fileName, resourceTemp);
    }

    /**
     * <h6>Applies resource changes to resources read from all loaded JAR files.
     *
     * @param resourceChanges One or more {@linkplain IResourceChange} objects representing the changes to be applied to
     *                        resources.
     */
    public void applyChanges(IResourceChange... resourceChanges) {
        for (String fileName : fileNames) {
            this.applyTargetedChanges(fileName, resourceChanges);
        }
    }

    /**
     * <h6>Creates an {@linkplain IOutputFile} representing the output file containing modified classes and resources
     * from a specific JAR file.
     *
     * @param fileName The name of the JAR file for which to create the output file.
     * @return An {@linkplain IOutputFile} object representing the generated output file.
     */
    public IOutputFile createTargetedOutputFile(String fileName) {
        if (!fileNames.contains(fileName)) {
            return null;
        }

        return new IOutputFile() {
            @Override
            public String getFileName() {
                return fileName;
            }

            @Override
            public byte[] getFileInBytes(int compression) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                ConcurrentHashMap<String, byte[]> classTempMap = classes.get(fileName);
                ConcurrentHashMap<String, byte[]> resourceTempMap = resources.get(fileName);

                try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
                    zipOutputStream.setLevel(compression);

                    classTempMap.forEach((className, classData) -> {
                        String entryName = className;
                        if (!entryName.contains(".class")) {
                            entryName = entryName + ".class";
                        }

                        try {
                            zipOutputStream.putNextEntry(new ZipEntry(entryName));
                            zipOutputStream.write(classData);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            ExceptionHandler.handleException("Failed to read '" + className + "', in '" + fileName + "'.", e);
                        }
                    });

                    resourceTempMap.forEach((resourceName, resourceData) -> {
                        try {
                            zipOutputStream.putNextEntry(new ZipEntry(resourceName));
                            zipOutputStream.write(resourceData);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            ExceptionHandler.handleException("Failed to read '" + resourceName + "', in '" + fileName + "'.", e);
                        }
                    });


                } catch (IOException e) {
                    ExceptionHandler.handleException("Failure to compress class/resource data, maybe null bytes or I/O issues.", e);
                }

                return byteArrayOutputStream.toByteArray();
            }
        };
    }

    /**
     * <h6>Creates an array of {@linkplain IOutputFile} objects representing the output files containing modified
     * classes and resources from all loaded JAR files.
     *
     * @return An array of {@linkplain IOutputFile} objects representing the generated output files.
     */
    public IOutputFile[] createOutputs() {
        ArrayList<IOutputFile> outputFiles = new ArrayList<>();

        for (String fileName : fileNames) {
            IOutputFile outputFile = this.createTargetedOutputFile(fileName);

            outputFiles.add(outputFile);
        }

        return outputFiles.toArray(new IOutputFile[0]);
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, byte[]>> getClasses() {
        return new ConcurrentHashMap<>(classes);
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, byte[]>> getResources() {
        return new ConcurrentHashMap<>(resources);
    }

    /**
     * <h6>Removes entries (classes and resources) associated with a specific JAR file from internal collections.
     *
     * @param fileName The name of the JAR file to remove entries from.
     */
    public void removeEntry(String fileName) {
        if (fileNames.contains(fileName)) {
            fileNames.remove(fileName);
            classes.remove(fileName);
            resources.remove(fileName);
        }
    }

    public void close() {
        fileNames.clear();
        classes.clear();
        resources.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MultiClassManager that = (MultiClassManager) o;
        return Objects.equals(fileNames, that.fileNames)
               && Objects.equals(getClasses(), that.getClasses())
               && Objects.equals(getResources(), that.getResources());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileNames, getClasses(), getResources());
    }

    @Override
    public String toString() {
        return "MultiClassManager{" +
               "fileNames=" + fileNames +
               ", classes=" + classes +
               ", resources=" + resources +
               '}';
    }
}