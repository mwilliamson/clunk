package org.zwobble.clunk.backends;

import org.zwobble.clunk.ast.typed.TypedNamespaceNode;

import java.io.IOException;
import java.nio.file.Path;

public interface Backend {
    void compile(TypedNamespaceNode typedNamespaceNode, Path outputPath) throws IOException;
}
