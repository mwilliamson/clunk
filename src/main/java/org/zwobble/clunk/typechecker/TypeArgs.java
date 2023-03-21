package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.TypeMap;
import org.zwobble.clunk.types.TypeParameter;
import org.zwobble.clunk.types.Types;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TypeArgs {
    public static TypeArgs forTypeParams(List<TypeParameter> typeParams) {
        var typeParamToTypeArg = new LinkedHashMap<TypeParameter, Optional<Type>>();
        for (var typeParam : typeParams) {
            typeParamToTypeArg.put(typeParam, Optional.empty());
        }
        return new TypeArgs(typeParamToTypeArg);
    }

    private final LinkedHashMap<TypeParameter, Optional<Type>> typeParamToTypeArg;

    public TypeArgs(LinkedHashMap<TypeParameter, Optional<Type>> typeParamToTypeArg) {
        this.typeParamToTypeArg = typeParamToTypeArg;
    }

    public Type specialise(Type type) {
        var fullTypeMap = typeParamToTypeArg.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue().orElse(Types.OBJECT)
            ));
        return type.replace(new TypeMap(fullTypeMap));
    }

    public SignatureNonGeneric specialise(Signature signature, Source source) {
        // TODO: handle missing unknown type args
        var typeArgs = typeParamToTypeArg.values().stream()
            // TODO: include specific missing type arg
            .map(typeArg -> typeArg.orElseThrow(() -> new MissingTypeLevelArgsError(source)))
            .toList();

        return signature instanceof SignatureGeneric signatureGeneric
            ? signatureGeneric.typeArgs(typeArgs)
            : (SignatureNonGeneric) signature;
    }

    public void unify(Type paramType, Type argType) {
        // TODO: handle conflicts of type args
        if (typeParamToTypeArg.containsKey(paramType)/* && typeParamToTypeArg.get(paramType).isEmpty()*/) {
            typeParamToTypeArg.put((TypeParameter) paramType, Optional.of(argType));
        }
    }
}
