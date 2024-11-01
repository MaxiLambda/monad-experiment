package lincks.maximilian.parser.parser.ast;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.impl.monad.Maybe;
import lincks.maximilian.parser.token.Symbol;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class Context<T> {
    private final Map<Symbol, Function<MList<Literal<T>>, Literal<T>>> operators;

    public Maybe<Function<MList<Literal<T>>, Literal<T>>> getReduction(Symbol symbol) {
        return Maybe.fromNullable(operators.get(symbol));
    }
}
