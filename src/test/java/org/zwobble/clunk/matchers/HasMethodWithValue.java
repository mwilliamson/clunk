package org.zwobble.clunk.matchers;

import org.hamcrest.Condition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.lang.reflect.Method;

import static org.hamcrest.Condition.matched;
import static org.hamcrest.Condition.notMatched;
import static org.hamcrest.beans.PropertyUtil.NO_ARGUMENTS;

public class HasMethodWithValue<T> extends TypeSafeDiagnosingMatcher<T> {
    private final String methodName;
    private final Matcher<Object> valueMatcher;

    public HasMethodWithValue(String methodName, Matcher<?> valueMatcher) {
        this.methodName = methodName;
        this.valueMatcher = nastyGenericsWorkaround(valueMatcher);
    }

    @Override
    public boolean matchesSafely(T bean, Description mismatch) {
        return recordComponentOn(bean, mismatch)
            .and(withPropertyValue(bean))
            .matching(valueMatcher, methodName + "()");
    }

    private Condition.Step<Method, Object> withPropertyValue(final T bean) {
        return new Condition.Step<>() {
            @Override
            public Condition<Object> apply(Method readMethod, Description mismatch) {
                try {
                    return matched(readMethod.invoke(bean, NO_ARGUMENTS), mismatch);
                } catch (Exception e) {
                    mismatch.appendText(e.getMessage());
                    return notMatched();
                }
            }
        };
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has(").appendValue(methodName).appendText(", ")
            .appendDescriptionOf(valueMatcher).appendText(")");
    }

    private Condition<Method> recordComponentOn(T actual, Description mismatch) {
        try {
            return matched(actual.getClass().getMethod(methodName), mismatch);
        } catch (NoSuchMethodException e) {
            mismatch.appendText("No method \"" + methodName + "\"");
            return notMatched();
        }
    }

    @SuppressWarnings("unchecked")
    private static Matcher<Object> nastyGenericsWorkaround(Matcher<?> valueMatcher) {
        return (Matcher<Object>) valueMatcher;
    }

    public static <T> Matcher<T> has(String methodName, Matcher<?> valueMatcher) {
        return new HasMethodWithValue<T>(methodName, valueMatcher);
    }
}
