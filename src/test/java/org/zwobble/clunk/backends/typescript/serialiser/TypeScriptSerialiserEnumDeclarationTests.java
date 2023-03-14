package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptEnumDeclarationNode;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserEnumDeclarationTests {
    @Test
    public void enumWithNoMembers() {
        var node = new TypeScriptEnumDeclarationNode("NoteType", List.of());

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            enum NoteType {
            }
            """
        ));
    }

    @Test
    public void enumWithOneMember() {
        var node = new TypeScriptEnumDeclarationNode("NoteType", List.of("FOOTNOTE"));

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            enum NoteType {
                FOOTNOTE,
            }
            """
        ));
    }

    @Test
    public void enumWithMultipleMembers() {
        var node = new TypeScriptEnumDeclarationNode("NoteType", List.of("FOOTNOTE", "ENDNOTE"));

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            enum NoteType {
                FOOTNOTE,
                ENDNOTE,
            }
            """
        ));
    }
}
