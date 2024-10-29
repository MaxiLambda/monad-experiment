package lincks.maximilian.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PrefixOp {
    //TODO action, receives array with arity many elements of type int/long/double
    private final int level;
    private final int arity;
    private final Token token;
}
