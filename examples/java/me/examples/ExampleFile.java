package me.examples;

import com.universal.asm.changes.IClassChange;
import com.universal.asm.changes.IResourceChange;
import com.universal.asm.file.ClassFile;
import com.universal.asm.file.IOutputFile;
import com.universal.asm.file.ResourceFile;
import com.universal.asm.manager.ClassManager;
import com.universal.asm.manager.IClassManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.util.zip.Deflater;

public class ExampleFile {

    /* Implementing IClassChange */
    public static class RenameChange implements IClassChange {

        @Override
        public ClassFile applyChange(String name, byte[] classBytes) { // New way of applying changes
            // You have to set up your own ClassReader and ClassWriter.
            // Then in this example we are accepting a class that extends ClassVisitor.
            ClassReader cr = new ClassReader(classBytes);
            ClassWriter writer = new ClassWriter(cr, ClassWriter. COMPUTE_FRAMES);
            cr.accept(new TestVisitor(Opcodes.ASM9, writer), ClassReader. EXPAND_FRAMES);

            // You need to change the name separately.

            return new ClassFile(name, writer.toByteArray()); // You need to return a ClassFile
        }
    }

    /* Implementing IResourceChange */
    public static class ManifestChange implements IResourceChange {

        @Override
        public ResourceFile applyChange(String name, byte[] data) {
            // Implementing any code here will work for this example we will change the name of the file. This would normally break the JAR do not use this as actual code.
            if (name.equalsIgnoreCase("META-INF/MANIFEST.MF")) {
                name = "MY MANIFEST.MF";
            }
            return new ResourceFile(name, data); // You have to return a RecourseFile with the name and data (bytes) of the file.
        }
    }

    /* Adding the changes to the ClassManager */
    public static class Main {
        public static void main(String[] args) {
            ClassManager classManager = new ClassManager(); // Creating a new instance of ClassManager.

            File file = new File("Random.jar"); // Random JAR you want to read.

            classManager.readJarFile(file);

            /* Preferred Way */
            classManager.applyChanges(new RenameChange()); // Apply the IClassChange in here. You can input as many as you want.

            classManager.applyChanges(new ManifestChange()); // Apply the IResourceChange in here. You can input as many as you want.

        }
    }

    /* Creating an IOutputFile */
    public static class Example {
        public static void main(String[] args) {
            ClassManager classManager = new ClassManager(); // Creating a new instance of ClassManager.

            File file = new File("Random.jar"); // Random JAR you want to read.

            classManager.readJarFile(file); // Reading a File input.

            classManager.applyChanges(new RenameChange()); // Applying changes.

            // This is the only way you can output file data.
            IOutputFile outputFile = classManager.outputFile();

            byte[] fileInBytes = outputFile.getFileInBytes(Deflater.DEFLATED); // You can customize your own compression level like this.
        }
    }

    /* Implementing IClassManager */
    public static class CustomClassManager implements IClassManager {

        @Override
        public void readJarFile(File fileInput) {
            // Read JAR here.
        }

        @Override
        public void applyChanges(IClassChange... classChanges) {
            // Apply Class Changes here.
        }

        @Override
        public void applyChanges(IResourceChange... resourceChanges) {
            // Apply Resource Changes here.
        }

        @Override
        public IOutputFile outputFile() {
            // Return an IOutputFile with the modified class and resource data.
            return null;
        }

        @Override
        public void close() {
            // Close all maps and lists that you're using and reset any values you have here.
        }
    }
}