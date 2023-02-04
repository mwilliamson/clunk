package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ParamTypesTests {
    @Test
    public void describeCommaSeparatesParams() {
        var paramTypes = new ParamTypes(
            List.of(Types.INT, Types.STRING),
            List.of(new NamedParamType("x", Types.BOOL), new NamedParamType("y", Types.STRING))
        );

        var result = paramTypes.describe();

        assertThat(result, equalTo("Int, String, .x: Bool, .y: String"));
    }
}
