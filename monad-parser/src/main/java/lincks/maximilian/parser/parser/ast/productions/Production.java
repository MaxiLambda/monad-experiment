package lincks.maximilian.parser.parser.ast.productions;

import lincks.maximilian.impl.monad.Either;
import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.impl.monad.Maybe;
import lincks.maximilian.parser.custom.InfixOp;
import lincks.maximilian.parser.custom.PrefixOp;
import lincks.maximilian.parser.token.OperatorToken;
import lincks.maximilian.parser.token.Symbol;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static lincks.maximilian.impl.monad.MList.toMList;
import static lincks.maximilian.parser.token.SyntaxToken.L_BRACE;
import static lincks.maximilian.parser.token.SyntaxToken.R_BRACE;

@RequiredArgsConstructor
@Getter
@ToString
public class Production {

    public static final Function<Function<Integer, Integer>, Either<MList<Production>, OperatorProduction>> INITIAL_PRODUCTION = (normalizeOperatorLevel) ->
            new Either.Left<>(new MList<>(new Production(0, Type.PREFIX_PRODUCTION, normalizeOperatorLevel)));
    private final int level;
    private final Type type;
    private final Function<Integer, Integer> normalizeOperatorLevel;

//E   -> E' | !E | % E E
    //E'  -> T E''
    //E'' -> + T E'' | eps
    //T   -> T' | @T | ? T T
    //T'  -> F T''
    //T'' -> * F T'' | eps
    //F   -> (E) | int

    public static boolean isEpsilonProduction(Either<MList<Production>, OperatorProduction> production) {
        return switch (production) {
            case Either.Left<MList<Production>, OperatorProduction> productions -> productions.value().isEmpty();
            case Either.Right<MList<Production>, OperatorProduction> ignore -> false;
        };
    }

    public boolean hasType(Type type) {
        return this.type.equals(type);
    }

    public <T> Either<MList<Production>, OperatorProduction> nextProductions(Symbol nextSymbol, Map<Symbol, OperatorToken<T>> operators, int maxStrength) {
        return Maybe.fromNullable(operators.get(nextSymbol))
                .map(o -> operatorProductions(o, nextSymbol, maxStrength))
                .otherwise(nonOperatorProductions(nextSymbol, maxStrength));

    }

    private Either<MList<Production>, OperatorProduction> nonOperatorProductions(Symbol nextSymbol, int maxStrength) {
        return switch (type) {
            case PREFIX_PRODUCTION -> {
                if (R_BRACE.getSymbol().equals(nextSymbol)) {
                    throw new RuntimeException("%s not expected".formatted(nextSymbol));
                }
                //assume literal or L_BRACE
                yield new Either.Left<>(new MList<>(createProduction(level, Type.INTERMEDIATE_PRODUCTION)));
            }
            case INTERMEDIATE_PRODUCTION -> {
                if (R_BRACE.getSymbol().equals(nextSymbol)) {
                    throw new RuntimeException("%s not expected".formatted(nextSymbol));
                }
                //assume literal or L_BRACE
                yield new Either.Left<>(new MList<>(createProduction(level + 1, level == maxStrength
                        ? Type.LAST_PRODUCTION
                        : Type.PREFIX_PRODUCTION), createProduction(level, Type.INFIX_PRODUCTION)));
            }
            case INFIX_PRODUCTION -> new Either.Left<>(new MList<>());
            case LAST_PRODUCTION -> {
                if (L_BRACE.getSymbol().equals(nextSymbol)) {
                    yield new Either.Left<>(new MList<>(createProduction(0, Type.PREFIX_PRODUCTION)));
                } else if (R_BRACE.getSymbol().equals(nextSymbol)) {
                    throw new RuntimeException("%s not expected".formatted(nextSymbol));
                }
                //assume literal
                //a no argument operator is a literal
                yield new Either.Right<>(new OperatorProduction(nextSymbol, new MList<>()));
            }
        };
    }

    private <T> Either<MList<Production>, OperatorProduction> operatorProductions(OperatorToken<T> op, Symbol nextSymbol, int maxStrength) {
        return switch (type) {
            case PREFIX_PRODUCTION -> {
                if (op.getLevel() == level && op instanceof PrefixOp<T>) {
                    yield new Either.Right<>(new OperatorProduction(nextSymbol, Stream.generate(() -> this)
                            .limit(op.getArity()).collect(toMList())));
                } else if (op.getLevel() > level && op instanceof PrefixOp<T>) {
                    yield new Either.Left<>(new MList<>(createProduction(level, Type.INTERMEDIATE_PRODUCTION)));
                }
                throw new RuntimeException("%s not expected".formatted(op));
            }
            case INTERMEDIATE_PRODUCTION -> {
                if (op.getLevel() > level && op instanceof PrefixOp<T>) {
                    yield new Either.Left<>(new MList<>(createProduction(level + 1, Type.PREFIX_PRODUCTION)
                            , createProduction(level, Type.INFIX_PRODUCTION)));
                }
                throw new RuntimeException("%s not expected".formatted(op));
            }
            case INFIX_PRODUCTION -> {
                if (op instanceof InfixOp<T>) {
                    if (op.getLevel() == level) {
                        yield new Either.Right<>(new OperatorProduction(nextSymbol, new MList<>(
                                createProduction(level + 1, level == maxStrength
                                        ? Type.LAST_PRODUCTION
                                        : Type.PREFIX_PRODUCTION)
                                , this)));
                    } else if (op.getLevel() > level) {
                        throw new RuntimeException("%s not expected".formatted(op));
                    }
                    yield new Either.Left<>(new MList<>()); //eps
                }
                yield new Either.Left<>(new MList<>()); //eps
            }
            case LAST_PRODUCTION -> throw new RuntimeException("%s not expected".formatted(op));
        };
    }

    private Production createProduction(int level, Type type) {
        return new Production(level, type, normalizeOperatorLevel);
    }

    public enum Type {
        PREFIX_PRODUCTION, //E and T
        INTERMEDIATE_PRODUCTION, //E' and T'
        INFIX_PRODUCTION, //E'' and T''
        LAST_PRODUCTION, // F
    }

    public record OperatorProduction(Symbol operator, MList<Production> productions) {
    }

}
