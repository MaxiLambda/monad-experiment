package lincks.maximilian.parser.parser.ast;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.parser.token.Symbol;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
public class Expression<T> implements AstExpression<T> {

    private final Symbol symbol;
    private final MList<AstExpression<T>> args;

    @Override
    public Literal<T> eval(Context<T> context) {
        return context.getReduction(symbol)
                .map(f -> f.apply(args.map(arg -> arg.eval(context))))
                .orElseThrow();
    }
}
