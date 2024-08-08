/*
 * MIT License
 *
 * Copyright (c) 2024 OmniMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.omnimc.asm.creator;

import org.omnimc.asm.creator.method.MethodCreator;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;

/**
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 2.2.3
 */
@SuppressWarnings("unused")
public class ClassCreator {
    private final ClassWriter classWriter;
    private Option currentOption;

    public ClassCreator(int flags) {
        this.classWriter = new ClassWriter(flags);
    }

    public ClassCreator createClass(@NotNull Option selectedOption, IOption option) {
        this.currentOption = selectedOption;

        return this;
    }

    public ClassCreator createField(int access, String name, String descriptor, String signature, Object value) {
        switch (currentOption) {

        }

        return this;
    }

    public MethodCreator createMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new MethodCreator(access, name, descriptor, signature, exceptions, classWriter);
    }


    public ClassCreator end() {
        classWriter.visitEnd();
        return this;
    }

    public enum Option {
        CLASS,
        INTERFACE,
        RECORD,
        ANNOTATION,
        ENUM;
    }
}