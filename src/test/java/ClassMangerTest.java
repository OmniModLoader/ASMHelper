import com.universal.asm.manager.ClassManager;
import com.universal.asm.manager.thread.SafeClassManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class ClassMangerTest {

    // I will write more tests as more content gets added to this.
    @Test
    public void testNullFile() throws IOException {
        ClassManager classManager = new ClassManager();
        SafeClassManager safeClassManager = new SafeClassManager();

        File file = new File("HelloWorldTest.txt");

        FileWriter writer = new FileWriter(file);
        writer.write("Hello World YoungOne");
        writer.flush();
        writer.close();

        assertThrows(RuntimeException.class, () -> classManager.readJarFile(file));
        assertThrows(RuntimeException.class, () -> safeClassManager.readJarFile(file));
        file.deleteOnExit();

        assertThrows(NullPointerException.class, () -> classManager.readJarFile(null));
        assertThrows(NullPointerException.class, () -> safeClassManager.readJarFile(null));
    }
}
