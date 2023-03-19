package org.zwobble.clunk.builtins;

import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BuiltinsMatchers {
    private BuiltinsMatchers() {
    }

    static final NamespaceId NAMESPACE_ID = NamespaceId.source("stdlib", "matchers");

    static final StaticFunctionType EQUAL_TO_TYPE = new StaticFunctionType(
        NAMESPACE_ID,
        "equalTo",
        Optional.empty(),
        new ParamTypes(List.of(Types.OBJECT), List.of()),
        Types.UNIT,
        Visibility.PUBLIC
    );

    static final NamespaceType NAMESPACE_TYPE = new NamespaceType(NAMESPACE_ID, Map.ofEntries(
        Map.entry("equalTo", EQUAL_TO_TYPE)
    ));
}
