package org.zwobble.clunk.types;

public class IndexableTypeSet implements TypeSet {
    public static final IndexableTypeSet INSTANCE = new IndexableTypeSet();

    private IndexableTypeSet() {
    }

    @Override
    public String describe() {
        return "indexable";
    }
}
