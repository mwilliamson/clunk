package org.zwobble.clunk.matchers;

import org.hamcrest.Condition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.function.Function;

import static org.hamcrest.Condition.matched;
import static org.hamcrest.Condition.notMatched;

public class HasMatcher<T, R> extends TypeSafeDiagnosingMatcher<T> {
    public static <T, R> Matcher<T> has(String name, Function<T, R> get, Matcher<? super R> valueMatcher) {
        return new HasMatcher<>(name, get, valueMatcher);
    }

    private final String name;
    private final Function<T, R> get;
    private final Matcher<? super R> valueMatcher;

    public HasMatcher(String name, Function<T, R> get, Matcher<? super R> valueMatcher) {
        this.name = name;
        this.get = get;
        this.valueMatcher = valueMatcher;
    }

    @Override
    public boolean matchesSafely(T item, Description mismatch) {
        return withValue(item, mismatch)
            .matching((Matcher<R>)valueMatcher, name + ": ");
    }

    private Condition<R> withValue(T item, Description mismatch) {
        try {
            var value = get.apply(item);
            return matched(value, mismatch);
        } catch (Exception e) {
            mismatch.appendText(e.getMessage());
            return notMatched();
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has(").appendValue(name).appendText(", ")
            .appendDescriptionOf(valueMatcher).appendText(")");
    }

    public static <T> Matcher<T> has(String methodName, Matcher<?> valueMatcher) {
        return new HasMethodWithValue<T>(methodName, valueMatcher);
    }
}
