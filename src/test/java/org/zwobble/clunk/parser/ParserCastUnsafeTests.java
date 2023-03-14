package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedCastUnsafeNode;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.has;
import static org.zwobble.precisely.Matchers.instanceOf;

public class ParserCastUnsafeTests {
    @Test
    public void canParseCastUnsafe() {
        var source = "x as String";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, instanceOf(
            UntypedCastUnsafeNode.class,
            has("expression", x -> x.expression(), isUntypedReferenceNode("x")),
            has("typeExpression", x -> x.typeExpression(), isUntypedTypeLevelReferenceNode("String"))
        ));
    }
}
