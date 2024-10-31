package lincks.maximilian.parser.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LiteralToken<T> implements Token, TypedToken<T> {

    @Getter
    private final Symbol symbol;

    @Override
    public TokenType getType() {
        return TokenType.LITERAL;
    }
}
