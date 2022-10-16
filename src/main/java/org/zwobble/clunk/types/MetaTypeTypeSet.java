package org.zwobble.clunk.types;

public class MetaTypeTypeSet implements TypeSet {
    public static final MetaTypeTypeSet INSTANCE = new MetaTypeTypeSet();

    private MetaTypeTypeSet() {
    }

    @Override
    public String describe() {
        return "meta-type";
    }
}
