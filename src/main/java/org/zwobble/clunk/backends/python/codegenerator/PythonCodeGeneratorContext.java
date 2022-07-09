package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.ast.typed.TypedRecordFieldNode;
import org.zwobble.clunk.backends.python.ast.PythonImportFromNode;
import org.zwobble.clunk.backends.python.ast.PythonImportNode;
import org.zwobble.clunk.backends.python.ast.PythonStatementNode;
import org.zwobble.clunk.typechecker.FieldsLookup;
import org.zwobble.clunk.types.RecordType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PythonCodeGeneratorContext {
    public static PythonCodeGeneratorContext stub(FieldsLookup fieldsLookup) {
        return new PythonCodeGeneratorContext(fieldsLookup);
    }

    public static PythonCodeGeneratorContext stub() {
        return new PythonCodeGeneratorContext(FieldsLookup.EMPTY);
    }

    private final FieldsLookup fieldsLookup;
    private final Set<List<String>> imports;

    public PythonCodeGeneratorContext(FieldsLookup fieldsLookup) {
        this.fieldsLookup = fieldsLookup;
        this.imports = new LinkedHashSet<>();
    }

    public void addImport(List<String> import_) {
        imports.add(import_);
    }

    public List<PythonStatementNode> imports() {
        return imports.stream()
            .map(import_ -> generateImport(import_))
            .toList();
    }

    private PythonStatementNode generateImport(List<String> import_) {
        if (import_.size() == 1) {
            return new PythonImportNode(String.join(".", import_));
        } else {
            return new PythonImportFromNode(
                String.join(".", import_.subList(0, import_.size() - 1)),
                List.of(import_.get(import_.size() - 1))
            );
        }
    }

    public List<TypedRecordFieldNode> fieldsOf(RecordType recordType) {
        return fieldsLookup.fieldsOf(recordType);
    }
}
