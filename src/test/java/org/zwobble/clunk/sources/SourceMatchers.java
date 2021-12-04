package org.zwobble.clunk.sources;

import org.hamcrest.Matcher;

import static org.zwobble.clunk.matchers.CastMatcher.cast;

public class SourceMatchers {
    private SourceMatchers() {

    }

    public static Matcher<Source> isFileFragmentSource(Matcher<FileFragmentSource> matcher) {
        return cast(FileFragmentSource.class, matcher);
    }
}
