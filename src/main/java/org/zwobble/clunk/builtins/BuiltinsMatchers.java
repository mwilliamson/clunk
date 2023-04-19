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

    private static final TypeParameter HAS_MEMBER_TYPE_PARAM_RECEIVER = TypeParameter.function(
        NAMESPACE_ID,
        "hasMember",
        "T"
    );

    private static final TypeParameter HAS_MEMBER_TYPE_PARAM_MEMBER = TypeParameter.function(
        NAMESPACE_ID,
        "hasMember",
        "R"
    );

    static final StaticFunctionType HAS_MEMBER_TYPE = new StaticFunctionType(
        NAMESPACE_ID,
        "hasMember",
        Optional.of(List.of(HAS_MEMBER_TYPE_PARAM_RECEIVER, HAS_MEMBER_TYPE_PARAM_MEMBER)),
        new ParamTypes(
            List.of(
                Types.member(HAS_MEMBER_TYPE_PARAM_RECEIVER, HAS_MEMBER_TYPE_PARAM_MEMBER),
                matcher(HAS_MEMBER_TYPE_PARAM_MEMBER)
            ),
            List.of()
        ),
        matcher(HAS_MEMBER_TYPE_PARAM_RECEIVER),
        Visibility.PUBLIC
    );

    private static final TypeParameter IS_SOME_TYPE_PARAM = TypeParameter.function(
        NAMESPACE_ID,
        "isSome",
        "T"
    );

    static final StaticFunctionType IS_SOME_TYPE = new StaticFunctionType(
        NAMESPACE_ID,
        "isSome",
        Optional.of(List.of(IS_SOME_TYPE_PARAM)),
        new ParamTypes(List.of(matcher(IS_SOME_TYPE_PARAM)), List.of()),
        matcher(Types.option(IS_SOME_TYPE_PARAM)),
        Visibility.PUBLIC
    );

    static final NamespaceType NAMESPACE_TYPE = new NamespaceType(NAMESPACE_ID, Map.ofEntries(
        Map.entry("equalTo", EQUAL_TO_TYPE),
        Map.entry("hasMember", HAS_MEMBER_TYPE),
        Map.entry("isSome", IS_SOME_TYPE)
    ));
}
