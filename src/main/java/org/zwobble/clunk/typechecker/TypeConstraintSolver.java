package org.zwobble.clunk.typechecker;

import org.pcollections.OrderedPMap;
import org.pcollections.PVector;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.*;
import org.zwobble.clunk.util.P;
import org.zwobble.clunk.util.PCollectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class TypeConstraintSolver {
    public static TypeConstraintSolver create(
        List<TypeArg> typeArgs,
        SubtypeRelations subtypeRelations
    ) {
        return new TypeConstraintSolver(
            typeArgs.stream()
                .map(typeArg -> Map.entry(typeArg, TypeBounds.EMPTY))
                .collect(PCollectors.toOrderedPMap()),
            subtypeRelations
        );
    }
    public static TypeConstraintSolver create(
        SubtypeRelations subtypeRelations
    ) {
        return new TypeConstraintSolver(
            OrderedPMap.empty(),
            subtypeRelations
        );
    }

    record TypeBounds(PVector<Type> lower, PVector<Type> upper) {
        private static final TypeBounds EMPTY = new TypeBounds(P.vector(), P.vector());

        public TypeBounds addLowerBound(Type lowerBound) {
            return new TypeBounds(
                this.lower.plus(lowerBound),
                this.upper
            );
        }

        public TypeBounds addUpperBound(Type upperBound) {
            return new TypeBounds(
                this.lower,
                this.upper.plus(upperBound)
            );
        }
    }

    private final OrderedPMap<TypeArg, TypeBounds> typeBounds;
    private final SubtypeRelations subtypeRelations;

    private TypeConstraintSolver(
        OrderedPMap<TypeArg, TypeBounds> typeBounds,
        SubtypeRelations subtypeRelations
    ) {
        this.typeBounds = typeBounds;
        this.subtypeRelations = subtypeRelations;
    }

    public TypeConstraintSolver addLowerTypeBound(TypeArg typeArg, Type lowerBound) {
        return updateTypeBounds(typeArg, bounds -> bounds.addLowerBound(lowerBound));
    }

    public TypeConstraintSolver addUpperTypeBound(TypeArg typeArg, Type upperBound) {
        return updateTypeBounds(typeArg, bounds -> bounds.addUpperBound(upperBound));
    }

    private TypeConstraintSolver updateTypeBounds(
        TypeArg typeArg,
        Function<TypeBounds, TypeBounds> update
    ) {
        var bounds = update.apply(this.typeBounds.get(typeArg));
        return new TypeConstraintSolver(
            this.typeBounds.plus(typeArg, bounds),
            this.subtypeRelations
        );
    }

    private boolean isBoundTypeArg(TypeArg typeArg) {
        return this.typeBounds.containsKey(typeArg);
    }

    public static Optional<TypeConstraintSolver> addSubtypeConstraint(
        TypeConstraintSolver solver,
        Type subtype,
        Type supertype
    ) {
        if (supertype instanceof TypeArg supertypeTypeArg && solver.isBoundTypeArg(supertypeTypeArg)) {
            solver = solver.addLowerTypeBound(supertypeTypeArg, subtype);
            return Optional.of(solver);
        }

        if (subtype instanceof TypeArg subtypeTypeArg && solver.isBoundTypeArg(subtypeTypeArg)) {
            solver = solver.addUpperTypeBound(subtypeTypeArg, supertype);
            return Optional.of(solver);
        }

        if (supertype.equals(Types.OBJECT)) {
            return Optional.of(solver);
        }

        if (subtype.equals(Types.NOTHING)) {
            return Optional.of(solver);
        }

        if (subtype.equals(supertype)) {
            return Optional.of(solver);
        }

        var extendedTypes = solver.subtypeRelations.extendedTypes(subtype);
        for (var extendedType : extendedTypes) {
            var result = addSubtypeConstraint(solver, extendedType, supertype);
            if (result.isPresent()) {
                return result;
            }
        }

        if (
            subtype instanceof FunctionValueType subtypeFunction &&
            supertype instanceof FunctionValueType supertypeFunction
        ) {
            // TODO: handle named params
            if (subtypeFunction.params().positional().size() != supertypeFunction.params().positional().size()) {
                return Optional.empty();
            }

            for (var i = 0; i < subtypeFunction.params().positional().size(); i++) {
                var subtypeParam = subtypeFunction.params().positional().get(i);
                var supertypeParam = supertypeFunction.params().positional().get(i);
                var result = addSubtypeConstraint(solver, supertypeParam, subtypeParam);
                if (result.isEmpty()) {
                    return Optional.empty();
                } else {
                    solver = result.get();
                }
            }

            return addSubtypeConstraint(solver, subtypeFunction.returnType(), supertypeFunction.returnType());
        }

        if (
            subtype instanceof ConstructedType subtypeConstructed &&
            supertype instanceof ConstructedType supertypeConstructed &&
            subtypeConstructed.constructor().equals(supertypeConstructed.constructor())
        ) {
            for (var i = 0; i < subtypeConstructed.constructor().params().size(); i++) {
                var param = subtypeConstructed.constructor().params().get(i);
                var argSubtype = subtypeConstructed.args().get(i);
                var argSupertype = supertypeConstructed.args().get(i);

                switch (param.variance()) {
                    case COVARIANT -> {
                        var result = addSubtypeConstraint(solver, argSubtype, argSupertype);
                        if (result.isEmpty()) {
                            return Optional.empty();
                        } else {
                            solver = result.get();
                        }
                    }
                    case CONTRAVARIANT -> {
                        var result = addSubtypeConstraint(solver, argSupertype, argSubtype);
                        if (result.isEmpty()) {
                            return Optional.empty();
                        } else {
                            solver = result.get();
                        }
                    }
                    case INVARIANT -> {
                        var resultCovariant = addSubtypeConstraint(solver, argSubtype, argSupertype);
                        if (resultCovariant.isEmpty()) {
                            return Optional.empty();
                        }
                        solver = resultCovariant.get();

                        var resultContravariant = addSubtypeConstraint(solver, argSupertype, argSubtype);
                        if (resultContravariant.isEmpty()) {
                            return Optional.empty();
                        }
                        solver = resultContravariant.get();
                    }
                }
            }
            return Optional.of(solver);
        }

        return Optional.empty();
    }

    public List<Type> solve(Source source) {
        var result = new ArrayList<Type>();

        for (var typeBounds : this.typeBounds.values()) {
            var inferredType = solveTypeBounds(typeBounds, source);
            result.add(inferredType);
        }

        return result;
    }

    private Type solveTypeBounds(TypeBounds typeParamBounds, Source source) {
        if (typeParamBounds.lower.isEmpty() && typeParamBounds.upper.isEmpty()) {
            throw new MissingTypeLevelArgsError(source);
        }

        var lowerBound = Types.NOTHING;
        for (var lower : typeParamBounds.lower) {
            lowerBound = Types.commonSupertype(lowerBound, lower);
        }

        var upperBound = Types.OBJECT;
        for (var upper : typeParamBounds.upper) {
            upperBound = Types.commonSubtype(upperBound, upper);
        }

        if (!subtypeRelations.isSubType(lowerBound, upperBound)) {
            throw new MissingTypeLevelArgsError(source);
        }

        return typeParamBounds.lower.isEmpty() && !typeParamBounds.upper.isEmpty()
            ? upperBound
            : lowerBound;
    }
}
