package me.examples;

import com.universal.asm.changes.IClassChange;
import com.universal.asm.changes.IResourceChange;
import com.universal.asm.file.IOutputFile;
import com.universal.asm.file.ResourceFile;
import com.universal.asm.manager.ClassManager;
import com.universal.asm.manager.IClassManager;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.util.Random;

public class ExampleFile {

    /* Implementing IClassChange */
    public static class RenameChange implements IClassChange {

        @Override
        public ClassNode applyChanges(ClassNode classNode) {
            // Implementing a remapper here for this change would be the actual usage but for this example we will be changing the name to "hii".
            classNode.name = "Hiiiiii" + new Random().nextInt(Integer.MAX_VALUE); // If the class name is the same it will be overwritten over and over.
            return classNode; // You need to return the classNode you changed
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

            // There are multiple ways of using `applyChanges`, I will show you another way you can do it.

            /* Another Way */
            IClassChange[] classChanges = new IClassChange[] {new RenameChange()}; // A list of IClassChanges

            IResourceChange[] resourceChanges = new IResourceChange[] {new ManifestChange()}; // A list of IResourceChanges

            classManager.applyChanges(classChanges, resourceChanges);

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
        public void applyChanges(IClassChange[] classChanges, IResourceChange[] resourceChanges) {
            // Call both `applyChanges` functions or create your own logic here.
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