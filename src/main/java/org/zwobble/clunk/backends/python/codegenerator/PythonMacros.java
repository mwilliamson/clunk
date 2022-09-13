package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.backends.python.ast.PythonExpressionNode;
import org.zwobble.clunk.backends.python.ast.PythonReferenceNode;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Type;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PythonMacros {
    private PythonMacros() {
    }

    private static final Map<NamespaceName, Map<String, PythonStaticFunctionMacro>> STATIC_FUNCTION_MACROS = Map.ofEntries(
        Map.entry(
            NamespaceName.fromParts("stdlib", "assertions"),
            Map.ofEntries(
                Map.entry("assertThat", new PythonStaticFunctionMacro() {
                    @Override
                    public PythonExpressionNode compileReceiver(PythonCodeGeneratorContext context) {
                        context.addImport(List.of("precisely", "assert_that"));
                        return new PythonReferenceNode("assert_that");
                    }
                })
            )
        ),
        Map.entry(
            NamespaceName.fromParts("stdlib", "matchers"),
            Map.ofEntries(
                Map.entry("equalTo", new PythonStaticFunctionMacro() {
                    @Override
                    public PythonExpressionNode compileReceiver(PythonCodeGeneratorContext context) {
                        context.addImport(List.of("precisely", "equal_to"));
                        return new PythonReferenceNode("equal_to");
                    }
                })
            )
        )
    );

    public static Optional<PythonStaticFunctionMacro> lookupStaticFunctionMacro(Type type) {
        if (type instanceof StaticFunctionType staticFunctionType) {
            var macro = STATIC_FUNCTION_MACROS.getOrDefault(staticFunctionType.namespaceName(), Map.of())
                .get(staticFunctionType.functionName());
            return Optional.ofNullable(macro);
        } else {
            return Optional.empty();
        }
    }
}
