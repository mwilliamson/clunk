package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.SubtypeRelations;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorInterfaceTests {
    @Test
    public void sealedInterfaceIsCompiledToSealedInterface() {
        var interfaceType = Types.sealedInterfaceType(NamespaceName.fromParts("one", "two"), "X");
        var node = Typed.interface_("X", interfaceType);
        var subtypeLookup = SubtypeRelations.EMPTY
            .add(new RecordType(NamespaceName.fromParts("one", "two"), "A"), interfaceType)
            .add(new RecordType(NamespaceName.fromParts("one", "two"), "B"), interfaceType);
        var context = new JavaCodeGeneratorContext(JavaTargetConfig.stub(), subtypeLookup);

        var result = JavaCodeGenerator.compileInterface(
            node,
            context
        );

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package one.two;
                
                public sealed interface X permits A, B {
                }
                """
        ));
    }
}
