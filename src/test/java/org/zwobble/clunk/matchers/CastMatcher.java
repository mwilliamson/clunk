package org.zwobble.clunk.matchers;

import org.zwobble.precisely.MatchResult;
import org.zwobble.precisely.Matcher;

import java.util.List;

import static org.zwobble.precisely.Matchers.instanceOf;

public class CastMatcher<TActual, TExpected> implements Matcher<TActual> {
    private final Matcher<TActual> matcher;

    public CastMatcher(Class<TExpected> expectedClass, List<Matcher<? super TExpected>> matchers) {
        this.matcher = instanceOf(expectedClass, matchers);
    }

    @Override
    public MatchResult match(TActual actual) {
        return matcher.match(actual);
    }

    @Override
    public String describe() {
        return matcher.describe();
    }
}
