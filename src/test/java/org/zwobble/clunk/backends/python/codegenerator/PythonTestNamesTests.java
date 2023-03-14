package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.errors.InternalCompilerError;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PythonTestNamesTests {
    @Test
    public void spacesAreConvertedToUnderscores() {
        var result = PythonTestNames.generateTestFunctionName("one two three");

        assertThat(result, equalTo("test_one_two_three"));
    }

    @Test
    public void nameIsConvertedToLowercase() {
        var result = PythonTestNames.generateTestFunctionName("One TWO thrEE");

        assertThat(result, equalTo("test_one_two_three"));
    }

    @Test
    public void doubleEqualsAreConvertedToEquals() {
        var result = PythonTestNames.generateTestFunctionName("one == two");

        assertThat(result, equalTo("test_one_equals_two"));
    }

    @Test
    public void hyphenIsTreatedAsSpace() {
        var result = PythonTestNames.generateTestFunctionName("non-empty");

        assertThat(result, equalTo("test_non_empty"));
    }

    @Test
    public void whenNameIsNotValidIdentifierThenErrorIsThrown() {
        var result = assertThrows(
            InternalCompilerError.class,
            () -> PythonTestNames.generateTestFunctionName("☃")
        );

        assertThat(result.getMessage(), equalTo("Could not convert test name to Python identifier: ☃"));
    }
}
