package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedFunctionSignatureNode;
import org.zwobble.clunk.ast.typed.TypedInterfaceNode;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.SubtypeRelations;
import org.zwobble.clunk.types.Types;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class JavaCodeGeneratorInterfaceTests {
    @Test
    public void sealedInterfaceIsCompiledToSealedInterface() {
        var interfaceType = Types.sealedInterfaceType(NamespaceId.source("one", "two"), "X");
        var node = Typed.interface_("X", interfaceType);
        var subtypeLookup = SubtypeRelations.EMPTY
            .addSealedInterfaceCase(interfaceType, Types.recordType(NamespaceId.source("one", "two"), "A"))
            .addSealedInterfaceCase(interfaceType, Types.recordType(NamespaceId.source("one", "two"), "B"));
        var context = new JavaCodeGeneratorContext(JavaTargetConfig.stub(), subtypeLookup);

        var result = JavaCodeGenerator.compileInterface(
            node,
            context
        );

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package one.two;
                
                public sealed interface X permits A, B {
                    <T> T accept(Visitor<T> visitor);
                    public interface Visitor<T> {
                        T visit(A a);
                        T visit(B b);
                    }
                }
                """
        ));
    }

    @Test
    public void unsealedInterfaceIsCompiledToUnsealedInterface() {
        var interfaceType = Types.unsealedInterfaceType(NamespaceId.source("one", "two"), "X");
        var node = Typed.interface_("X", interfaceType);
        var subtypeLookup = SubtypeRelations.EMPTY;
        var context = new JavaCodeGeneratorContext(JavaTargetConfig.stub(), subtypeLookup);

        var result = JavaCodeGenerator.compileInterface(
            node,
            context
        );

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package one.two;
                
                public interface X {
                }
                """
        ));
    }

    @Test
    public void functionsAreCompiledToMethods() {
        var node = TypedInterfaceNode.builder(NamespaceId.source("example", "project"), "Example")
            .addMethod(TypedFunctionSignatureNode.builder()
                .name("fullName")
                .returnType(Typed.typeLevelString())
                .build()
            )
            .build();
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileInterface(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package example.project;
                
                public interface Example {
                    String fullName();
                }
                """
        ));
    }
}
