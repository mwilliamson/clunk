package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;

public class TypeCheckCallTests {
    @Test
    public void canTypeCheckCallToMethodWithoutTypeParams() {
        var untypedNode = Untyped.call(
            Untyped.memberAccess(
                Untyped.reference("x"),
                "y"
            ),
            List.of(Untyped.intLiteral(123))
        );
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "X");
        var context = TypeCheckerContext.stub()
            .addLocal("x", recordType, NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.of("y", Types.methodType(List.of(Types.INT), Types.INT)));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallMethodNode()
            .withReceiver(isTypedReferenceNode().withName("x").withType(recordType))
            .withMethodName("y")
            .withPositionalArgs(contains(isTypedIntLiteralNode(123)))
            .withType(Types.INT)
        );
    }
    @Test
    public void canTypeCheckCallWithExplicitTypeArgsToMethodWithTypeParams() {
        var untypedNode = Untyped.call(
            Untyped.memberAccess(
                Untyped.reference("x"),
                "y"
            ),
            List.of(Untyped.typeLevelReference("String")),
            List.of(Untyped.string())
        );
        var namespaceName = NamespaceName.fromParts("example");
        var recordType = Types.recordType(namespaceName, "X");
        var typeParameter = TypeParameter.function(namespaceName, "X", "f", "T");
        var methodType = Types.methodType(List.of(typeParameter), List.of(typeParameter), typeParameter);
        var context = TypeCheckerContext.stub()
            .addLocal("x", recordType, NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.of("y", methodType));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallMethodNode()
            .withType(Types.STRING)
        );
    }

    @Test
    public void canTypeCheckCallToStaticFunction() {
        var untypedNode = Untyped.call(
            Untyped.reference("abs"),
            List.of(Untyped.intLiteral(123))
        );
        var functionType = new StaticFunctionType(
            NamespaceName.fromParts("Stdlib", "Math"),
            "abs",
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("abs", functionType, NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallStaticFunctionNode()
            .withReceiver(isTypedReferenceNode().withName("abs").withType(functionType))
            .withPositionalArgs(contains(isTypedIntLiteralNode(123)))
            .withType(Types.INT)
        );
    }

    @Test
    public void canTypeCheckCallToRecordConstructor() {
        var untypedNode = Untyped.call(
            Untyped.reference("Id"),
            List.of(Untyped.intLiteral(123))
        );
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "Id");
        var context = TypeCheckerContext.stub()
            .addLocal("Id", Types.metaType(recordType), NullSource.INSTANCE)
            .addFields(recordType, List.of(Typed.recordField("value", Typed.typeLevelInt())));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallConstructorNode()
            .withReceiver(isTypedReferenceNode().withName("Id").withType(Types.metaType(recordType)))
            .withPositionalArgs(contains(isTypedIntLiteralNode(123)))
            .withType(recordType)
        );
    }

    @Test
    public void whenPositionalArgIsWrongTypeThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.reference("abs"),
            List.of(Untyped.string("123"))
        );
        var functionType = new StaticFunctionType(
            NamespaceName.fromParts("Stdlib", "Math"),
            "abs",
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("abs", functionType, NullSource.INSTANCE);

        var error = assertThrows(UnexpectedTypeError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getExpected(), equalTo(IntType.INSTANCE));
        assertThat(error.getActual(), equalTo(StringType.INSTANCE));
    }

    @Test
    public void whenPositionalArgIsMissingThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.reference("abs"),
            List.of()
        );
        var functionType = new StaticFunctionType(
            NamespaceName.fromParts("Stdlib", "Math"),
            "abs",
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("abs", functionType, NullSource.INSTANCE);

        var error = assertThrows(WrongNumberOfArgumentsError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getExpected(), equalTo(1));
        assertThat(error.getActual(), equalTo(0));
    }

    @Test
    public void whenExtraPositionalArgIsPassedThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.reference("abs"),
            List.of(Untyped.intLiteral(123), Untyped.intLiteral(456))
        );
        var functionType = new StaticFunctionType(
            NamespaceName.fromParts("Stdlib", "Math"),
            "abs",
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("abs", functionType, NullSource.INSTANCE);

        var error = assertThrows(WrongNumberOfArgumentsError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getExpected(), equalTo(1));
        assertThat(error.getActual(), equalTo(2));
    }
}
