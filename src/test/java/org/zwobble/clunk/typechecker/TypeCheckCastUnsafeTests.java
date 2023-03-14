package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedCastUnsafeNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.Types;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.precisely.Matchers.instanceOf;
import static org.zwobble.precisely.Matchers.has;

public class TypeCheckCastUnsafeTests {
    @Test
    public void canCastToType() {
        var untypedNode = Untyped.castUnsafe(
            Untyped.reference("x"),
            Untyped.typeLevelReference("String")
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", Types.OBJECT, NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedCastUnsafeNode.class,
            has("expression", x -> x.expression(), isTypedReferenceNode().withName("x").withType(Types.OBJECT)),
            has("typeExpression", x -> x.typeExpression(), isTypedTypeLevelReferenceNode("String", Types.STRING)),
            has("type", x -> x.type(), equalTo(Types.STRING))
        ));
    }
}
