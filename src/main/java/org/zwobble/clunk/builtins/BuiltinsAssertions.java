package org.zwobble.clunk.builtins;

import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BuiltinsAssertions {
    private BuiltinsAssertions() {
    }

    static final NamespaceType NAMESPACE_TYPE = new NamespaceType(NamespaceId.source("stdlib", "assertions"), Map.ofEntries(
        Map.entry("assertThat", new StaticFunctionType(
            NamespaceId.source("stdlib", "assertions"),
            "assertThat",
            Optional.empty(),
            new ParamTypes(List.of(Types.OBJECT, Types.UNIT), List.of()),
            Types.UNIT,
            Visibility.PUBLIC
        ))
    ));
}
