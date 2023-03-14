package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedSingleLineCommentNode;

import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class ParserSingleLineCommentTests {
    @Test
    public void canParseSingleLineCommentAsFunctionStatement() {
        var source = "// Beware.";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, instanceOf(
            UntypedSingleLineCommentNode.class,
            has("value", x -> x.value(), equalTo(" Beware."))
        ));
    }

    @Test
    public void canParseSingleLineCommentAsNamespaceStatement() {
        var source = "// Beware.";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, instanceOf(
            UntypedSingleLineCommentNode.class,
            has("value", x -> x.value(), equalTo(" Beware."))
        ));
    }

    @Test
    public void canParseSingleLineCommentAsRecordBodyDeclaration() {
        var source = "// Beware.";

        var node = parseString(source, Parser::parseRecordBodyDeclaration);

        assertThat(node, instanceOf(
            UntypedSingleLineCommentNode.class,
            has("value", x -> x.value(), equalTo(" Beware."))
        ));
    }
}
