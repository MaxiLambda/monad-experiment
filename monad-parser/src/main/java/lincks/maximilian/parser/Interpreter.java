package lincks.maximilian.parser;

import lincks.maximilian.parser.parser.ast.AstExpression;
import lincks.maximilian.parser.parser.ast.Context;
import lincks.maximilian.parser.parser.ast.Literal;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class Interpreter<T> {

    private final Parser<T> parser;
    private final Function<Literal<T>, T> fromLiteral;
    private final Context<T> context;

    public T run(String input) {
        AstExpression<T> ast = parser.run(input);

        Literal<T> result = ast.eval(context);
        return fromLiteral.apply(result);
    }

    public T run(AstExpression<T> ast) {
        Literal<T> result = ast.eval(context);
        return fromLiteral.apply(result);
    }

}
