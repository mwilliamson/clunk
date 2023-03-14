package org.zwobble.clunk.sources;

import org.zwobble.precisely.Matcher;

import static org.zwobble.precisely.Matchers.instanceOf;

public class SourceMatchers {
    private SourceMatchers() {

    }

    public static Matcher<Source> isFileFragmentSource(Matcher<FileFragmentSource> matcher) {
        return instanceOf(FileFragmentSource.class, matcher);
    }
}
