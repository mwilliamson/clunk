package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckInterfaceTests {
    @Test
    public void canTypeCheckedEmptyInterface() {
        var untypedNode = Untyped.interface_("DocumentElement");

        var result = TypeChecker.typeCheckNamespaceStatement(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(result.typedNode(), has("name", equalTo("DocumentElement")));
    }
}
