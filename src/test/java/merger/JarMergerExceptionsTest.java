package merger;

import org.omnimc.asm.merger.JarMerger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.jar.JarFile;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
public class JarMergerExceptionsTest {

    @Test
    public void illegalArgumentTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JarMerger("Random"));
    }

    @Test
    public void nullMergedJarNameTest() {
        Assertions.assertThrows(NullPointerException.class, () -> new JarMerger(null));
    }

    @Test
    public void nullMergeJarsTest() {
        Assertions.assertThrows(NullPointerException.class, () -> new JarMerger("Test.jar").mergeJars((JarFile[]) null));
    }
}
