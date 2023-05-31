package org.zwobble.clunk.util;

import org.pcollections.Empty;
import org.pcollections.PVector;

import java.util.stream.Collector;

public class PCollectors {
    private PCollectors() {
    }

    public static <T> Collector<T, PVectorCollector<T>, PVector<T>> toVector() {
        return Collector.of(
            PVectorCollector::new,
            PVectorCollector::add,
            PVectorCollector::addAll,
            PVectorCollector::toVector
        );
    }

    private static class PVectorCollector<T> {
        private PVector<T> vector = Empty.vector();

        void add(T other) {
            vector = vector.plus(other);
        }

        PVectorCollector<T> addAll(PVectorCollector<T> other) {
            vector = vector.plusAll(other.vector);
            return this;
        }

        PVector<T> toVector() {
            return vector;
        }
    }
}
