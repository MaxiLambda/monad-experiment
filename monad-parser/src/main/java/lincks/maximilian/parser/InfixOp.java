package lincks.maximilian.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class InfixOp {
    //TODO action, receives two elements of type int/long/double
    private final int level;
    private final Token token;
}
