package lincks.maximilian.parser.custom;

import lincks.maximilian.parser.token.OperatorToken;
import lincks.maximilian.parser.token.Symbol;
import lombok.ToString;

@ToString
public class PrefixOp<T> extends OperatorToken<T> {
    public PrefixOp(Symbol symbol, int arity, int level) {
        super(symbol, arity, level);
    }
}
