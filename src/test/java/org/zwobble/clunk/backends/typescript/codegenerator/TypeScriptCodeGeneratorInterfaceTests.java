package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.typechecker.SubtypeLookup;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorInterfaceTests {
    @Test
    public void sealedInterfaceIsCompiledToUnion() {
        var interfaceType = Types.interfaceType(NamespaceName.fromParts("one", "two"), "X");
        var node = Typed.interface_("X", interfaceType);
        var subtypeLookup = new SubtypeLookup(Map.of(
            interfaceType, List.of(
                new RecordType(NamespaceName.fromParts("one", "two"), "A"),
                new RecordType(NamespaceName.fromParts("one", "two"), "B")
            )
        ));
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
}
