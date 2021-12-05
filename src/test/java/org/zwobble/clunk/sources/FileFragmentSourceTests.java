package org.zwobble.clunk.sources;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FileFragmentSourceTests {
    private record TestCase(
        String name,
        String contents,
        int characterIndexStart,
        String expectedContext
    ) {
        public int characterIndexEnd() {
            return characterIndexStart;
        }
    }

    @TestFactory
    public List<DynamicTest> sourceContextIsSourceLineWithPointer() {
        return Stream.of(
            new TestCase(
                "one line, first character",
                "abcd",
                0,
                """
                    <filename>:1:1
                    abcd
                    ^"""
            ),
            new TestCase(
                "one line, last character",
                "abcd",
                3,
                """
                    <filename>:1:4
                    abcd
                       ^"""
            ),
            new TestCase(
                "one line, end",
                "abcd",
                4,
                """
                    <filename>:1:5
                    abcd
                        ^"""
            ),
            new TestCase(
                "many lines, end",
                """
                    abc
                    def""",
                7,
                """
                    <filename>:2:4
                    def
                       ^"""
            ),
            new TestCase(
                "first line, first character",
                """
                    abc
                    def""",
                0,
                """
                    <filename>:1:1
                    abc
                    ^"""
            ),
            new TestCase(
                "first line, last character",
                """
                    abc
                    def""",
                2,
                """
                    <filename>:1:3
                    abc
                      ^"""
            ),
            new TestCase(
                "last line, first character",
                """
                    abc
                    def""",
                4,
                """
                    <filename>:2:1
                    def
                    ^"""
            )
        ).map(testCase -> DynamicTest.dynamicTest(testCase.name, () -> {
            var source = new FileFragmentSource(
                "<filename>",
                testCase.contents,
                testCase.characterIndexStart,
                testCase.characterIndexEnd()
            );

            assertThat(source.describe(), equalTo(testCase.expectedContext));
        })).collect(Collectors.toList());
    }
}
