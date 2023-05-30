package org.zwobble.clunk.builtins;

import org.zwobble.clunk.typechecker.TypeCheckerContext;
import org.zwobble.clunk.typechecker.Variable;
import org.zwobble.clunk.types.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.zwobble.clunk.types.Types.metaType;

public class Builtins {
    private static final TypeParameter NONE_TYPE_PARAM = TypeParameter.function(
        Types.BUILTIN_NAMESPACE_ID,
        "none",
        "T"
    );

    private static final Map<String, Type> ENVIRONMENT_TYPES = Map.ofEntries(
        Map.entry("Bool", metaType(Types.BOOL)),
        Map.entry("Int", metaType(Types.INT)),
        Map.entry(Types.LIST_CONSTRUCTOR.name(), Types.typeConstructorType(Types.LIST_CONSTRUCTOR)),
        Map.entry(Types.MAP_CONSTRUCTOR.name(), Types.typeConstructorType(Types.MAP_CONSTRUCTOR)),
        Map.entry(Types.MEMBER_CONSTRUCTOR.name(), Types.typeConstructorType(Types.MEMBER_CONSTRUCTOR)),
        Map.entry(Types.MUTABLE_LIST_CONSTRUCTOR.name(), Types.typeConstructorType(Types.MUTABLE_LIST_CONSTRUCTOR)),
        Map.entry(Types.OPTION_CONSTRUCTOR.name(), Types.typeConstructorType(Types.OPTION_CONSTRUCTOR)),
        Map.entry("String", metaType(Types.STRING)),
        Map.entry("StringBuilder", Types.metaType(Types.STRING_BUILDER)),
        Map.entry("Unit", metaType(Types.UNIT)),

        Map.entry("none", new StaticFunctionType(
            Types.BUILTIN_NAMESPACE_ID,
            "none",
            Optional.of(List.of(NONE_TYPE_PARAM)),
            ParamTypes.empty(),
            Types.option(NONE_TYPE_PARAM),
            Visibility.PUBLIC
        ))
    );

    public static final Map<String, Variable> ENVIRONMENT = ENVIRONMENT_TYPES.entrySet()
        .stream()
        .collect(Collectors.toMap(
            entry -> entry.getKey(),
            entry -> Variable.local(entry.getValue())
        ));

    // TODO: proper typing for builtin modules
    public static final TypeCheckerContext TYPE_CHECKER_CONTEXT = createContext();

    private static TypeCheckerContext createContext() {
        var context = TypeCheckerContext.EMPTY;

        context = context.updateNamespaceType(BuiltinsAssertions.NAMESPACE_TYPE);

        context = context.updateNamespaceType(BuiltinsMatchers.NAMESPACE_TYPE);

        context = context.withBuiltins(Builtins.ENVIRONMENT);

        context = context.addMemberTypes(Types.LIST_CONSTRUCTOR.genericType(), Map.ofEntries(
            Map.entry("contains", Types.methodType(
                Types.LIST_CONSTRUCTOR,
                List.of(Types.LIST_CONSTRUCTOR.param(0)),
                Types.BOOL
            )),
            Map.entry("flatMap", listFlatMapType()),
            Map.entry("get", Types.methodType(
                Types.LIST_CONSTRUCTOR,
                List.of(Types.INT),
                Types.LIST_CONSTRUCTOR.param(0)
            )),
            Map.entry("last", Types.methodType(
                Types.LIST_CONSTRUCTOR,
                List.of(),
                Types.LIST_CONSTRUCTOR.param(0)
            )),
            // TODO: should be a property?
            Map.entry("length", Types.methodType(
                Types.LIST_CONSTRUCTOR,
                List.of(),
                Types.INT
            ))
        ));

        context = context.addConstructorType(Types.constructorType(
            List.of(),
            Types.MUTABLE_LIST_CONSTRUCTOR.genericType()
        ));

        context = context.addMemberTypes(Types.MAP_CONSTRUCTOR.genericType(), Map.ofEntries(
            Map.entry("get", Types.methodType(
                Types.MAP_CONSTRUCTOR,
                List.of(Types.MAP_CONSTRUCTOR.param(0)),
                Types.option(Types.MAP_CONSTRUCTOR.param(1))
            ))
        ));

        var mutableListTypeParam = Types.MUTABLE_LIST_CONSTRUCTOR.params().get(0);
        var mutableListListSupertype = Types.list(mutableListTypeParam);
        var mutableListMemberTypes = new HashMap<>(context.memberTypes(mutableListListSupertype));
        mutableListMemberTypes.put("add", Types.methodType(
            Types.MUTABLE_LIST_CONSTRUCTOR,
            List.of(mutableListTypeParam),
            Types.UNIT
        ));
        context = context.addMemberTypes(Types.MUTABLE_LIST_CONSTRUCTOR.genericType(), mutableListMemberTypes);

        context = context.addSubtypeRelation(
            Types.MUTABLE_LIST_CONSTRUCTOR.genericType(),
            mutableListListSupertype
        );

        context = context.addConstructorType(Types.constructorType(List.of(), Types.STRING_BUILDER));

        context = context.addMemberTypes(Types.STRING_BUILDER, Map.ofEntries(
            Map.entry("append", Types.methodType(Types.STRING_BUILDER, List.of(Types.STRING), Types.UNIT)),
            Map.entry("build", Types.methodType(Types.STRING_BUILDER, List.of(), Types.STRING))
        ));

        return context;
    }

    private static MethodType listFlatMapType() {
        var typeParameterResult = TypeParameter.method(
            Types.LIST_CONSTRUCTOR,
            "flatMap",
            "R"
        );

        return Types.methodType(
            Types.LIST_CONSTRUCTOR,
            List.of(typeParameterResult),
            List.of(
                Types.functionType(
                    List.of(Types.LIST_CONSTRUCTOR.param(0)),
                    Types.list(typeParameterResult)
                )
            ),
            Types.list(typeParameterResult)
        );
    }
}
