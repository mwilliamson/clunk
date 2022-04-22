package org.zwobble.clunk.builtins;

import org.zwobble.clunk.types.*;

import java.util.Map;

import static org.zwobble.clunk.types.Types.metaType;

public class Builtins {
    public static final Map<String, Type> ENVIRONMENT = Map.ofEntries(
        Map.entry("Bool", metaType(Types.BOOL)),
        Map.entry("Int", metaType(Types.INT)),
        Map.entry("String", metaType(Types.STRING))
    );
}
