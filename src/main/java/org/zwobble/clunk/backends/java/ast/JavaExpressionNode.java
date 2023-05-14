package org.zwobble.clunk.backends.java.ast;

public interface JavaExpressionNode extends JavaNode {
    JavaPrecedence precedence();

    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaAddNode node);
        T visit(JavaBoolLiteralNode node);
        T visit(JavaCallNode node);
        T visit(JavaCallNewNode node);
        T visit(JavaCallStaticNode node);
        T visit(JavaCastNode node);
        T visit(JavaConditionalNode node);
        T visit(JavaEqualsNode node);
        T visit(JavaInstanceOfNode node);
        T visit(JavaIntLiteralNode node);
        T visit(JavaLambdaExpressionNode node);
        T visit(JavaLogicalAndNode node);
        T visit(JavaLogicalNotNode node);
        T visit(JavaLogicalOrNode node);
        T visit(JavaMemberAccessNode node);
        T visit(JavaMethodReferenceStaticNode node);
        T visit(JavaNotEqualNode node);
        T visit(JavaReferenceNode node);
        T visit(JavaStringLiteralNode node);
        T visit(JavaSubtractNode node);
    }
}
