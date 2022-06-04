package org.zwobble.clunk.backends;

import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.config.ProjectConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Backend {
    void compile(
        List<TypedNamespaceNode> typedNamespaceNodes,
        Path outputRoot,
        ProjectConfig projectConfig
    ) throws IOException;
}
