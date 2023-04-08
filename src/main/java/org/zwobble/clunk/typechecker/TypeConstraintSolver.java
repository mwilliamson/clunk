package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.*;
import org.zwobble.clunk.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TypeConstraintSolver {
    private record Constraint(TypeParameter typeParam, Optional<Type> subtype, Optional<Type> supertype) {
        public static Constraint subtype(TypeParameter typeParam, Type supertype) {
            return new Constraint(typeParam, Optional.empty(), Optional.of(supertype));
        }

        public static Constraint supertype(TypeParameter typeParam, Type subtype) {
            return new Constraint(typeParam, Optional.of(subtype), Optional.empty());
        }
    }

    private final List<TypeParameter> typeParams;
    private final SubtypeRelations subtypeRelations;
    private final List<Constraint> constraints;

    public TypeConstraintSolver(
        List<TypeParameter> typeParams,
        SubtypeRelations subtypeRelations
    ) {
        this.typeParams = typeParams;
        this.subtypeRelations = subtypeRelations;
        this.constraints = new ArrayList<>();
    }

    public boolean addSubtypeConstraint(Type subtype, Type supertype) {
        if (supertype.equals(Types.OBJECT)) {
            return true;
        }

        if (subtype.equals(Types.NOTHING)) {
            return true;
        }

        if (subtype.equals(supertype)) {
            return true;
        }

        var extendedTypes = subtypeRelations.extendedTypes(subtype);
        for (var extendedType : extendedTypes) {
            if (addSubtypeConstraint(extendedType, supertype)) {
                return true;
            }
        }

        if (
            subtype instanceof FunctionType subtypeFunction &&
            supertype instanceof FunctionType supertypeFunction
        ) {
            // TODO: handle named params
            if (subtypeFunction.params().positional().size() != supertypeFunction.params().positional().size()) {
                return false;
            }

            for (var i = 0; i < subtypeFunction.params().positional().size(); i++) {
                var subtypeParam = subtypeFunction.params().positional().get(i);
                var supertypeParam = supertypeFunction.params().positional().get(i);
                if (!addSubtypeConstraint(supertypeParam, subtypeParam)) {
                    return false;
                }
            }

            if (!addSubtypeConstraint(subtypeFunction.returnType(), supertypeFunction.returnType())) {
                return false;
            }

            return true;
        }

        if (
            subtype instanceof ConstructedType subtypeConstructed &&
            supertype instanceof ConstructedType supertypeConstructed &&
            subtypeConstructed.constructor().equals(supertypeConstructed.constructor())
        ) {
            for (var i = 0; i < subtypeConstructed.constructor().params().size(); i++) {
                var param = subtypeConstructed.constructor().params().get(i);
                var argSubtype = subtypeConstructed.args().get(0);
                var argSupertype = supertypeConstructed.args().get(0);

                switch (param.variance()) {
                    case COVARIANT -> {
                        if (!addSubtypeConstraint(argSubtype, argSupertype)) {
                            return false;
                        }
                    }
                    case CONTRAVARIANT -> {
                        if (!addSubtypeConstraint(argSupertype, argSubtype)) {
                            return false;
                        }
                    }
                    case INVARIANT -> {
                        if (!argSubtype.equals(argSupertype)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        if (supertype instanceof TypeParameter supertypeTypeParam && typeParams.contains(supertypeTypeParam)) {
            constraints.add(Constraint.supertype(supertypeTypeParam, subtype));
            return true;
        }

        if (subtype instanceof TypeParameter subtypeTypeParam && typeParams.contains(subtypeTypeParam)) {
            constraints.add(Constraint.subtype(subtypeTypeParam, supertype));
            return true;
        }

        return false;
    }

    public List<Type> solve(Source source) {
        var typeParamBounds = new HashMap<TypeParameter, Pair<Type, Type>>();
        for (var typeParam : typeParams) {
            typeParamBounds.put(typeParam, Pair.of(Types.OBJECT, Types.NOTHING));
        }

        var constraints = new ArrayList<>(this.constraints);

        while (!constraints.isEmpty()) {
            var constraintIterator = constraints.listIterator();
            while (constraintIterator.hasNext()) {
                var constraint = constraintIterator.next();
                var currentBounds = typeParamBounds.get(constraint.typeParam());
                // TODO: check for type parameter as constraining type
                var upperBound = currentBounds.first();
                var lowerBound = currentBounds.second();
                if (constraint.subtype().isPresent()) {
                    lowerBound = Types.commonSupertype(lowerBound, constraint.subtype().get());
                }
                if (constraint.supertype().isPresent()) {
                    upperBound = Types.commonSubtype(upperBound, constraint.supertype().get());
                }
                if (!subtypeRelations.isSubType(lowerBound, upperBound)) {
                    throw new MissingTypeLevelArgsError(source);
                }
                typeParamBounds.put(constraint.typeParam(), Pair.of(upperBound, lowerBound));
                constraintIterator.remove();
            }
        }

        return typeParams.stream()
            .map(typeParam -> {
                var bounds = typeParamBounds.get(typeParam);
                var upperBound = bounds.first();
                var lowerBound = bounds.second();
                // TODO: check to see if we've been constrained rather than for Nothing/Object?
                if (lowerBound.equals(Types.NOTHING) && upperBound.equals(Types.OBJECT)) {
                    throw new MissingTypeLevelArgsError(source);
                } else if (lowerBound.equals(Types.NOTHING)) {
                    return upperBound;
                } else {
                    return lowerBound;
                }
            })
            .toList();
    }
}
