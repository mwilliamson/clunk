package org.zwobble.clunk.matchers;

import org.zwobble.precisely.MatchResult;
import org.zwobble.precisely.Matcher;

import java.util.Optional;

public class OptionalMatcher<T> implements Matcher<Optional<? extends T>> {
    public static <T> OptionalMatcher<T> present(Matcher<? super T> matcher) {
        return new OptionalMatcher<>(matcher);
    }

    private final Matcher<? super T> matcher;

    public OptionalMatcher(Matcher<? super T> matcher) {
        this.matcher = matcher;
    }

    @Override
    public MatchResult match(Optional<? extends T> actual) {
        if (null == actual) {
            return MatchResult.unmatched("was null");
        }

        if (actual.isEmpty()) {
            return MatchResult.unmatched("was empty");
        }

        return matcher.match(actual.get());
    }

    @Override
    public String describe() {
        return matcher.describe();
    }
}
