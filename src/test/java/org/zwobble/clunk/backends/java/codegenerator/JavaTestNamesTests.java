package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.errors.InternalCompilerError;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JavaTestNamesTests {
    @Test
    public void spaceFollowedByLowerCaseLetterIsConvertedToCamelCase() {
        var result = JavaTestNames.generateTestName("can assign bool");

        assertThat(result, equalTo("canAssignBool"));
    }

    @Test
    public void spaceFollowedByUpperCaseLetterIsConvertedToCamelCase() {
        var result = JavaTestNames.generateTestName("can Assign Bool");

        assertThat(result, equalTo("canAssignBool"));
    }

    @Test
    public void spaceFollowedByNumberIsConvertedToCamelCase() {
        var result = JavaTestNames.generateTestName("increment 42");

        assertThat(result, equalTo("increment42"));
    }

    @Test
    public void doubleEqualsAreConvertedToEquals() {
        var result = JavaTestNames.generateTestName("one == two");

        assertThat(result, equalTo("oneEqualsTwo"));
    }

    @Test
    public void hyphenIsTreatedAsSpace() {
        var result = JavaTestNames.generateTestName("non-empty");

        assertThat(result, equalTo("nonEmpty"));
    }

    @Test
    public void whenNameIsNotValidIdentifierThenErrorIsThrown() {
        var result = assertThrows(
            InternalCompilerError.class,
            () -> JavaTestNames.generateTestName("☃")
        );

        assertThat(result.getMessage(), equalTo("Could not convert test name to Java identifier: ☃"));
    }
}
