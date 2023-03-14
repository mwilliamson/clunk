package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.SubtypeRelations;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorRecordTests {
    @Test
    public void recordIsCompiledToJavaRecord() {
        var node = TypedRecordNode.builder(NamespaceId.source("example", "project"), "Example")
            .addField(Typed.recordField("first", Typed.typeLevelString()))
            .addField(Typed.recordField("second", Typed.typeLevelInt()))
            .build();
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package example.project;
                
                public record Example(String first, int second) {
                }"""
        ));
    }

    @Test
    public void propertiesAreCompiledToMethods() {
        var node = TypedRecordNode.builder(NamespaceId.source("example", "project"), "Example")
            .addProperty(Typed.property(
                "value",
                Typed.typeLevelString(),
                List.of(Typed.returnStatement(Typed.string("hello")))
            ))
            .build();
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package example.project;
                
                public record Example() {
                    public String value() {
                        return "hello";
                    }
                }"""
        ));
    }

    @Test
    public void functionsAreCompiledToMethods() {
        var node = TypedRecordNode.builder(NamespaceId.source("example", "project"), "Example")
            .addMethod(TypedFunctionNode.builder()
                .name("fullName")
                .returnType(Typed.typeLevelString())
                .addBodyStatement(Typed.returnStatement(Typed.string("Bob")))
                .build()
            )
            .build();
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package example.project;
                
                public record Example() {
                    public String fullName() {
                        return "Bob";
                    }
                }"""
        ));
    }

    @Test
    public void whenRecordHasSealedInterfaceAsSupertypeThenJavaRecordImplementsInterfaceIncludingAccept() {
        var node = TypedRecordNode.builder(NamespaceId.source("example", "project"), "Example")
            .build();
        var subtypeRelations = SubtypeRelations.EMPTY
            .addExtendedType(node.type(), Types.sealedInterfaceType(NamespaceId.source("a", "b"), "X"))
            .addExtendedType(node.type(), Types.sealedInterfaceType(NamespaceId.source("a", "b"), "Y"));
        var context = JavaCodeGeneratorContext.stub(subtypeRelations);

        var result = JavaCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package example.project;
                
                public record Example() implements a.b.X, a.b.Y {
                    public <T> T accept(a.b.X.Visitor<T> visitor) {
                        return visitor.visit(this);
                    }
                    public <T> T accept(a.b.Y.Visitor<T> visitor) {
                        return visitor.visit(this);
                    }
                }"""
        ));
    }

    @Test
    public void whenRecordHasUnsealedInterfaceAsSupertypeThenJavaRecordImplementsInterface() {
        var node = TypedRecordNode.builder(NamespaceId.source("example", "project"), "Example")
            .build();
        var subtypeRelations = SubtypeRelations.EMPTY
            .addExtendedType(node.type(), Types.unsealedInterfaceType(NamespaceId.source("a", "b"), "X"))
            .addExtendedType(node.type(), Types.unsealedInterfaceType(NamespaceId.source("c", "d"), "Y"));
        var context = JavaCodeGeneratorContext.stub(subtypeRelations);

        var result = JavaCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package example.project;
                
                public record Example() implements a.b.X, c.d.Y {
                }"""
        ));
    }
}
