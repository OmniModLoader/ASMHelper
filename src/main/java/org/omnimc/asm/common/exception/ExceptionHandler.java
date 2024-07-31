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

package org.omnimc.asm.common.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

/**
 * <h6>A utility class for handling and logging exceptions.
 * <p>
 * This class provides methods to log exceptions to a specified {@linkplain PrintStream} with optional additional
 * information. It includes features for wrapping text to ensure log output remains readable and properly formatted.
 * </p>
 *
 * <p>
 * <b>Example usage:<b>
 * <pre>{@code
 * Exception exception = new RuntimeException("Something went wrong");
 * ExceptionHandler.handleException("Additional text", exception);
 * }</pre>
 * </p>
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 2.2.6
 */
public final class ExceptionHandler {
    private static final PrintStream PRINT_STREAM = System.err;

    /**
     * <h6>Logs the given throwable using the default print stream ({@linkplain System#err}).
     * <p>
     * This method provides a default way to handle exceptions without additional context.
     * </p>
     *
     * @param throwable The {@linkplain Throwable} to log.
     */
    public static void handleException(@NotNull Throwable throwable) {
        handleException(null, throwable);
    }

    /**
     * <h6>Logs the given throwable with optional additional information using the default print stream
     * ({@linkplain System#err}).
     * <p>
     * This method allows logging of exceptions along with extra information.
     * </p>
     *
     * @param message   Optional additional context or message to log.
     * @param throwable The {@linkplain Throwable} to log.
     */
    public static void handleException(@Nullable Object message, @NotNull Throwable throwable) {
        handleException(message, throwable, PRINT_STREAM);
    }

    /**
     * <h6>Logs the given throwable with optional additional information to a specified print stream.
     * <p>
     * This method provides full control over the logging output destination and format.
     * </p>
     *
     * @param message     Optional additional context or message to log.
     * @param throwable   The {@linkplain Throwable} to log.
     * @param printStream The {@linkplain PrintStream} to which the log will be written.
     */
    public static void handleException(@Nullable Object message, @NotNull Throwable throwable, @NotNull PrintStream printStream) {
        final StringBuilder handler = new StringBuilder();
        handler.append("Exception in thread '")
                .append(Thread.currentThread().getName())
                .append("' ")
                .append(throwable)
                .append("\n");

        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            handler.append("   ") // Indent.
                    .append("at ")
                    .append(stackTraceElement.toString())
                    .append("\n");
        }

        if (message != null) {
            handler.append(" ") // Indent.
                    .append("Additional Information:\n");
            handler.append("  ")
                    .append(LineWrapper.wrapText(message instanceof String ? (String) message : message.toString()));
        }

        printStream.println(handler);
    }

    /**
     * <h6>A utility class for wrapping text to fit within a specified line length.
     * <p>
     * This class ensures that text output does not exceed a predefined width, making it more readable in logs.
     * </p>
     *
     * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
     * @since 2.2.6
     */
    static class LineWrapper {
        private static final int WRAP_LENGTH = 80;
        private static final String SPACE = "  "; // The indentation.

        /**
         * <h6>Wraps the provided text to fit within the {@linkplain #WRAP_LENGTH} characters per line.
         * <p>
         * This method ensures that long lines of text are broken into multiple lines to maintain readability.
         * </p>
         *
         * @param text The text to wrap.
         * @return The wrapped text, with lines no longer than {@linkplain #WRAP_LENGTH} characters.
         */
        @NotNull
        public static String wrapText(@NotNull String text) {
            if (text.isEmpty()) {
                return text;
            }

            final StringBuilder wrappedText = new StringBuilder();
            final String[] words = text.split(" ");
            int currentLength = 0;

            for (String word : words) {
                if ((currentLength + word.length() + 1) > WRAP_LENGTH) {
                    wrappedText.append("\n").append(SPACE);
                    currentLength = SPACE.length();
                }

                wrappedText.append(word).append(" ");
                currentLength += word.length() + 1;
            }

            return wrappedText.toString().trim();
        }
    }
}