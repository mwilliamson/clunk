package org.zwobble.clunk.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RegexTokeniser<T> {
    public static <T> TokenRule<T> rule(T type, String regex) {
        var pattern = Pattern.compile(regex);

        if (pattern.matcher("").groupCount() != 0) {
            throw new RuntimeException("regex cannot contain any groups");
        }

        return new TokenRule<T>(type, pattern);
    }

    static class TokenRule<T> {
        private final T type;
        private final Pattern regex;

        private TokenRule(T type, Pattern regex) {
            this.type = type;
            this.regex = regex;
        }
    }

    private final Pattern pattern;
    private final List<T> rules;

    public RegexTokeniser(T unknown, List<TokenRule<T>> rules) {
        var allRules = new ArrayList<>(rules);
        allRules.add(rule(unknown, "."));

        this.pattern = Pattern.compile(
            allRules.stream()
                .map(rule -> "(" + rule.regex.pattern() + ")")
                .collect(Collectors.joining("|"))
        );

        this.rules = allRules.stream()
            .map(rule -> rule.type)
            .collect(Collectors.toList());
    }

    public List<Token<T>> tokenise(String value) {
        var matcher = pattern.matcher(value);
        var tokens = new ArrayList<Token<T>>();

        while (matcher.lookingAt()) {
            var groupIndex = IntStream.rangeClosed(1, this.rules.size())
                .filter(index -> matcher.group(index) != null)
                .findFirst()
                .getAsInt();

            var tokenType = this.rules.get(groupIndex - 1);
            tokens.add(new Token<>(matcher.regionStart(), tokenType, matcher.group()));
            matcher.region(matcher.end(), value.length());
        }

        return tokens;
    }
}
