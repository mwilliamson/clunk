package org.zwobble.clunk.backends.java.ast;

public interface JavaStatementNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaAssignmentNode node);
        T visit(JavaReturnNode node);
    }
}
