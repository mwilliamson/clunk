package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TypeScriptCodeGeneratorListComprehensionTests {
    @Test
    public void listComprehensionWithSingleForAndNoConditionsIsCompiledToMap() {
        var node = Typed.listComprehension(
            List.of(
                Typed.comprehensionForClause(
                    "x",
                    Types.INT,
                    Typed.localReference("xs", Types.list(Types.INT)),
                    List.of()
                )
            ),
            Typed.intAdd(Typed.localReference("x", Types.INT), Typed.intLiteral(1))
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.map((x) => x + 1)"));
    }

    @Test
    public void multipleForsAreCompiledToFlatMapsAndFinalMap() {
        var node = Typed.listComprehension(
            List.of(
                Typed.comprehensionForClause(
                    "xss",
                    Types.list(Types.list(Types.STRING)),
                    Typed.localReference("xsss", Types.list(Types.list(Types.list(Types.STRING)))),
                    List.of()
                ),
                Typed.comprehensionForClause(
                    "xs",
                    Types.list(Types.STRING),
                    Typed.localReference("xss", Types.list(Types.list(Types.STRING))),
                    List.of()
                ),
                Typed.comprehensionForClause(
                    "x",
                    Types.STRING,
                    Typed.localReference("xs", Types.list(Types.STRING)),
                    List.of()
                )
            ),
            Typed.localReference("x", Types.STRING)
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xsss.flatMap((xss) => xss.flatMap((xs) => xs.map((x) => x)))"));
    }

    @Test
    public void conditionsAreAddedAsFiltersOnIterables() {
        var node = Typed.listComprehension(
            List.of(
                Typed.comprehensionForClause(
                    "xs",
                    Types.list(Types.STRING),
                    Typed.localReference("xss", Types.list(Types.list(Types.STRING))),
                    List.of(
                        Typed.comprehensionIfClause(Typed.localReference("a", Types.BOOL)),
                        Typed.comprehensionIfClause(Typed.localReference("b", Types.BOOL))
                    )
                ),
                Typed.comprehensionForClause(
                    "x",
                    Types.STRING,
                    Typed.localReference("xs", Types.list(Types.STRING)),
                    List.of(
                        Typed.comprehensionIfClause(Typed.localReference("c", Types.BOOL))
                    )
                )
            ),
            Typed.localReference("x", Types.STRING)
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xss.filter((xs) => a).filter((xs) => b).flatMap((xs) => xs.filter((x) => c).map((x) => x))"));
    }

    @Test
    public void conditionWithTypeNarrowingIsAddedAsFlatMap() {
        var interfaceType = Types.sealedInterfaceType(NamespaceId.source("example"), "Node");
        var recordType = Types.recordType(NamespaceId.source("example"), "Add");
        var node = Typed.listComprehension(
            List.of(
                Typed.comprehensionForClause(
                    "x",
                    Types.STRING,
                    Typed.localReference("xs", Types.list(Types.STRING)),
                    List.of(
                        Typed.comprehensionIfClause(
                            Typed.instanceOf(
                                Typed.localReference("x", interfaceType),
                                Typed.typeLevelReference("Add", recordType)
                            ),
                            recordType
                        )
                    )
                )
            ),
            Typed.localReference("x", Types.STRING)
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.flatMap((x) => x.type === \"Add\" ? [x] : []).map((x) => x)"));
    }
}
