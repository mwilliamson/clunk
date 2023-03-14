package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedPropertyNode;
import org.zwobble.clunk.ast.typed.TypedRecordBodyDeclarationNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedRecordBodyDeclarationNode;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.typechecker.TypeChecker.typeCheckRecordBodyDeclaration;
import static org.zwobble.precisely.Matchers.isSequence;

public class TypeCheckPropertyTests {
    @Test
    public void givenPropertyHasUnitTypeWhenBodyDoesNotReturnThenFunctionTypeChecks() {
        var untypedNode = Untyped.property(
            "x",
            Untyped.typeLevelReference("Unit"),
            List.of()
        );

        var result = typeCheckRecordBodyDeclarationAllPhases(untypedNode, TypeCheckerContext.stub());

        var typedNode = (TypedPropertyNode) result;
        assertThat(typedNode.body(), isSequence());
    }

    @Test
    public void givenPropertyHasNonUnitTypeWhenBodyDoesNotReturnThenErrorIsThrown() {
        var untypedNode = Untyped.property(
            "x",
            Untyped.typeLevelReference("Bool"),
            List.of()
        );

        assertThrows(MissingReturnError.class, () -> typeCheckRecordBodyDeclarationAllPhases(untypedNode, TypeCheckerContext.stub()));
    }

    private TypedRecordBodyDeclarationNode typeCheckRecordBodyDeclarationAllPhases(
        UntypedRecordBodyDeclarationNode node,
        TypeCheckerContext context
    ) {
        var result = typeCheckRecordBodyDeclaration(node, context);
        return result.value(context);
    }
}
