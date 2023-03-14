package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedTypeLevelReferenceNode;

public class TypeCheckTypeLevelReferenceTests {
    @Test
    public void typeLevelReferenceToBoolHasBoolType() {
        var untypedNode = Untyped.typeLevelReference("Bool");

        var typedNode = TypeChecker.typeCheckTypeLevelReferenceNode(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(typedNode, isTypedTypeLevelReferenceNode("Bool", BoolType.INSTANCE));
    }

    @Test
    public void typeLevelReferenceToIntHasIntType() {
        var untypedNode = Untyped.typeLevelReference("Int");

        var typedNode = TypeChecker.typeCheckTypeLevelReferenceNode(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(typedNode, isTypedTypeLevelReferenceNode("Int", IntType.INSTANCE));
    }

    @Test
    public void typeLevelReferenceToStringHasStringType() {
        var untypedNode = Untyped.typeLevelReference("String");

        var typedNode = TypeChecker.typeCheckTypeLevelReferenceNode(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(typedNode, isTypedTypeLevelReferenceNode("String", StringType.INSTANCE));
    }

    @Test
    public void whenReferenceIsToNonTypeLevelValueThenErrorIsThrown() {
        var untypedNode = Untyped.typeLevelReference("X");
        var context = TypeCheckerContext.stub()
            .addLocal("X", Types.INT, NullSource.INSTANCE);

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckTypeLevelReferenceNode(
                untypedNode,
                context
            )
        );

        assertThat(result.getExpected(), equalTo(TypeLevelValueTypeSet.INSTANCE));
        assertThat(result.getActual(), equalTo(Types.INT));
    }
}
