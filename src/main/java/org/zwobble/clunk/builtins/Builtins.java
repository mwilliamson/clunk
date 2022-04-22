package org.zwobble.clunk.builtins;

import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;

import static org.zwobble.clunk.types.Types.metaType;

public class Builtins {
    public static final Map<String, Type> ENVIRONMENT = Map.ofEntries(
        Map.entry("Bool", metaType(Types.BOOL)),
        Map.entry("Int", metaType(Types.INT)),
        Map.entry("String", metaType(Types.STRING))
    );

    public static final NamespaceType NAMESPACE_STDLIB_ASSERT = new NamespaceType(
        List.of("Stdlib", "Assert"),
        Map.ofEntries(
            Map.entry("isTrue", new FunctionType(List.of(Types.BOOL), Types.UNIT))
        )
    );
}
