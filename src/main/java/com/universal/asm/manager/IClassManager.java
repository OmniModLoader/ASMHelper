package com.universal.asm.manager;

import com.universal.asm.changes.IClassChange;
import com.universal.asm.changes.IResourceChange;
import com.universal.asm.file.IOutputFile;

import java.io.File;

/**
 * <h6>The interface defining operations to manage classes and resources within a JAR file.
 * <p>Implementing classes are responsible for reading a JAR file, applying changes,
 * generating an output file, and closing resources.
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
public interface IClassManager {

    /**
     * Reads a JAR file and populates internal structures with classes and resources.
     *
     * @param fileInput The input File object representing the JAR file to be read.
     */
    void readJarFile(File fileInput);

    /**
     * Applies changes to the loaded classes based on an array of {@linkplain IClassChange}.
     *
     * @param classChanges Array of {@linkplain IClassChange} implementations for modifying classes.
     */
    void applyChanges(IClassChange... classChanges);

    /**
     * Applies changes to the loaded resources based on an array of {@linkplain IResourceChange}.
     *
     * @param resourceChanges Array of {@linkplain IResourceChange} implementations for modifying resources.
     */
    void applyChanges(IResourceChange... resourceChanges);

    /**
     * Generates an output file containing modified classes and resources.
     *
     * @return An instance of {@linkplain IOutputFile} representing the generated output file.
     */
    IOutputFile outputFile();

    /**
     * Closes resources and clears internal collections.
     */
    void close();
}
