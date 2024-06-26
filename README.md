# ASM Helper

This is a library that makes modifying jars easier we will be using it to help us make changes to ModFiles in Universal.

However, you can use it in any fashion.

All remapping is handled by yourself not via this.

### Examples

<details>

<summary>Making a ClassManager</summary>

```java
import com.universal.asm.manager.ClassManager;

public static void main(String[] args) {
    ClassManager classManager = new ClassManager();
}
```

</details>

<details>

<summary>Modifying a Class or a Resource</summary>

```java
import com.universal.asm.changes.IClassChange;
import com.universal.asm.changes.IResourceChange;
import com.universal.asm.manager.ClassManager;

import java.io.File;
import java.util.Random;


public static void main(String[] args) {
    ClassManager classManager = new ClassManager();

    File file = new File("RandomFile.jar");

    classManager.readJarFile(file);
    
    IClassChange[] classChanges = new IClassChange[] {
            classNode -> {
                classNode.name = "Example" + new Random().nextInt(1000);

                return classNode;
            }
    };

    IResourceChange[] resourceChanges = new IResourceChange[] {
            (name, bytes) -> {
                if (name.equalsIgnoreCase("META-INF/MANIFEST.MF")) {
                    // then modify information in here using the bytes
                }
                return new ResourceFile(name, bytes);
            }
    };

    classManager.applyChanges(classChanges, resourceChanges);
}
```

</details>

<details>

<summary>Generating the output information</summary>

```java
import com.universal.asm.changes.IClassChange;
import com.universal.asm.changes.IResourceChange;
import com.universal.asm.file.IOutputFile;
import com.universal.asm.manager.ClassManager;

import java.io.File;
import java.util.Random;

public static void main(String[] args) {
    ClassManager classManager = new ClassManager();
    File file = new File("RandomFile.jar");
    
    classManager.readJarFile(file);

    IClassChange[] classChanges = new IClassChange[] {
            classNode -> {
                classNode.name = "Example" + new Random().nextInt(1000);

                return classNode;
            }
    };

    IResourceChange[] resourceChanges = new IResourceChange[] {
            (name, bytes) -> {
                if (name.equalsIgnoreCase("META-INF/MANIFEST.MF")) {
                    // then modify information in here using the bytes
                }
                return new ResourceFile(name, bytes);
            }
    };

    classManager.applyChanges(classChanges, resourceChanges);
    
    IOutputFile outputFile = classManager.outputFile(); // this is it yes i know
}
```

</details>
