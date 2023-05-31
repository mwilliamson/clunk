package org.zwobble.clunk.ast.untyped;

public interface UntypedInterfaceBodyDeclarationNode extends UntypedNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(UntypedFunctionSignatureNode node);
    }
}
