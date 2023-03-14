package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedMapLiteralNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.precisely.Matchers.instanceOf;
import static org.zwobble.precisely.Matchers.has;

public class TypeCheckMapLiteralTests {
    @Test
    public void emptyMapHasNothingKeyTypeAndNothingValueType() {
        var untypedNode = Untyped.mapLiteral(List.of());
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedMapLiteralNode.class,
            has("entries", x -> x.entries(), isSequence()),
            has("keyType", x -> x.keyType(), equalTo(Types.NOTHING)),
            has("valueType", x -> x.valueType(), equalTo(Types.NOTHING))
        ));
    }

    @Test
    public void singletonMapUsesTypeOfEntry() {
        var untypedNode = Untyped.mapLiteral(List.of(
            Untyped.mapEntryLiteral(Untyped.string("a"), Untyped.intLiteral(42))
        ));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedMapLiteralNode.class,
            has("entries", x -> x.entries(), isSequence(
                isTypedMapEntryLiteralNode(isTypedStringLiteralNode("a"), isTypedIntLiteralNode(42))
            )),
            has("keyType", x -> x.keyType(), equalTo(Types.STRING)),
            has("valueType", x -> x.valueType(), equalTo(Types.INT))
        ));
    }

    @Test
    public void whenEntriesAreTheSameTypeThenMapUsesEntryType() {
        var untypedNode = Untyped.mapLiteral(List.of(
            Untyped.mapEntryLiteral(Untyped.string("a"), Untyped.intLiteral(42)),
            Untyped.mapEntryLiteral(Untyped.string("b"), Untyped.intLiteral(47))
        ));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedMapLiteralNode.class,
            has("entries", x -> x.entries(), isSequence(
                isTypedMapEntryLiteralNode(isTypedStringLiteralNode("a"), isTypedIntLiteralNode(42)),
                isTypedMapEntryLiteralNode(isTypedStringLiteralNode("b"), isTypedIntLiteralNode(47))
            )),
            has("keyType", x -> x.keyType(), equalTo(Types.STRING)),
            has("valueType", x -> x.valueType(), equalTo(Types.INT))
        ));
    }
}
