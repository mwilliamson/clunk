package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedPropertyNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

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

        assertThat(node, isUntypedRecordNode().withFields(contains(
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

        assertThat(node, isUntypedRecordNode().withFields(contains(
            isUntypedRecordFieldNode().withName("name"),
            isUntypedRecordFieldNode().withName("emailAddress")
        )));
    }

    @Test
    public void fieldsCanHaveTrailingComma() {
        var source = "record User(name: String, emailAddress: String,)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withFields(contains(
            isUntypedRecordFieldNode().withName("name"),
            isUntypedRecordFieldNode().withName("emailAddress")
        )));
    }

    @Test
    public void whenSupertypesAreNotSpecifiedThenSupertypesIsEmpty() {
        var source = "record User()";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withSupertypes(empty()));
    }

    @Test
    public void canParseSingleSupertype() {
        var source = "record User() <: Person";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withSupertypes(contains(isUntypedTypeLevelReferenceNode("Person"))));
    }

    @Test
    public void canParseMultipleSupertypes() {
        var source = "record User() <: Person, Principal";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withSupertypes(contains(
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

        assertThat(node, isUntypedRecordNode().withBody(contains(
            isUntypedFunctionNode()
                .withName("active")
                .withReturnType(isUntypedTypeLevelReferenceNode("Bool"))
                .withBody(contains(isUntypedReturnNode().withExpression(isUntypedBoolLiteralNode(true))))
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

        assertThat(node, isUntypedRecordNode().withBody(contains(
            cast(
                UntypedPropertyNode.class,
                has("name", equalTo("active")),
                has("type", isUntypedTypeLevelReferenceNode("Bool")),
                has("body", contains(isUntypedReturnNode().withExpression(isUntypedBoolLiteralNode(true))))
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
            .withBody(contains(
                cast(
                    UntypedPropertyNode.class,
                    has("name", equalTo("active"))
                ),
                isUntypedBlankLineNode(),
                cast(
                    UntypedPropertyNode.class,
                    has("name", equalTo("isAdmin"))
                )
        )));
    }
}
