package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class JavaCodeGeneratorListComprehensionTests {
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

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.stream().map((x) -> x + 1).toList()"));
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
            Typed.intAdd(Typed.localReference("x", Types.STRING), Typed.intLiteral(1))
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xsss.stream().flatMap((xss) -> xss.stream().flatMap((xs) -> xs.stream().map((x) -> x + 1))).toList()"));
    }

    @Test
    public void whenFinalMapIsIdentityThenFinalMapIsSkipped() {
        var node = Typed.listComprehension(
            List.of(
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

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xss.stream().flatMap((xs) -> xs.stream()).toList()"));
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

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xss.stream().filter((xs) -> a).filter((xs) -> b).flatMap((xs) -> xs.stream().filter((x) -> c)).toList()"));
    }

    @Test
    public void conditionWithTypeNarrowingIsAddedAsFlatMap() {
        // TODO: consider something better than a flat map?
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

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.stream().flatMap((x) -> x instanceof Add ? java.util.stream.Stream.of((example.Add) x) : null).toList()"));
    }
}
