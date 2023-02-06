package org.zwobble.clunk.backends.python.codegenerator.macros;

import org.zwobble.clunk.backends.python.ast.*;
import org.zwobble.clunk.backends.python.codegenerator.PythonClassMacro;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class PythonListMacro implements PythonClassMacro {
    public static final PythonListMacro INSTANCE = new PythonListMacro();

    private PythonListMacro() {
    }

    @Override
    public Type receiverType() {
        return Types.LIST_CONSTRUCTOR.genericType();
    }

    @Override
    public PythonExpressionNode compileConstructorCall(PythonArgsNode args) {
        return new PythonListNode(List.of());
    }

    @Override
    public PythonExpressionNode compileMethodCall(PythonExpressionNode receiver, String methodName, PythonArgsNode args) {
        var result = tryCompileMethodCall(receiver, methodName, args);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new UnsupportedOperationException("unexpected method: " + methodName);
        }
    }

    Optional<PythonExpressionNode> tryCompileMethodCall(
        PythonExpressionNode receiver,
        String methodName,
        PythonArgsNode args
    ) {
        switch (methodName) {
            case "contains" -> {
                var result = new PythonInNode(args.positional().get(0), receiver);
                return Optional.of(result);
            }
            case "flatMap" -> {
                // TODO: Need to guarantee this doesn't collide -- we don't
                // allow variables to start with an underscore, so this should
                // be safe, but a little more rigour would probably be wise
                // e.g. keeping track of variables in scope.
                // Also, this probably doesn't work well with nested flatMaps.
                // TODO: avoid evaluating func multiple times
                var inputElementName = "_element";
                var outputElementName = "_result";
                var func = args.positional().get(0);

                var result = new PythonListComprehensionNode(
                    new PythonReferenceNode(outputElementName),
                    List.of(
                        new PythonComprehensionForClauseNode(
                            inputElementName,
                            receiver
                        ),
                        new PythonComprehensionForClauseNode(
                            outputElementName,
                            new PythonCallNode(
                                func,
                                new PythonArgsNode(
                                    List.of(new PythonReferenceNode(inputElementName)),
                                    List.of()
                                )
                            )
                        )
                    )
                );
                return Optional.of(result);
            }
            case "get" -> {
                var result = new PythonSubscriptionNode(receiver, args.positional());
                return Optional.of(result);
            }
            case "last" -> {
                var result = new PythonSubscriptionNode(
                    receiver,
                    List.of(new PythonIntLiteralNode(BigInteger.valueOf(-1)))
                );
                return Optional.of(result);
            }
            case "length" -> {
                var result = new PythonCallNode(
                    new PythonReferenceNode("len"),
                    new PythonArgsNode(
                        List.of(receiver),
                        List.of()
                    )
                );
                return Optional.of(result);
            }
            default -> {
                return Optional.empty();
            }
        }
    }
}
