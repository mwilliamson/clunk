package org.zwobble.clunk.builtins;

import org.zwobble.clunk.typechecker.TypeCheckerContext;
import org.zwobble.clunk.typechecker.Variable;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.zwobble.clunk.types.Types.metaType;

public class Builtins {
    private static final Map<String, Type> ENVIRONMENT_TYPES = Map.ofEntries(
        Map.entry("Bool", metaType(Types.BOOL)),
        Map.entry("Int", metaType(Types.INT)),
        Map.entry("List", Types.typeConstructorType(Types.LIST_CONSTRUCTOR)),
        Map.entry("MutableList", Types.typeConstructorType(Types.MUTABLE_LIST_CONSTRUCTOR)),
        Map.entry("Option", Types.typeConstructorType(Types.OPTION_CONSTRUCTOR)),
        Map.entry("String", metaType(Types.STRING)),
        Map.entry("StringBuilder", Types.metaType(Types.STRING_BUILDER)),
        Map.entry("Unit", metaType(Types.UNIT))
    );

    public static final Map<String, Variable> ENVIRONMENT = ENVIRONMENT_TYPES.entrySet()
        .stream()
        .collect(Collectors.toMap(
            entry -> entry.getKey(),
            entry -> Variable.local(entry.getValue())
        ));

    // TODO: proper typing for builtin modules
    public static final TypeCheckerContext TYPE_CHECKER_CONTEXT = TypeCheckerContext.EMPTY
        .updateNamespaceType(new NamespaceType(NamespaceName.fromParts("stdlib", "assertions"), Map.ofEntries(
            Map.entry("assertThat", new StaticFunctionType(
                NamespaceName.fromParts("stdlib", "assertions"),
                "assertThat",
                List.of(Types.OBJECT, Types.UNIT),
                Types.UNIT,
                Visibility.PUBLIC
            ))
        )))
        .updateNamespaceType(new NamespaceType(NamespaceName.fromParts("stdlib", "matchers"), Map.ofEntries(
            Map.entry("equalTo", new StaticFunctionType(
                NamespaceName.fromParts("stdlib", "matchers"),
                "equalTo",
                List.of(Types.OBJECT),
                Types.UNIT,
                Visibility.PUBLIC
            ))
        )))
        .withBuiltins(Builtins.ENVIRONMENT)
        .addMemberTypes(Types.LIST_CONSTRUCTOR.genericType(), Map.ofEntries(
            Map.entry("flatMap", listFlatMapType()),
            // TODO: should be a property?
            Map.entry("get", Types.methodType(
                NamespaceName.fromParts(),
                List.of(Types.INT),
                Types.LIST_CONSTRUCTOR.params().get(0)
            )),
            // TODO: should be a property?
            Map.entry("length", Types.methodType(
                NamespaceName.fromParts(),
                List.of(),
                Types.INT
            ))
        ))
        .addConstructorType(Types.STRING_BUILDER, List.of(), Visibility.PUBLIC)
        .addMemberTypes(Types.STRING_BUILDER, Map.ofEntries(
            Map.entry("append", Types.methodType(NamespaceName.fromParts(), List.of(Types.STRING), Types.UNIT)),
            Map.entry("build", Types.methodType(NamespaceName.fromParts(), List.of(), Types.STRING))
        ));

    private static MethodType listFlatMapType() {
        var typeParameterResult = TypeParameter.function(
            NamespaceName.fromParts(),
            "List",
            "flatMap",
            "R"
        );

        return Types.methodType(
            NamespaceName.fromParts(),
            List.of(typeParameterResult),
            List.of(
                Types.functionType(
                    List.of(Types.LIST_CONSTRUCTOR.params().get(0)),
                    Types.list(typeParameterResult)
                )
            ),
            Types.list(typeParameterResult)
        );
    }
}
