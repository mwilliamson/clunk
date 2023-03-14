package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedPropertyNode;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class ParserRecordTests {
    @Test
    public void canParseRecordName() {
        var source = "record User(name: String)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withName("User"));
    }

    @Test
    public void canParseSingleField() {
        var source = "record User(name: String)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withFields(isSequence(
            allOf(
                isUntypedRecordFieldNode().withName("name"),
                isUntypedRecordFieldNode().withType(isUntypedTypeLevelReferenceNode("String"))
            )
        )));
    }

    @Test
    public void canParseMultipleFields() {
        var source = "record User(name: String, emailAddress: String)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withFields(isSequence(
            isUntypedRecordFieldNode().withName("name"),
            isUntypedRecordFieldNode().withName("emailAddress")
        )));
    }

    @Test
    public void fieldsCanHaveTrailingComma() {
        var source = "record User(name: String, emailAddress: String,)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withFields(isSequence(
            isUntypedRecordFieldNode().withName("name"),
            isUntypedRecordFieldNode().withName("emailAddress")
        )));
    }

    @Test
    public void whenSupertypesAreNotSpecifiedThenSupertypesIsEmpty() {
        var source = "record User()";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withSupertypes(isSequence()));
    }

    @Test
    public void canParseSingleSupertype() {
        var source = "record User() <: Person";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withSupertypes(isSequence(isUntypedTypeLevelReferenceNode("Person"))));
    }

    @Test
    public void canParseMultipleSupertypes() {
        var source = "record User() <: Person, Principal";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withSupertypes(isSequence(
            isUntypedTypeLevelReferenceNode("Person"),
            isUntypedTypeLevelReferenceNode("Principal")
        )));
    }

    @Test
    public void canParseSingleMethod() {
        var source = """
            record User() {
                fun active() -> Bool {
                    return true;
                }
            }
            """;

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withBody(isSequence(
            isUntypedFunctionNode()
                .withName("active")
                .withReturnType(isUntypedTypeLevelReferenceNode("Bool"))
                .withBody(isSequence(isUntypedReturnNode().withExpression(isUntypedBoolLiteralNode(true))))
        )));
    }

    @Test
    public void canParseSingleProperty() {
        var source = """
            record User() {
                property active: Bool {
                    return true;
                }
            }
            """;

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withBody(isSequence(
            instanceOf(
                UntypedPropertyNode.class,
                has("name", x -> x.name(), equalTo("active")),
                has("type", x -> x.type(), isUntypedTypeLevelReferenceNode("Bool")),
                has("body", x -> x.body(), isSequence(isUntypedReturnNode().withExpression(isUntypedBoolLiteralNode(true))))
            )
        )));
    }

    @Test
    public void canParseBlankLines() {
        var source = """
            record User() {
                property active: Bool {
                }
                
                property isAdmin: Bool {
                }
            }
            """;

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode()
            .withBody(isSequence(
                instanceOf(
                    UntypedPropertyNode.class,
                    has("name", x -> x.name(), equalTo("active"))
                ),
                isUntypedBlankLineNode(),
                instanceOf(
                    UntypedPropertyNode.class,
                    has("name", x -> x.name(), equalTo("isAdmin"))
                )
        )));
    }
}
