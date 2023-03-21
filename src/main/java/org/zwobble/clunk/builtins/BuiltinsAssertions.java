package org.zwobble.clunk.builtins;

import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BuiltinsAssertions {

    private static final NamespaceId NAMESPACE_ID = NamespaceId.source("stdlib", "assertions");

    private BuiltinsAssertions() {
    }

    private static final TypeParameter ASSERT_THAT_TYPE_PARAM = TypeParameter.function(
        NAMESPACE_ID,
        "assertThat",
        "T"
    );

    static final NamespaceType NAMESPACE_TYPE = new NamespaceType(NAMESPACE_ID, Map.ofEntries(
        Map.entry("assertThat", new StaticFunctionType(
            NAMESPACE_ID,
            "assertThat",
            Optional.of(List.of(ASSERT_THAT_TYPE_PARAM)),
            new ParamTypes(List.of(ASSERT_THAT_TYPE_PARAM, BuiltinsMatchers.matcher(ASSERT_THAT_TYPE_PARAM)), List.of()),
            Types.UNIT,
            Visibility.PUBLIC
        ))
    ));
}
