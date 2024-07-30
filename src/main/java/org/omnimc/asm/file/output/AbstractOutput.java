package org.omnimc.asm.file.output;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.6
 */
public abstract class AbstractOutput<T, P> {

    //public abstract void init(String name, byte[] bytes); // This would be the abstract method in it

    public abstract String getName();

    public abstract T getOutput();

    public T getOutput(P parameter) {
        return null;
    }

}