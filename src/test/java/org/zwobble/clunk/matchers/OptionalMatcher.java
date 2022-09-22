package org.zwobble.clunk.matchers;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;

import java.util.Optional;

public class OptionalMatcher<T> extends DiagnosingMatcher<Optional<T>> {
    public static <T> OptionalMatcher<T> present(Matcher<T> matcher) {
        return new OptionalMatcher<>(matcher);
    }

    private final Matcher<T> matcher;

    public OptionalMatcher(Matcher<T> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean matches(Object item, Description mismatch) {
        if (null == item) {
            mismatch.appendText("null");
            return false;
        }

        if (!(item instanceof Optional<?> optional)) {
            mismatch.appendValue(item).appendText(" is a " + item.getClass().getName());
            return false;
        }

        if (optional.isEmpty()) {
            mismatch.appendText("is empty");
            return false;
        }

        var elementMatches = matcher.matches(optional.get());

        if (!elementMatches) {
            matcher.describeMismatch(optional.get(), mismatch);
        }

        return elementMatches;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("optional of ").appendDescriptionOf(matcher);
    }
}
