package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.errors.InternalCompilerError;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PythonTestNamesTests {
    @Test
    public void spacesAreConvertedToUnderscores() {
        var result = PythonTestNames.generateName("one two three");

        assertThat(result, equalTo("test_one_two_three"));
    }

    @Test
    public void nameIsConvertedToLowercase() {
        var result = PythonTestNames.generateName("One TWO thrEE");

        assertThat(result, equalTo("test_one_two_three"));
    }

    @Test
    public void doubleEqualsAreConvertedToEquals() {
        var result = PythonTestNames.generateName("one == two");

        assertThat(result, equalTo("test_one_equals_two"));
    }

    @Test
    public void whenNameIsNotValidIdentifierThenErrorIsThrown() {
        var result = assertThrows(
            InternalCompilerError.class,
            () -> PythonTestNames.generateName("☃")
        );

        assertThat(result.getMessage(), equalTo("Could not convert test name to Python identifier: ☃"));
    }
}
