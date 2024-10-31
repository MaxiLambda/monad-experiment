package lincks.maximilian.parser.token;

import lincks.maximilian.functor.Functor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public abstract class OperatorToken<T> implements Token, TypedToken<T> {

    private final Symbol symbol;
    private final int arity;
    private final int level;
//    private final Function<List<LiteralToken<T>>, LiteralToken<T>> operation;

    @Override
    public TokenType getType() {
        return TokenType.OPERATOR;
    }

//    @Override
//    public LiteralToken<T> eval(List<LiteralToken<T>> vals) {
//        return operation.apply(vals);
//    }
}
