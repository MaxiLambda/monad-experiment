package lincks.maximilian.parser.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LiteralToken<T> implements Token {

    @Getter
    private final Symbol symbol;
}
