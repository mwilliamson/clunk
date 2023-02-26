package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.SubtypeRelations;
import org.zwobble.clunk.types.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorInterfaceTests {
    @Test
    public void sealedInterfaceIsCompiledToUnion() {
        var interfaceType = Types.sealedInterfaceType(NamespaceId.source("one", "two"), "X");
        var node = Typed.interface_("X", interfaceType);
        var subtypeLookup = SubtypeRelations.EMPTY
            .addSealedInterfaceCase(interfaceType, Types.recordType(NamespaceId.source("one", "two"), "A"))
            .addSealedInterfaceCase(interfaceType, Types.recordType(NamespaceId.source("one", "two"), "B"));
        var context = new TypeScriptCodeGeneratorContext(subtypeLookup);

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(
            node,
            context
        );

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
            type X = A | B;
            """
        ));
    }

    @Test
    public void unsealedInterfaceIsCompiledToInterface() {
        var interfaceType = Types.unsealedInterfaceType(NamespaceId.source("one", "two"), "X");
        var node = Typed.interface_("X", interfaceType);
        var subtypeLookup = SubtypeRelations.EMPTY;
        var context = new TypeScriptCodeGeneratorContext(subtypeLookup);

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(
            node,
            context
        );

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
            interface X {
            }
            """
        ));
    }
}
