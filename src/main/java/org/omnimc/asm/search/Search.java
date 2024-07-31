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

package org.omnimc.asm.search;

import org.omnimc.asm.common.ByteUtil;
import org.omnimc.asm.file.output.FileOutput;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <h6>Utility class for searching and retrieving files from JAR archives.
 * <p>
 * This class provides methods to locate specific files within JAR archives and retrieve them as {@linkplain FileOutput}
 * instances. It supports searching by file name and handles the extraction of the file's content.
 * </p>
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 2.2.6
 */
public final class Search {

    /**
     * Searches for a specific file within a JAR archive.
     * <p>
     * This method searches for a file with the specified name inside the given JAR file. If the file is found, it
     * returns an {@linkplain FileOutput} instance containing the file's name and its byte content.
     * </p>
     * <p>
     * The method performs the following steps:
     * <ul>
     *     <li>Checks if the specified file is a JAR file and exists.</li>
     *     <li>Opens the JAR file and searches for the specified file.</li>
     *     <li>If found, reads the file's content and returns it as a {@linkplain FileOutput} instance.</li>
     *     <li>If not found or an error occurs, returns {@code null} or throws a {@link RuntimeException}.</li>
     * </ul>
     * </p>
     *
     * <h6>Example Usage:</h6>
     * <pre>{@code
     * File jarFile = new File("path/to/your/file.jar");
     * FileOutput result = Search.searchInFile("META-INF/MANIFEST.MF", jarFile);
     * if (result != null) {
     *     System.out.println("File found: " + result.getName());
     *     byte[] content = result.getOutput();
     *     // Process the content
     * } else {
     *     System.out.println("File not found.");
     * }
     * }</pre>
     *
     * @param fileName     The name of the file to search for within the JAR archive.
     * @param fileToSearch The JAR file to search within.
     * @return An {@linkplain FileOutput} instance containing the file's name and content if found; {@code null} if the
     * } if the file does not exist in the JAR.
     * @throws RuntimeException If an I/O error occurs while accessing the JAR file or reading its contents.
     */
    public static FileOutput searchInFile(String fileName, File fileToSearch) {
        if (!fileToSearch.exists() || !fileToSearch.getName().endsWith(".jar")) {
            return null;
        }

        try (JarFile jarFile = new JarFile(fileToSearch)) {
            JarEntry entry = jarFile.getJarEntry(fileName);
            if (entry != null) {
                try (InputStream inputStream = jarFile.getInputStream(entry);
                     ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                    byte[] byteArray = ByteUtil.toByteArray(inputStream, outputStream);

                    return new FileOutput() {
                        @Override
                        public String getName() {
                            return entry.getName();
                        }

                        @Override
                        public byte[] getOutput(Integer parameter) {
                            return byteArray;
                        }

                        @Override
                        public byte[] getOutput() {
                            return byteArray;
                        }
                    };
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}