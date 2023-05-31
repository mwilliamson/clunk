package org.zwobble.clunk.ast.typed;

public interface TypedInterfaceBodyDeclarationNode extends TypedNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(TypedFunctionSignatureNode node);
    }
}
