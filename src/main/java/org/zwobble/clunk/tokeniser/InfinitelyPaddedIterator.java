package org.zwobble.clunk.tokeniser;

import java.util.List;

public class InfinitelyPaddedIterator<T> {
    private final List<T> elements;
    private final T end;
    private int index = 0;

    public InfinitelyPaddedIterator(List<T> elements, T end) {
        this.elements = elements;
        this.end = end;
    }

    public T get() {
        if (index < elements.size()) {
            return elements.get(index);
        } else {
            return end;
        }
    }

    public void moveNext() {
        index++;
    }
}
