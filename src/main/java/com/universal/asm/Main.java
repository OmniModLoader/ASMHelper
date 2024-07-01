package com.universal.asm;

import com.universal.asm.manager.ClassManager;
import com.universal.asm.manager.thread.SafeClassManager;

import java.io.File;
import java.util.Scanner;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.1.3
 */
public class Main {

    /**
     * <h6>This is not a good program do not use this actually, this is only for dev env
     * <p>I can't really tell you to not use this but I would advise against it as it is/should be used for testing speed and not generally expecting an output other than println</p>
     * @param args ignore these :)
     */
    public static void main(String[] args) {
        System.out.println("+------------------------------------Time Test------------------------------------+");
        /* Choosing which ClassManager you want to use */
        System.out.println("Would you like to use the thread safe ClassManager? (y/n)");
        Scanner safeClassManagerInput = new Scanner(System.in);
        String output = safeClassManagerInput.nextLine();

        /* File Setup */
        System.out.println("Please input the file you want to time test. (full path)");
        Scanner fileInput = new Scanner(System.in);
        String input = fileInput.nextLine();

        File file = new File(input);
        if (!file.exists()) {
            throw new RuntimeException(input + " does not exist.");
        }

        if (output.equalsIgnoreCase("y")) {
            // Thread safe manager
            SafeClassManager classManager = new SafeClassManager();
            long threadCurrentTime = System.currentTimeMillis();
            classManager.readJarFile(file);
            long threadEndTime = System.currentTimeMillis();
            System.out.println("Time to read " + file.getName() + " is " + (threadEndTime - threadCurrentTime) + " ms");
        } else {
            ClassManager classManager = new ClassManager();
            long currentTime = System.currentTimeMillis();
            classManager.readJarFile(file);
            long endTime = System.currentTimeMillis();
            System.out.println("Time to read " + file.getName() + " is " + (endTime - currentTime) + " ms");
        }

        System.out.println("+---------------------------------------END---------------------------------------+");

    }
}
