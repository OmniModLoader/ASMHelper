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

package org.omnimc.asm.merger;

import org.jetbrains.annotations.NotNull;
import org.omnimc.asm.common.ByteUtil;
import org.omnimc.asm.file.IOutputFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.jar.Attributes.Name.MAIN_CLASS;

/**
 * <h6>The {@linkplain JarMerger} class provides functionality to merge multiple JAR files into a single JAR file.
 * <p>It supports reading entries from multiple input JAR files, handling manifest attributes, and creating
 * a merged output JAR file.
 *
 * <p><b>Thread Safety:</b>
 * <ul>
 *   <li>{@linkplain JarMerger} is not inherently thread-safe. Ensure that access to instances of {@linkplain JarMerger}
 *       from multiple threads is synchronized externally if necessary.</li>
 * </ul>
 *
 * <p><b>Usage:</b></p>
 * <pre>{@code
 *     public static void main(String[] args) throws IOException {
 *         JarMerger jarMerger = new JarMerger("MergedJarName.jar"); // Create an instance.
 *
 *         jarMerger.mergeJars(new JarFile("Random.jar"), new JarFile("Random1.jar")); // input as many JAR files you want to merge here.
 *
 *         IOutputFile mergeJarOutput = jarMerger.outputFile(); // This creates IOutputFile that contains the bytes of the file.
 *
 *         byte[] fileInBytes = mergeJarOutput.getFileInBytes(Deflater.NO_COMPRESSION); // This creates the Bytes and the Deflater is the compression level you want.
 *     }
 * }</pre>
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
public class JarMerger {
    private final ConcurrentHashMap<String, byte[]> fileInputs = new ConcurrentHashMap<>();
    private final String mergedJarName;
    private Attributes chosenManifestAttr = null;

    /**
     * <h6>Constructs a {@linkplain JarMerger} instance with the specified merged JAR file name.
     *
     * @param mergedJarName The name of the merged JAR file. Must end with ".jar".
     * @throws IllegalArgumentException If {@code mergedJarName} does not end with ".jar".
     */
    public JarMerger(@NotNull String mergedJarName) {
        Objects.requireNonNull(mergedJarName);

        if (!mergedJarName.endsWith(".jar")) {
            throw new IllegalArgumentException(mergedJarName + ", does not end in .jar. This needs to be a Jar file!");
        }

        this.mergedJarName = mergedJarName;
    }

    /**
     * <h6>Merges the contents of multiple input JAR files into the {@linkplain #fileInputs} map.
     * <p>It reads all entries (except directories) from each input JAR file and stores them in memory.
     *
     * @param inputs An array of {@linkplain JarFile} instances representing the input JAR files to merge.
     * @throws IllegalStateException If less than two input JAR files are provided.
     * @throws RuntimeException      If there is an error reading or parsing bytes from the input JAR files.
     */
    public void mergeJars(@NotNull JarFile... inputs) {
        Objects.requireNonNull(inputs);

        if (inputs.length < 1) {
            throw new IllegalStateException("You must have more than one input to merge JARs.");
        }

        try {
            for (JarFile jarFile : inputs) {
                this.checkManifest(jarFile.getManifest());

                List<JarEntry> entries = Collections.list(jarFile.entries());

                entries.parallelStream().forEach(jarEntry -> {
                    String entryName = jarEntry.getName();

                    if (entryName.endsWith("/")) {
                        return;
                    }

                    try {
                        InputStream inputStream = jarFile.getInputStream(jarEntry);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] value = ByteUtil.toByteArray(inputStream, byteArrayOutputStream);

                        fileInputs.putIfAbsent(entryName, value);
                    } catch (IOException e) {
                        throw new RuntimeException("Error parsing bytes.", e);
                    }
                });
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading manifests.", e);
        }
    }

    /**
     * <h6>Returns an {@linkplain IOutputFile} representing the merged JAR file.
     * <p>The merged JAR file contains all entries from the {@linkplain #fileInputs} map.
     *
     * @return An {@linkplain IOutputFile} object representing the merged output JAR file.
     * @throws RuntimeException If there is an error creating the output file, such as {@linkplain IOException} during
     *                          ZIP file operations.
     */
    public IOutputFile outputFile() {
        return new IOutputFile() {
            @Override
            public String getFileName() {
                return mergedJarName;
            }

            @Override
            public byte[] getFileInBytes(int compression) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
                    zipOutputStream.setLevel(compression);

                    for (Map.Entry<String, byte[]> entry : fileInputs.entrySet()) {
                        String entryName = entry.getKey();
                        byte[] entryData = entry.getValue();

                        zipOutputStream.putNextEntry(new ZipEntry(entryName));
                        zipOutputStream.write(entryData);
                        zipOutputStream.closeEntry();
                    }

                } catch (IOException e) {
                    throw new RuntimeException("Error creating output file.", e);
                }

                return byteArrayOutputStream.toByteArray();
            }
        };
    }

    /**
     * Clears all stored file inputs and resets the chosen manifest attributes.
     */
    public void close() {
        this.fileInputs.clear();
        this.chosenManifestAttr = null;
    }

    /**
     * Checks the manifest of a given {@linkplain JarFile} and ensures that only one main class attribute is chosen
     * among all input JAR files.
     *
     * @param manifest The {@linkplain Manifest} object representing the manifest of the {@linkplain JarFile}.
     * @throws IOException If there is an error accessing or reading the manifest.
     */
    private void checkManifest(@NotNull Manifest manifest) throws IOException {
        Attributes mainAttributes = manifest.getMainAttributes();
        String mainClass = mainAttributes.getValue(MAIN_CLASS);

        if (mainClass == null) {
            return;
        }

        if (chosenManifestAttr == null) {
            chosenManifestAttr = mainAttributes;
        } else {
            mainAttributes.remove(MAIN_CLASS);
        }
    }
}