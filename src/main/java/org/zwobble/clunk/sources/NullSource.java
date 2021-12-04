package org.zwobble.clunk.sources;

public class NullSource implements Source {
    private NullSource() {

    }

    public static final NullSource INSTANCE = new NullSource();

    @Override
    public String describe() {
        return "(null)";
    }

    @Override
    public Source at(int characterIndex) {
        return this;
    }
}
