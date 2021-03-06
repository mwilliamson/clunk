package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.errors.InternalCompilerError;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JavaTestNamesTests {
    @Test
    public void spaceFollowedByLowerCaseLetterIsConvertedToCamelCase() {
        var result = JavaTestNames.generateName("can assign bool");

        assertThat(result, equalTo("canAssignBool"));
    }

    @Test
    public void spaceFollowedByUpperCaseLetterIsConvertedToCamelCase() {
        var result = JavaTestNames.generateName("can Assign Bool");

        assertThat(result, equalTo("canAssignBool"));
    }

    @Test
    public void spaceFollowedByNumberIsConvertedToCamelCase() {
        var result = JavaTestNames.generateName("increment 42");

        assertThat(result, equalTo("increment42"));
    }

    @Test
    public void doubleEqualsAreConvertedToEquals() {
        var result = JavaTestNames.generateName("one == two");

        assertThat(result, equalTo("oneEqualsTwo"));
    }

    @Test
    public void whenNameIsNotValidIdentifierThenErrorIsThrown() {
        var result = assertThrows(
            InternalCompilerError.class,
            () -> JavaTestNames.generateName("☃")
        );

        assertThat(result.getMessage(), equalTo("Could not convert test name to Java identifier: ☃"));
    }
}
