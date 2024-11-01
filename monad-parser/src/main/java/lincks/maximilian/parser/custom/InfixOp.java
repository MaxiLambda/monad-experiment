package lincks.maximilian.parser.custom;

import lincks.maximilian.parser.token.OperatorToken;
import lincks.maximilian.parser.token.Symbol;
import lombok.ToString;

@ToString
public class InfixOp<T> extends OperatorToken<T> {
    public InfixOp(Symbol symbol, int level) {
        super(symbol, 2, level);
    }
}
