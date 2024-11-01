package lincks.maximilian.parser.parser.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public final class ValueLiteral<T> implements Literal<T> {

    //holds the actual value
    private final T value;
}
