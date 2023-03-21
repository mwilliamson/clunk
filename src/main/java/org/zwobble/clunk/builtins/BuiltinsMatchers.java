package org.zwobble.clunk.builtins;

import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BuiltinsMatchers {
    private BuiltinsMatchers() {
    }

    static final NamespaceId NAMESPACE_ID = NamespaceId.source("stdlib", "matchers");

    private static final TypeConstructor MATCHER_CONSTRUCTOR = new TypeConstructor(
        List.of(TypeParameter.contravariant(NamespaceId.source("stdlib", "matchers"), "Matcher", "T")),
        Types.recordType(NamespaceId.source("stdlib", "matchers"), "Matcher")
    );

    static Type matcher(Type type) {
        return Types.construct(MATCHER_CONSTRUCTOR, List.of(type));
    }

    // TODO: type equalTo more precisely
//    private static final TypeParameter EQUAL_TO_TYPE_PARAM = TypeParameter.function(
//        NAMESPACE_ID,
//        "equalTo",
//        "T"
//    );

    static final StaticFunctionType EQUAL_TO_TYPE = new StaticFunctionType(
        NAMESPACE_ID,
        "equalTo",
        Optional.empty(),
        new ParamTypes(List.of(Types.OBJECT), List.of()),
        matcher(Types.OBJECT),
        Visibility.PUBLIC
    );

    static final NamespaceType NAMESPACE_TYPE = new NamespaceType(NAMESPACE_ID, Map.ofEntries(
        Map.entry("equalTo", EQUAL_TO_TYPE)
    ));
}
