import com.universal.asm.file.IOutputFile;
import com.universal.asm.manager.ClassManager;
import com.universal.asm.manager.thread.SafeClassManager;
import com.universal.asm.merger.JarMerger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.Deflater;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.1.3
 */
public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        // Options.
        List<String> speedOptions = Arrays.asList("sp", "speed", "speedTest", "t", "test");
        List<String> safeClassLoader = Arrays.asList("T", "thread", "S", "safeloader", "SafeClassLoader");
        List<String> classLoader = Arrays.asList("C", "CL", "classloader", "U", "unsafe");
        List<String> mergeOptions = Arrays.asList("mj", "merge", "mergeJar", "mergeJars");

        // Parse arguments.
        String option = args[0].substring(1);

        if (speedOptions.contains(option)) {
            if (args.length < 3) {
                printSpeedUsage();
                System.exit(1);
            }

            boolean usingSafeManager = safeClassLoader.contains(args[1]); // todo make this so it can be `equalsIgnoreCase`

            if (!usingSafeManager) {
                if (!classLoader.contains(args[1])) {
                    printClassManagerUsage();
                    System.exit(1);
                }
            }

            String jarFile = args[2];
            runSpeedTest(usingSafeManager, jarFile);
        } else if (mergeOptions.contains(option)) {
            if (args.length == 2 || args.length == 3) {
                printMergeUsage();
                System.exit(1);
            }

            List<String> jars = Arrays.asList(Arrays.copyOfRange(args, 2, args.length));

            String mergedJarName = args[1];

            runJarMerge(mergedJarName, jars);
        } else {
            System.err.println("Invalid option: " + option);
            printUsage();
            System.exit(1);
        }
    }

    /* Applications */
    private static void runSpeedTest(boolean safeClassLoader, String jarInput) {
        System.out.println("+------------------------------------Time Test------------------------------------+");
        File file = new File(jarInput);
        if (!file.exists()) {
            throw new RuntimeException(jarInput + " does not exist.");
        }

        if (safeClassLoader) {
            // Thread safe manager
            SafeClassManager classManager = new SafeClassManager();
            long threadCurrentTime = System.currentTimeMillis();
            classManager.readJarFile(file);
            long threadEndTime = System.currentTimeMillis();
            System.out.println("Time to read " + file.getName() + " is " + (threadEndTime - threadCurrentTime) + " ms");
            classManager.close();
        } else {
            // Normal Class Manager
            ClassManager classManager = new ClassManager();
            long currentTime = System.currentTimeMillis();
            classManager.readJarFile(file);
            long endTime = System.currentTimeMillis();
            System.out.println("Time to read " + file.getName() + " is " + (endTime - currentTime) + " ms");
            classManager.close();
        }

        System.out.println("+---------------------------------------END---------------------------------------+");
    }

    private static void runJarMerge(String mergedJarName, List<String> inputs) {
        System.out.println("+------------------------------------Merging------------------------------------+");

        ArrayList<JarFile> jarFiles = new ArrayList<>();

        for (String input : inputs) {
            File file = new File(input);
            if (!file.exists()) {
                throw new RuntimeException(input + " does not exist!");
            }

            try {
                jarFiles.add(new JarFile(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        JarMerger merger = new JarMerger(mergedJarName);

        long currentTime = System.currentTimeMillis();
        merger.mergeJars(jarFiles.toArray(new JarFile[0])); // The actual method.
        long endTime = System.currentTimeMillis();
        System.out.println("It took " + (endTime - currentTime) + "ms to merge `" + jarFiles.size() + "` jars.");

        long outPutCurrentTime = System.currentTimeMillis();
        IOutputFile outputFile = merger.outputFile(); // The actual method.
        long outPutEndTime = System.currentTimeMillis();
        System.out.println("It took " + (outPutEndTime - outPutCurrentTime) + "ms to create an IOutputFile.");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile.getFileName());
            fileOutputStream.write(outputFile.getFileInBytes(Deflater.NO_COMPRESSION)); // I don't want to parse more data for this...
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        merger.close();

        System.out.println("+--------------------------------------END--------------------------------------+");
    }

    /* Prints */
    private static void printUsage() {
        System.err.println("Usage:");
        System.err.println("  - (sp, speed, speedTest, t, test) <(T, thread, S, safeloader, SafeClassLoader) | (C, CL, classloader, U, unsafe)> <JAR>");
        System.err.println("  - (mj, merge, mergeJar(s)) <mergedJarName> <JAR1> <JAR2> ... <JAR>");
    }

    private static void printSpeedUsage() {
        System.err.println("Invalid usage for speed-related options:");
        System.err.println("  - (sp, speed, speedTest, t, test) <(T, thread, S, safeloader, SafeClassLoader) | (C, CL, classloader, U, unsafe)> <JAR>");
    }

    private static void printClassManagerUsage() {
        System.err.println("Invalid usage for class manager option:");
        System.err.println("  - <(T, thread, S, safeloader, SafeClassLoader) | (C, CL, classloader, U, unsafe)>");
    }

    private static void printMergeUsage() {
        System.err.println("Invalid usage for merge options:");
        System.err.println("  - (mj, merge, mergeJar(s)) <mergedJarName> <JAR1> <JAR2> ... <JAR>");
    }
}
