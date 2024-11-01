package lincks.maximilian.parser.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class OperatorToken<T> implements Token {

    private final Symbol symbol;
    private final int arity;
    private final int level;
}
