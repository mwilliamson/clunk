package org.zwobble.clunk.sources;

public record FileFragmentSource(
    String filename,
    String contents,
    int characterIndexStart,
    int characterIndexEnd
) implements Source {
    public static FileFragmentSource create(String filename, String contents) {
        return new FileFragmentSource(filename, contents, 0, contents.length());
    }

    @Override
    public String describe() {
        var lines = contents.split("\n");
        var position = 0;

        for (var lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            var line = lines[lineIndex];
            var nextLinePosition = position + line.length() + 1;
            if (nextLinePosition > characterIndexStart || nextLinePosition >= contents.length()) {
                return context(
                    line,
                    lineIndex,
                    characterIndexStart - position
                );
            }
            position = nextLinePosition;
        }
        throw new RuntimeException("should be impossible (but evidently isn't)");
    }

    @Override
    public Source at(int characterIndexStart, int characterIndexEnd) {
        return new FileFragmentSource(
            filename,
            contents,
            this.characterIndexStart + characterIndexStart,
            // TODO: check this is less than characterIndexEnd
            this.characterIndexStart + characterIndexEnd
        );
    }

    public Source end() {
        return at(contents.length(), contents.length());
    }

    private String context(String line, int lineIndex, int columnIndex) {
        var lineNumber = lineIndex + 1;
        var columnNumber = columnIndex + 1;
        return filename + ":" + lineNumber + ":" + columnNumber + "\n" + line + "\n" + " ".repeat(columnIndex) + "^";
    }
}
