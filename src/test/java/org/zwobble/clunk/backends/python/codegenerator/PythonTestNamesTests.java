package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PythonTestNamesTests {
    @Test
    public void spacesAreConvertedToUnderscores() {
        var result = PythonTestNames.generateName("one two three");

        assertThat(result, equalTo("one_two_three"));
    }
}
