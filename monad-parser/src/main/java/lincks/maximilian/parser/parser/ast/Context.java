package lincks.maximilian.parser.parser.ast;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.parser.token.Symbol;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class Context<T> {
    private final Map<Symbol, Function<MList<Literal<T>>, Literal<T>>> operators;

    public Optional<Function<MList<Literal<T>>, Literal<T>>> getReduction(Symbol symbol) {
        return Optional.ofNullable(operators.get(symbol));
    }
}
