package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.typechecker.SubtypeLookup;
import org.zwobble.clunk.typechecker.SubtypeRelation;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorInterfaceTests {
    @Test
    public void sealedInterfaceIsCompiledToSealedInterface() {
        var interfaceType = Types.interfaceType(NamespaceName.fromParts("one", "two"), "X");
        var node = Typed.interface_("X", interfaceType);
        var subtypeLookup = SubtypeLookup.fromSubtypeRelations(List.of(
            new SubtypeRelation(new RecordType(NamespaceName.fromParts("one", "two"), "A"), interfaceType),
            new SubtypeRelation(new RecordType(NamespaceName.fromParts("one", "two"), "B"), interfaceType)
        ));
        var context = new JavaCodeGeneratorContext(JavaTargetConfig.stub(), subtypeLookup);

        var result = JavaCodeGenerator.compileInterface(
            node,
            context
        );

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package one.two;
                
                public sealed interface X permits one.two.A, one.two.B {
                }
                """
        ));
    }
}
