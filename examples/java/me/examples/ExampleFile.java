package me.examples;

import org.omnimc.asm.changes.IClassChange;
import org.omnimc.asm.changes.IResourceChange;
import org.omnimc.asm.file.ClassFile;
import org.omnimc.asm.file.IOutputFile;
import org.omnimc.asm.file.ResourceFile;
import org.omnimc.asm.manager.ClassManager;
import org.omnimc.asm.manager.IClassManager;
import org.omnimc.asm.manager.multi.MultiClassManager;
import org.omnimc.asm.merger.JarMerger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.Deflater;

public class ExampleFile {

    /* MultiClassManager */

    public static class MultiClassManagerExample {
        public static void main(String[] args) {
            MultiClassManager classManager = new MultiClassManager(); // Creating an instance.

            // Remember performance will be hindered if JARs are big or if they have a lot of files.
            // You can add as many jar files here.
            classManager.readJarFiles(new File("Random.jar"), new File("Random1.jar"), new File("Random2.jar"));

            classManager.applyChanges((IClassChange) (name, classBytes) -> { // This will apply changes to all loaded JarFiles.
                name = "RandomName1231232131";
                return new ClassFile(name, classBytes);
            });

            // You can target the changes by calling this method.
            classManager.applyTargetedChanges("Random.jar", (IClassChange) (name, classBytes) -> { // You NEED the absolute name because it only saves in absolute names.
                name = "This_Random_Jar";
                return new ClassFile(name, classBytes);
            });

            // For resources, you can do these.
            classManager.applyChanges((IResourceChange) (name, resourceData) -> {
                name = "Testing All data.json";
                return new ResourceFile(name, resourceData);
            });

            classManager.applyTargetedChanges("Random1.jar", (IResourceChange) (name, resourceData) -> {
                name = "Random1 Test.png";
                return new ResourceFile(name, resourceData);
            });

            // When creating IResourceChange, or an IClassChange it is best to put them in a separate class and just implement them.

            IOutputFile[] outputs = classManager.createOutputs(); // This gives you access to all the ClassData.

            // For example here I will show you how to access one.
            outputs[0].getFileInBytes(Deflater.DEFLATED); // This is how you get the File in bytes, Deflater is the compression level, you choose what you want.

            IOutputFile targetedOutputFile = classManager.createTargetedOutputFile("Random.jar"); // This is how you create a targeted Output file, so you only output the file you want.

            classManager.close(); // It is best practice to close a MultiClassManager.
        }
    }

    /* Jar Merger */

    public static class JarMergerExample {
        public static void main(String[] args) throws IOException {
            JarMerger jarMerger = new JarMerger("MergedJarName.jar"); // This is what the JAR is going to be outputted too.

            jarMerger.mergeJars(new JarFile("Random.jar"), new JarFile("Random1.jar")); // You need more than one JARs to merge of course.

            IOutputFile outputFile = jarMerger.outputFile(); // This creates a IOutputFile to get the file in bytes.

            byte[] fileInBytes = outputFile.getFileInBytes(Deflater.DEFLATED); // This is how you get the File in bytes, Deflator is the compression level which you choose.

            jarMerger.close(); // It is best practice to close a JarMerger, so it doesn't leak classes or resources.
        }
    }

    /* ClassManager & SafeClassManager */

    /* Implementing IClassChange */
    public static class RenameChange implements IClassChange {

        @Override
        public ClassFile applyChange(String name, byte[] classBytes) { // New way of applying changes
            // You have to set up your own ClassReader and ClassWriter.
            // Then in this example we are accepting a class that extends ClassVisitor.
            ClassReader cr = new ClassReader(classBytes);
            ClassWriter writer = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            cr.accept(new TestVisitor(Opcodes.ASM9, writer), ClassReader.EXPAND_FRAMES);

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