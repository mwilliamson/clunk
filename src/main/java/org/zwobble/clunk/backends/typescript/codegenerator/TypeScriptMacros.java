package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptExpressionNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptReferenceNode;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.StaticFunctionType;

import java.util.Map;
import java.util.Optional;

public class TypeScriptMacros {
    private TypeScriptMacros() {
    }

    private static final Map<NamespaceName, Map<String, TypeScriptStaticFunctionMacro>> STATIC_FUNCTION_MACROS = Map.ofEntries(
        Map.entry(
            NamespaceName.fromParts("stdlib", "assertions"),
            Map.ofEntries(
                Map.entry("assertThat", new TypeScriptStaticFunctionMacro() {
                    @Override
                    public TypeScriptExpressionNode compileReceiver(TypeScriptCodeGeneratorContext context) {
                        context.addImport("@mwilliamson/precisely", "assertThat");
                        return new TypeScriptReferenceNode("assertThat");
                    }
                })
            )
        ),
        Map.entry(
            NamespaceName.fromParts("stdlib", "matchers"),
            Map.ofEntries(
                Map.entry("equalTo", new TypeScriptStaticFunctionMacro() {
                    @Override
                    public TypeScriptExpressionNode compileReceiver(TypeScriptCodeGeneratorContext context) {
                        context.addImport("@mwilliamson/precisely", "equalTo");
                        return new TypeScriptReferenceNode("equalTo");
                    }
                })
            )
        )
    );

    public static Optional<TypeScriptStaticFunctionMacro> lookupStaticFunctionMacro(StaticFunctionType staticFunctionType) {
        var macro = STATIC_FUNCTION_MACROS.getOrDefault(staticFunctionType.namespaceName(), Map.of())
            .get(staticFunctionType.functionName());
        return Optional.ofNullable(macro);
    }
}
