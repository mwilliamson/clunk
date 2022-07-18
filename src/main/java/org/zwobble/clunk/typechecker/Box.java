package org.zwobble.clunk.typechecker;

import java.util.Optional;

public class Box<T> {
    private Optional<T> value;

    public Box() {
        this.value = Optional.empty();
    }

    public T get() {
        if (value.isPresent()) {
            return value.get();
        } else {
            throw new RuntimeException("box is empty");
        }
    }

    public void set(T value) {
        this.value = Optional.of(value);
    }
}
