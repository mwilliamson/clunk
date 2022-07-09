package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.TypedRecordFieldNode;
import org.zwobble.clunk.types.RecordType;

import java.util.List;
import java.util.Map;

public class FieldsLookup {
    public static final FieldsLookup EMPTY = new FieldsLookup(Map.of());

    private final Map<RecordType, List<TypedRecordFieldNode>> typeToFields;

    public FieldsLookup(Map<RecordType, List<TypedRecordFieldNode>> typeToFields) {
        this.typeToFields = typeToFields;
    }

    public List<TypedRecordFieldNode> fieldsOf(RecordType recordType) {
        return typeToFields.get(recordType);
    }
}
