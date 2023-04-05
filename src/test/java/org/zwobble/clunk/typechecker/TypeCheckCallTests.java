package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedExpressionNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedCallNode;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.OptionalMatcher.present;
import static org.zwobble.precisely.Matchers.*;

public class TypeCheckCallTests {
    @Test
    public void canTypeCheckCallToMethodWithExplicitReceiver() {
        var untypedNode = Untyped.call(
            Untyped.memberAccess(
                Untyped.reference("x"),
                "y"
            ),
            List.of(Untyped.intLiteral(123))
        );
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "X");
        var context = TypeCheckerContext.stub()
            .addLocal("x", recordType, NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.of("y", Types.methodType(namespaceId, List.of(Types.INT), Types.INT)));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallMethodNode()
            .withReceiver(isTypedReferenceNode().withName("x").withType(recordType))
            .withMethodName("y")
            .withPositionalArgs(isSequence(isTypedIntLiteralNode(123)))
            .withType(Types.INT)
        );
    }

    @Test
    public void canTypeCheckCallToMethodWithImplicitReceiver() {
        var untypedNode = Untyped.call(
            Untyped.reference("y"),
            List.of(Untyped.intLiteral(123))
        );
        var namespaceId = NamespaceId.source("example");
        var context = TypeCheckerContext.stub()
            .enterRecordBody(Map.of("y", Types.methodType(namespaceId, List.of(Types.INT), Types.INT)));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallMethodNode()
            .withImplicitReceiver()
            .withMethodName("y")
            .withPositionalArgs(isSequence(isTypedIntLiteralNode(123)))
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
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "X");
        var typeParameter = TypeParameter.method(namespaceId, "X", "f", "T");
        var methodType = Types.methodType(namespaceId, List.of(typeParameter), List.of(typeParameter), typeParameter);
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
        var functionType = Types.staticFunctionType(
            NamespaceId.source("Stdlib", "Math"),
            "abs",
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("abs", functionType, NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallStaticFunctionNode()
            .withReceiver(isTypedReferenceNode().withName("abs").withType(functionType))
            .withPositionalArgs(isSequence(isTypedIntLiteralNode(123)))
            .withType(Types.INT)
        );
    }

    @Test
    public void canTypeCheckCallToNonGenericRecordConstructor() {
        var untypedNode = Untyped.call(
            Untyped.reference("Id"),
            List.of(Untyped.intLiteral(123))
        );
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "Id");
        var context = TypeCheckerContext.stub()
            .addLocal("Id", Types.metaType(recordType), NullSource.INSTANCE)
            .addConstructorType(Types.constructorType(List.of(Types.INT), recordType, Visibility.PUBLIC));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallConstructorNode()
            .withReceiver(isTypedReferenceNode().withName("Id").withType(Types.metaType(recordType)))
            .withPositionalArgs(isSequence(isTypedIntLiteralNode(123)))
            .withType(recordType)
        );
    }

    @Test
    public void canTypeCheckCallToGenericRecordConstructor() {
        var untypedNode = Untyped.call(
            Untyped.reference("Id"),
            List.of(Untyped.typeLevelReference("String")),
            List.of(Untyped.string("Hello."))
        );
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "Id");
        var typeParameter = TypeParameter.invariant(namespaceId, "Id", "T");
        var typeConstructor = new TypeConstructor(List.of(typeParameter), recordType);
        var context = TypeCheckerContext.stub()
            .addLocal("Id", Types.typeConstructorType(typeConstructor), NullSource.INSTANCE)
            .addConstructorType(Types.constructorType(List.of(typeParameter), List.of(typeParameter), recordType, Visibility.PUBLIC));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallConstructorNode()
            .withReceiver(isTypedReferenceNode()
                .withName("Id")
                .withType(Types.typeConstructorType(typeConstructor))
            )
            .withTypeArgs(present(isSequence(isTypedTypeLevelReferenceNode("String", Types.STRING))))
            .withPositionalArgs(isSequence(isTypedStringLiteralNode("Hello.")))
            .withType(Types.construct(typeConstructor, List.of(Types.STRING)))
        );
    }

    @Test
    public void positionalArgsAreTypeChecked() {
        var untypedNode = Untyped.call(
            Untyped.reference("abs"),
            List.of(Untyped.intLiteral(42))
        );
        var functionType = Types.staticFunctionType(
            NamespaceId.source("Stdlib", "Math"),
            "abs",
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("abs", functionType, NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallStaticFunctionNode()
            .withPositionalArgs(isSequence(isTypedIntLiteralNode(42))));
    }

    @Test
    public void whenPositionalArgIsWrongTypeThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.reference("abs"),
            List.of(Untyped.string("123"))
        );
        var functionType = Types.staticFunctionType(
            NamespaceId.source("Stdlib", "Math"),
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
        var functionType = Types.staticFunctionType(
            NamespaceId.source("Stdlib", "Math"),
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
        var functionType = Types.staticFunctionType(
            NamespaceId.source("Stdlib", "Math"),
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

    @Test
    public void namedArgsAreTypeChecked() {
        var untypedNode = UntypedCallNode.builder(Untyped.reference("abs"))
            .addNamedArg("x", Untyped.intLiteral(42))
            .build();
        var functionType = Types.staticFunctionType(
            NamespaceId.source("Stdlib", "Math"),
            "abs",
            List.of(),
            List.of(Types.namedParam("x", Types.INT)),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("abs", functionType, NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallStaticFunctionNode()
            .withNamedArgs(isSequence(
                isTypedNamedArgNode("x", isTypedIntLiteralNode(42))
            ))
        );
    }

    @Test
    public void whenNamedArgsAreNotInLexicographicalOrderThenErrorIsThrown() {
        var untypedNode = UntypedCallNode.builder(Untyped.reference("f"))
            .addNamedArg("y", Untyped.string("123"))
            .addNamedArg("x", Untyped.string("123"))
            .build();
        var functionType = Types.staticFunctionType(
            NamespaceId.source(),
            "f",
            List.of(),
            List.of(Types.namedParam("x", Types.STRING), Types.namedParam("y", Types.STRING)),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("f", functionType, NullSource.INSTANCE);

        assertThrows(
            NamedArgsNotInLexicographicalOrderError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );
    }

    @Test
    public void whenNamedArgIsWrongTypeThenErrorIsThrown() {
        var untypedNode = UntypedCallNode.builder(Untyped.reference("abs"))
            .addNamedArg("x", Untyped.string("123"))
            .build();
        var functionType = Types.staticFunctionType(
            NamespaceId.source("Stdlib", "Math"),
            "abs",
            List.of(),
            List.of(Types.namedParam("x", Types.INT)),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("abs", functionType, NullSource.INSTANCE);

        var error = assertThrows(UnexpectedTypeError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getExpected(), equalTo(IntType.INSTANCE));
        assertThat(error.getActual(), equalTo(StringType.INSTANCE));
    }

    @Test
    public void whenNamedArgIsMissingThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.reference("abs"),
            List.of()
        );
        var functionType = Types.staticFunctionType(
            NamespaceId.source("Stdlib", "Math"),
            "abs",
            List.of(),
            List.of(Types.namedParam("x", Types.INT)),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("abs", functionType, NullSource.INSTANCE);

        var error = assertThrows(
            NamedArgIsMissingError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(error.argName(), equalTo("x"));
    }

    @Test
    public void whenExtraNamedArgIsPassedThenErrorIsThrown() {
        var untypedNode = UntypedCallNode.builder(Untyped.reference("abs"))
            .addNamedArg("x", Untyped.intLiteral())
            .build();
        var functionType = Types.staticFunctionType(
            NamespaceId.source("Stdlib", "Math"),
            "abs",
            List.of(),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("abs", functionType, NullSource.INSTANCE);

        var error = assertThrows(
            ExtraNamedArgError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(error.argName(), equalTo("x"));
    }

    @Test
    public void whenDuplicateNamedArgIsPassedThenErrorIsThrown() {
        var untypedNode = UntypedCallNode.builder(Untyped.reference("abs"))
            .addNamedArg("x", Untyped.intLiteral())
            .addNamedArg("x", Untyped.intLiteral())
            .build();
        var functionType = Types.staticFunctionType(
            NamespaceId.source("Stdlib", "Math"),
            "abs",
            List.of(),
            List.of(Types.namedParam("x", Types.INT)),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("abs", functionType, NullSource.INSTANCE);

        var error = assertThrows(
            ExtraNamedArgError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(error.argName(), equalTo("x"));
    }

    @Test
    public void givenSignatureHasNoTypeParamsWhenTypeArgsArePassedThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.memberAccess(
                Untyped.reference("x"),
                "y"
            ),
            List.of(Untyped.typeLevelReference("String")),
            List.of()
        );
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "X");
        var context = TypeCheckerContext.stub()
            .addLocal("x", recordType, NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.of("y", Types.methodType(namespaceId, List.of(), Types.INT)));

        assertThrows(
            CannotPassTypeLevelArgsToNonGenericValueError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );
    }

    @Test
    public void givenSignatureHasTypeParamsWhenNoTypeArgsArePassedThenTypeArgsAreInferredFromPositionalArgs() {
        var untypedNode = UntypedCallNode
            .builder(Untyped.memberAccess(
                Untyped.reference("x"),
                "y"
            ))
            .addPositionalArg(Untyped.string())
            .build();
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "X");
        var typeParameter = TypeParameter.method(namespaceId, "X", "f", "T");
        var methodType = Types.methodType(namespaceId, List.of(typeParameter), List.of(typeParameter), typeParameter);
        var context = TypeCheckerContext.stub()
            .addLocal("x", recordType, NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.of("y", methodType));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallMethodNode()
            .withType(Types.STRING)
        );
    }

    @Test
    public void givenSignatureHasTypeParamsWhenNoTypeArgsArePassedThenTypeArgsAreInferredFromNamedArgs() {
        var untypedNode = UntypedCallNode
            .builder(Untyped.memberAccess(
                Untyped.reference("x"),
                "y"
            ))
            .addNamedArg("x", Untyped.string())
            .build();
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "X");
        var typeParameter = TypeParameter.method(namespaceId, "X", "f", "T");
        var methodType = Types.methodType(
            namespaceId,
            List.of(typeParameter),
            List.of(),
            List.of(Types.namedParam("x", typeParameter)),
            typeParameter
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", recordType, NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.of("y", methodType));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallMethodNode()
            .withType(Types.STRING)
        );
    }

    @Test
    public void inferredTypeArgsAreUsedInLaterArgs() {
        var untypedNode = UntypedCallNode
            .builder(Untyped.memberAccess(
                Untyped.reference("x"),
                "y"
            ))
            .addPositionalArg(Untyped.string())
            .addPositionalArg(Untyped.listLiteral(List.of()))
            .build();
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "X");
        var typeParameter = TypeParameter.method(namespaceId, "X", "f", "T");
        var methodType = Types.methodType(
            namespaceId,
            List.of(typeParameter),
            List.of(typeParameter, Types.list(typeParameter)),
            typeParameter
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", recordType, NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.of("y", methodType));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallMethodNode()
            .withPositionalArgs(isSequence(
                has("type", TypedExpressionNode::type, equalTo(Types.STRING)),
                has("type", TypedExpressionNode::type, equalTo(Types.list(Types.STRING)))
            ))
            .withType(Types.STRING)
        );
    }

    @Test
    public void possibleSpecialisationsOfTypeArgAreUnified() {
        var untypedNode = UntypedCallNode
            .builder(Untyped.memberAccess(
                Untyped.reference("x"),
                "y"
            ))
            .addPositionalArg(Untyped.string())
            .addPositionalArg(Untyped.intLiteral())
            .build();
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "X");
        var typeParameter = TypeParameter.method(namespaceId, "X", "f", "T");
        var methodType = Types.methodType(
            namespaceId,
            List.of(typeParameter),
            List.of(typeParameter, typeParameter),
            typeParameter
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", recordType, NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.of("y", methodType));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallMethodNode()
            .withPositionalArgs(isSequence(
                has("type", TypedExpressionNode::type, equalTo(Types.STRING)),
                has("type", TypedExpressionNode::type, equalTo(Types.INT))
            ))
            .withType(Types.OBJECT)
        );
    }

    @Test
    public void givenSignatureHasTypeParamsWhenNoTypeArgsArePassedAndTypeArgCannotBeInferredThenErrorIsThrown() {
        var untypedNode = UntypedCallNode
            .builder(Untyped.memberAccess(
                Untyped.reference("x"),
                "y"
            ))
            .build();
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "X");
        var typeParameter = TypeParameter.method(namespaceId, "X", "f", "T");
        var methodType = Types.methodType(namespaceId, List.of(typeParameter), List.of(), typeParameter);
        var context = TypeCheckerContext.stub()
            .addLocal("x", recordType, NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.of("y", methodType));

        assertThrows(
            MissingTypeLevelArgsError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );
    }

    @Test
    public void whenTypeArgIsMissingThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.memberAccess(
                Untyped.reference("x"),
                "y"
            ),
            List.of(Untyped.typeLevelReference("String")),
            List.of(Untyped.string())
        );
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "X");
        var typeParameter1 = TypeParameter.method(namespaceId, "X", "f", "T1");
        var typeParameter2 = TypeParameter.method(namespaceId, "X", "f", "T2");
        var methodType = Types.methodType(
            namespaceId,
            List.of(typeParameter1, typeParameter2),
            List.of(typeParameter1, typeParameter2),
            typeParameter1
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", recordType, NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.of("y", methodType));

        var error = assertThrows(
            WrongNumberOfTypeLevelArgsError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(error.getExpected(), equalTo(2));
        assertThat(error.getActual(), equalTo(1));
    }

    @Test
    public void whenExtraTypeArgIsPassedThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.memberAccess(
                Untyped.reference("x"),
                "y"
            ),
            List.of(Untyped.typeLevelReference("String"), Untyped.typeLevelReference("String")),
            List.of(Untyped.string())
        );
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "X");
        var typeParameter = TypeParameter.method(namespaceId, "X", "f", "T");
        var methodType = Types.methodType(namespaceId, List.of(typeParameter), List.of(typeParameter), typeParameter);
        var context = TypeCheckerContext.stub()
            .addLocal("x", recordType, NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.of("y", methodType));

        var error = assertThrows(
            WrongNumberOfTypeLevelArgsError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(error.getExpected(), equalTo(1));
        assertThat(error.getActual(), equalTo(2));
    }
}
