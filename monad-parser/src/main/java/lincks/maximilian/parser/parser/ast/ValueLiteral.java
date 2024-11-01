package lincks.maximilian.parser.parser.ast;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValueLiteral<T> implements Literal<T> {

    //holds the actual value
    private final T value;
}
