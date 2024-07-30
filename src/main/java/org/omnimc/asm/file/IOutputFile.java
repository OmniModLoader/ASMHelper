package org.omnimc.asm.file;

/**
 * <h6>The {@linkplain IOutputFile} interface represents an output file.
 * <p>It provides methods to retrieve the file name and obtain the file content as a byte array.
 * Implementing classes are expected to handle the generation and retrieval of file data.
 * <p>
 * Implementations of this interface should ensure proper handling of file content generation
 * and retrieval according to the specific requirements of the application.
 *
 * @deprecated since 2.2.6, I am currently working on a solution that will be flexible.
 *             Allowing you to choose what type of output you want.
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
@Deprecated(since = "2.2.6")
public interface IOutputFile {

    /**
     * Retrieves the file name associated with this output file.
     *
     * @return The file name as a String.
     */
    String getFileName();


    /**
     * Retrieves the content of the output file as a byte array.
     * The byte array represents the raw data of the file.
     *
     * @param compression Compression ratio used in generating the File.
     * @return The file content as a byte array.
     */
    byte[] getFileInBytes(int compression);
}
