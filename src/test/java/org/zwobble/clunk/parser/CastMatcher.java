package org.zwobble.clunk.parser;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;

public class CastMatcher<TActual, TExpected> extends DiagnosingMatcher<TActual> {
    private final Class<TExpected> expectedClass;
    private final Matcher<TExpected> matcher;

    public CastMatcher(Class<TExpected> expectedClass, Matcher<TExpected> matcher) {
        this.expectedClass = expectedClass;
        this.matcher = matcher;
    }

    @Override
    protected boolean matches(Object item, Description mismatch) {
        if (null == item) {
            mismatch.appendText("null");
            return false;
        }

        if (!expectedClass.isInstance(item)) {
            mismatch.appendValue(item).appendText(" is a " + item.getClass().getName());
            return false;
        }

        if (!matcher.matches(item)) {
            matcher.describeMismatch(item, mismatch);
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("an instance of ").appendText(expectedClass.getName());
    }

    public static <TActual, TExpected> Matcher<TActual> cast(Class<TExpected> type, Matcher<TExpected> matcher) {
        return new CastMatcher<TActual, TExpected>(type, matcher);
    }
}
