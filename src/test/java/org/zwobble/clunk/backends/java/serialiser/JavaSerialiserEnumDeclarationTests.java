package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.JavaEnumDeclarationNode;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserEnumDeclarationTests {
    @Test
    public void enumWithNoMembers() {
        var node = new JavaEnumDeclarationNode("NoteType", List.of());

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo(
            """
            public enum NoteType {
            }"""
        ));
    }

    @Test
    public void enumWithOneMember() {
        var node = new JavaEnumDeclarationNode("NoteType", List.of("FOOTNOTE"));

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo(
            """
            public enum NoteType {
                FOOTNOTE
            }"""
        ));
    }

    @Test
    public void enumWithMultipleMembers() {
        var node = new JavaEnumDeclarationNode("NoteType", List.of("FOOTNOTE", "ENDNOTE"));

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo(
            """
            public enum NoteType {
                FOOTNOTE,
                ENDNOTE
            }"""
        ));
    }
}
