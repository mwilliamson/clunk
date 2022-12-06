package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.backends.java.ast.JavaExpressionNode;
import org.zwobble.clunk.backends.java.ast.JavaReferenceNode;
import org.zwobble.clunk.backends.java.codegenerator.macros.JavaListMacro;
import org.zwobble.clunk.backends.java.codegenerator.macros.JavaMutableListMacro;
import org.zwobble.clunk.backends.java.codegenerator.macros.JavaStringBuilderMacro;
import org.zwobble.clunk.types.ConstructedType;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Type;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaMacros {
    private static final Map<NamespaceName, Map<String, JavaStaticFunctionMacro>> STATIC_FUNCTION_MACROS = Map.ofEntries(
        Map.entry(
            NamespaceName.fromParts("stdlib", "assertions"),
            Map.ofEntries(
                Map.entry("assertThat", new JavaStaticFunctionMacro() {
                    @Override
                    public JavaExpressionNode compileReceiver(JavaCodeGeneratorContext context) {
                        context.addImportStatic("org.hamcrest.MatcherAssert", "assertThat");
                        return new JavaReferenceNode("assertThat");
                    }
                })
            )
        ),
        Map.entry(
            NamespaceName.fromParts("stdlib", "matchers"),
            Map.ofEntries(
                Map.entry("equalTo", new JavaStaticFunctionMacro() {
                    @Override
                    public JavaExpressionNode compileReceiver(JavaCodeGeneratorContext context) {
                        context.addImportStatic("org.hamcrest.Matchers", "equalTo");
                        return new JavaReferenceNode("equalTo");
                    }
                })
            )
        )
    );

    private static final Map<Type, JavaClassMacro> CLASS_MACROS = Stream.of(
        JavaListMacro.INSTANCE,
        JavaMutableListMacro.INSTANCE,
        JavaStringBuilderMacro.INSTANCE
    ).collect(Collectors.toMap(x -> x.receiverType(), x -> x));

    public static Optional<JavaStaticFunctionMacro> lookupStaticFunctionMacro(StaticFunctionType receiverType) {
        var macro = STATIC_FUNCTION_MACROS.getOrDefault(receiverType.namespaceName(), Map.of())
            .get(receiverType.functionName());
        return Optional.ofNullable(macro);
    }

    public static Optional<JavaExpressionNode> compileConstructorCall(
        Type type,
        List<JavaExpressionNode> positionalArgs
    ) {
        var classMacro = lookupClassMacro(type);
        if (classMacro.isPresent()) {
            return Optional.of(classMacro.get().compileConstructorCall(positionalArgs));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<JavaExpressionNode> compileMethodCall(
        Type type,
        JavaExpressionNode receiver,
        String methodName,
        List<JavaExpressionNode> positionalArgs
    ) {
        var classMacro = lookupClassMacro(type);
        if (classMacro.isPresent()) {
            return Optional.of(classMacro.get().compileMethodCall(receiver, methodName, positionalArgs));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<JavaClassMacro> lookupClassMacro(Type type) {
        if (type instanceof ConstructedType constructedType) {
            type = constructedType.constructor().genericType();
        }
        return Optional.ofNullable(CLASS_MACROS.get(type));
    }
}
