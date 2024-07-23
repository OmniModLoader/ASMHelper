package org.omnimc.asm.search;

import org.omnimc.asm.common.ByteUtil;
import org.omnimc.asm.file.IOutputFile;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <h6>Utility class for searching files within JAR archives.</h6>
 * <p>
 * This class provides methods to search for specific files inside JAR archives and retrieve them as {@linkplain IOutputFile} instances.
 * </p>
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.6
 */
public final class Search {

    /**
     * <h6>Searches for a specific file within a JAR archive.</h6>
     * <p>
     * This method searches for a file with the given name inside the specified JAR file. If the file is found,
     * it returns an {@linkplain IOutputFile} instance containing the file's name and its byte content.
     * </p>
     *
     * <h6>Example Usage:</h6>
     * <pre>{@code
     * File jarFile = new File("path/to/your/file.jar");
     * IOutputFile result = Search.searchInFile("META-INF/MANIFEST.MF", jarFile);
     * if (result != null) {
     *     System.out.println("File found: " + result.getFileName());
     *     byte[] content = result.getFileInBytes(0);
     *     // Do something with the content
     * } else {
     *     System.out.println("File not found.");
     * }
     * }</pre>
     *
     * @param fileName The name of the file to search for within the JAR archive.
     * @param fileToSearch The JAR file to search within.
     * @return An {@linkplain IOutputFile} instance if the file is found, or {@code null} if the file does not exist.
     * @throws RuntimeException if an I/O error occurs during the search.
     */
    public static IOutputFile searchInFile(String fileName, File fileToSearch) {
        if (!fileToSearch.exists() || !fileToSearch.getName().endsWith(".jar")) {
            return null;
        }

        try (JarFile jarFile = new JarFile(fileToSearch)) {
            JarEntry entry = jarFile.getJarEntry(fileName);
            if (entry != null) {
                try (InputStream inputStream = jarFile.getInputStream(entry);
                     ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                    byte[] byteArray = ByteUtil.toByteArray(inputStream, outputStream);

                    return new IOutputFile() {
                        @Override
                        public String getFileName() {
                            return fileName;
                        }

                        @Override
                        public byte[] getFileInBytes(int compression) {
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
