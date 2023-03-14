package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedInstanceOfNode;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.has;
import static org.zwobble.precisely.Matchers.instanceOf;

public class ParserInstanceOfTests {
    @Test
    public void canParseInstanceOf() {
        var source = "x instanceof Y";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, instanceOf(
            UntypedInstanceOfNode.class,
            has("expression", x -> x.expression(), isUntypedReferenceNode("x")),
            has("typeExpression", x -> x.typeExpression(), isUntypedTypeLevelReferenceNode("Y"))
        ));
    }
}
