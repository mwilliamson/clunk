package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorSwitchTests {
    @Test
    public void switchStatementIsCompiledToTypeScriptSwitchStatement() {
        var interfaceType = Types.sealedInterfaceType(NamespaceName.fromParts(), "Node");
        var recordType = Types.recordType(NamespaceName.fromParts(), "Add");

        var node = Typed.switchStatement(
            Typed.localReference("node", interfaceType),
            List.of(
                Typed.switchCase(
                    Typed.typeLevelReference("Add", recordType),
                    List.of(Typed.returnStatement(Typed.localReference("node", recordType)))
                )
            )
        );

        var result = TypeScriptCodeGenerator.compileFunctionStatement(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatements);
        assertThat(string, equalTo(
            """
            switch (node.type) {
                case "Add":
                    return node;
            }
            """));
    }
}
