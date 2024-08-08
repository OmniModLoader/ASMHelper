package classmanager;

import org.omnimc.asm.manager.ClassManager;
import org.omnimc.asm.manager.multi.MultiClassManager;
import org.omnimc.asm.manager.thread.SafeClassManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
public class ExceptionsClassManagerTest {

    @Test
    public void testMultiClassManagerNull() {
        MultiClassManager multiClassManager = new MultiClassManager();
        Assertions.assertThrows(NullPointerException.class, () -> multiClassManager.readJarFiles((File[]) null));
    }

    @Test
    public void testClassManagerNull() {
        ClassManager classManager = new ClassManager();
        Assertions.assertThrows(NullPointerException.class, () -> classManager.readJarFile(null));
    }

    @Test
    public void testSafeClassManagerNull() {
        SafeClassManager safeClassManager = new SafeClassManager();
        Assertions.assertThrows(NullPointerException.class, () -> safeClassManager.readJarFile(null));
    }

    @Test
    public void testRuntimeExceptionMultiClassManager() {
        MultiClassManager multiClassManager = new MultiClassManager();
        Assertions.assertThrows(RuntimeException.class, () -> multiClassManager.readJarFiles(new File("Random")));
    }

    @Test
    public void testRuntimeExceptionSafeClassManager() {
        SafeClassManager safeClassManager = new SafeClassManager();
        Assertions.assertThrows(RuntimeException.class, () -> safeClassManager.readJarFile(new File("Random")));
    }

    @Test
    public void testRuntimeExceptionClassManager() {
        ClassManager classManager = new ClassManager();
        Assertions.assertThrows(RuntimeException.class, () -> classManager.readJarFile(new File("Random")));
    }
}