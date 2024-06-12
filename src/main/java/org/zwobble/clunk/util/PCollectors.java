package org.zwobble.clunk.util;

import org.pcollections.Empty;
import org.pcollections.OrderedPMap;
import org.pcollections.PVector;

import java.util.Map;
import java.util.stream.Collector;

public class PCollectors {
    private PCollectors() {
    }

    public static <K, V> Collector<Map.Entry<K, V>, OrderedPMapCollector<K, V>, OrderedPMap<K, V>> toOrderedPMap() {
        return Collector.of(
            OrderedPMapCollector::new,
            OrderedPMapCollector::add,
            OrderedPMapCollector::addAll,
            OrderedPMapCollector::toOrderedMap
        );
    }

    private static class OrderedPMapCollector<K, V> {
        private OrderedPMap<K, V> map = OrderedPMap.empty();

        void add(Map.Entry<K, V> entry) {
            map = map.plus(entry.getKey(), entry.getValue());
        }

        OrderedPMapCollector<K, V> addAll(OrderedPMapCollector<K, V> other) {
            map = map.plusAll(other.map);
            return this;
        }

        OrderedPMap<K, V> toOrderedMap() {
            return map;
        }
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
