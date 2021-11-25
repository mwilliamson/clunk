package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.python.ast.Python;
import org.zwobble.clunk.backends.python.ast.PythonClassDeclarationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.backends.python.serialiser.PythonSerialiser.serialiseClassDeclaration;

public class PythonSerialiserClassDeclarationTests {
    @Test
    public void emptyClass() {
        var node = PythonClassDeclarationNode.builder("Example").build();

        var builder = new CodeBuilder();
        serialiseClassDeclaration(node, builder);

        assertThat(builder.toString(), equalTo("""
            class Example:
                pass
            """));
    }

    @Test
    public void oneDecorator() {
        var node = PythonClassDeclarationNode.builder("Example")
            .addDecorator(Python.reference("dataclass"))
            .build();

        var builder = new CodeBuilder();
        serialiseClassDeclaration(node, builder);

        assertThat(builder.toString(), equalTo("""
            @dataclass
            class Example:
                pass
            """));
    }

    @Test
    public void manyDecorators() {
        var node = PythonClassDeclarationNode.builder("Example")
            .addDecorator(Python.reference("first"))
            .addDecorator(Python.reference("second"))
            .build();

        var builder = new CodeBuilder();
        serialiseClassDeclaration(node, builder);

        assertThat(builder.toString(), equalTo("""
            @first
            @second
            class Example:
                pass
            """));
    }

    @Test
    public void oneStatement() {
        var node = PythonClassDeclarationNode.builder("Example")
            .addStatement(Python.variableType("first", Python.reference("str")))
            .build();

        var builder = new CodeBuilder();
        serialiseClassDeclaration(node, builder);

        assertThat(builder.toString(), equalTo("""
            class Example:
                first: str
            """));
    }

    @Test
    public void manyStatements() {
        var node = PythonClassDeclarationNode.builder("Example")
            .addStatement(Python.variableType("first", Python.reference("str")))
            .addStatement(Python.variableType("second", Python.reference("int")))
            .build();

        var builder = new CodeBuilder();
        serialiseClassDeclaration(node, builder);

        assertThat(builder.toString(), equalTo("""
            class Example:
                first: str
                second: int
            """));
    }
}
