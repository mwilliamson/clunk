package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptExpressionNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptReferenceNode;
import org.zwobble.clunk.backends.typescript.codegenerator.macros.TypeScriptListMacro;
import org.zwobble.clunk.backends.typescript.codegenerator.macros.TypeScriptMutableListMacro;
import org.zwobble.clunk.backends.typescript.codegenerator.macros.TypeScriptStringBuilderMacro;
import org.zwobble.clunk.backends.typescript.codegenerator.macros.TypeScriptUnitMacro;
import org.zwobble.clunk.types.ConstructedType;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Type;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeScriptMacros {
    private TypeScriptMacros() {
    }

    private static final Map<Type, TypeScriptClassMacro> CLASS_MACROS = Stream.of(
        TypeScriptMutableListMacro.INSTANCE,
        TypeScriptListMacro.INSTANCE,
        TypeScriptStringBuilderMacro.INSTANCE,
        TypeScriptUnitMacro.INSTANCE
    ).collect(Collectors.toMap(x -> x.receiverType(), x -> x));

    public static Optional<TypeScriptExpressionNode> compileTypeReference(Type type) {
        var macro = CLASS_MACROS.get(type);

        if (macro == null) {
            return Optional.empty();
        } else {
            return Optional.of(macro.compileTypeReference());
        }
    }

    public static Optional<TypeScriptClassMacro> lookupClassMacro(Type type) {
        if (type instanceof ConstructedType constructedType) {
            type = constructedType.constructor().genericType();
        }

        return Optional.ofNullable(CLASS_MACROS.get(type));
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
                        context.addImport("@mwilliamson/precisely", "deepEqualTo");
                        return new TypeScriptReferenceNode("deepEqualTo");
                    }
                })
            )
        )
    );

    public static Optional<TypeScriptStaticFunctionMacro> lookupStaticFunctionMacro(Type type) {
        if (type instanceof StaticFunctionType staticFunctionType) {
            var macro = STATIC_FUNCTION_MACROS.getOrDefault(staticFunctionType.namespaceName(), Map.of())
                .get(staticFunctionType.functionName());
            return Optional.ofNullable(macro);
        } else {
            return Optional.empty();
        }
    }
}
