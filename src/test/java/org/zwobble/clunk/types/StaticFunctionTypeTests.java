package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class StaticFunctionTypeTests {
    @Test
    public void describeIncludesArgsAndReturnType() {
        var type = Types.staticFunctionType(
            NamespaceId.source("A", "B"),
            "f",
            List.of(Types.BOOL, Types.STRING),
            Types.INT
        );

        var result = type.describe();

        assertThat(result, equalTo("A/B.f: (Bool, String) -> Int"));
    }

    @Test
    public void describeIncludesTypeLevelArgs() {
        var type = Types.staticFunctionType(
            NamespaceId.source("A", "B"),
            "f",
            List.of(
                TypeParam.function(NamespaceId.source(), "f", "T"),
                TypeParam.function(NamespaceId.source(), "f", "U")
            ),
            ParamTypes.empty(),
            Types.INT
        );

        var result = type.describe();

        assertThat(result, equalTo("A/B.f: [T, U]() -> Int"));
    }

    @Test
    public void replaceReplacesPositionalParamTypes() {
        var typeParameter = TypeParam.covariant(NamespaceId.source(), "X", "T");
        var type = Types.staticFunctionType(
            NamespaceId.source(),
            "f",
            List.of(),
            ParamTypes.of(List.of(typeParameter)),
            Types.INT
        );
        var typeMap = new TypeMap(Map.ofEntries(
            Map.entry(typeParameter, Types.STRING)
        ));

        var result = type.replace(typeMap);

        assertThat(result, equalTo(Types.staticFunctionType(
            NamespaceId.source(),
            "f",
            List.of(),
            ParamTypes.of(List.of(Types.STRING)),
            Types.INT
        )));
    }

    @Test
    public void replaceReplacesReturnType() {
        var typeParameter = TypeParam.covariant(NamespaceId.source(), "X", "T");
        var type = Types.staticFunctionType(
            NamespaceId.source(),
            "f",
            List.of(),
            ParamTypes.of(List.of(Types.INT)),
            typeParameter
        );
        var typeMap = new TypeMap(Map.ofEntries(
            Map.entry(typeParameter, Types.STRING)
        ));

        var result = type.replace(typeMap);

        assertThat(result, equalTo(Types.staticFunctionType(
            NamespaceId.source(),
            "f",
            List.of(),
            ParamTypes.of(List.of(Types.INT)),
            Types.STRING
        )));
    }
}
