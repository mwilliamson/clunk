package org.zwobble.clunk.errors;

public class CompilerError extends RuntimeException {
    public CompilerError(String message) {
        super(message);
    }
}
