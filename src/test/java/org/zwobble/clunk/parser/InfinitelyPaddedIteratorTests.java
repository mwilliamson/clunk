package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class InfinitelyPaddedIteratorTests {
    @Test
    public void iteratesThroughListThenAlwaysReturnsEnd() {
        var tokens = new InfinitelyPaddedIterator<>(
            List.of("one", "two", "three"),
            "end"
        );

        assertThat(tokens.get(), equalTo("one"));
        tokens.moveNext();
        assertThat(tokens.get(), equalTo("two"));
        tokens.moveNext();
        assertThat(tokens.get(), equalTo("three"));
        tokens.moveNext();
        assertThat(tokens.get(), equalTo("end"));
        tokens.moveNext();
        assertThat(tokens.get(), equalTo("end"));
    }
}
