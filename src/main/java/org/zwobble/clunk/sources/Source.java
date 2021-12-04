package org.zwobble.clunk.sources;

public interface Source {
    String describe();

    Source at(int characterIndex);
}
