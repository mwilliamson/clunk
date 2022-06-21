package org.zwobble.clunk.backends;

import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.config.ProjectConfig;
import org.zwobble.clunk.typechecker.TypeCheckResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Backend {
    void compile(
        TypeCheckResult<List<TypedNamespaceNode>> typedNamespaceNodes,
        Path outputRoot,
        ProjectConfig projectConfig
    ) throws IOException;
}
