package outputTest;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public abstract class AbstractOutput<T, P> {

    public abstract void init(String name, byte[] bytes); // This would be the abstract method in it

    public String getName() {
        return null;
    }

    public T getOutput() {
        return null;
    }

    public T getOutput(P parameter) {
        return null;
    }

}