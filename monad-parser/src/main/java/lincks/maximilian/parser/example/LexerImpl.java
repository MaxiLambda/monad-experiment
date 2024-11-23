package lincks.maximilian.parser.example;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.impl.monad.MParser;
import lincks.maximilian.parser.Lexer;
import lincks.maximilian.parser.token.Symbol;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.function.Predicate.not;
import static lincks.maximilian.impl.monad.MList.toMList;
import static lincks.maximilian.parser.token.SyntaxToken.L_BRACE;
import static lincks.maximilian.parser.token.SyntaxToken.R_BRACE;

/**
 * Lexer that supports literals without any special indication.
 * No Operator should be part of another operator, as this can lead to parsing problems.
 *
 * {@link DelimitedLexer} is the faster implementation at the cost of a literal delimiter.
 */
@RequiredArgsConstructor
public class LexerImpl implements Lexer<String> {
    private final static MList<Symbol> braceSymbols = new MList<>(L_BRACE.getSymbol(), R_BRACE.getSymbol());
    private final static Predicate<Character> spacyChar = c -> Pattern.matches("^(\\s|\\n|\\r)$", c + "");
    private final MList<Symbol> operatorSymbols;


    public MList<Symbol> getSymbols(String input) {

        String cleanedInput = input.replaceAll("(^[\\s\\n\\r]*)|([\\s\\n\\r]*$)", "");

        MList<MParser<Character, Symbol>> operatorParsers = fromSymbols(operatorSymbols);
        MList<MParser<Character, Symbol>> braceParsers = fromSymbols(braceSymbols);
        MParser<Character, Symbol> literalParser =
                MParser.matching(spacyChar)
                        .some2()
                        .then(() ->
                                MParser.matching(not(spacyChar))
                                .many3()
                                .consumeAll(spacyChar)
                                .map(list -> list.foldr((val, acc) -> val + acc, ""))
                                .map(Symbol::new));

        MParser<Character, MList<Symbol>> symbolParser = braceParsers
                .mplus(operatorParsers)
                .mplus(new MList<>(literalParser))
                .foldr(MParser::either, MParser.<Character, Symbol>empty())
                .many2();

        var xxx = symbolParser.parse(cleanedInput.chars().mapToObj(c -> (char) c)
                        .collect(toMList()))
                .filter(r -> r.remainingTokens().isEmpty())
                .map(MParser.ParseResult::value)
                //every symbol must be an operator or don't contain an operator AND the concatenation of sequential non-operator symbols doesn't contain an Operator
                .filter(syms -> syms.all(sym ->
                        //check if the symbol is reserved/an operator
                        reservedSymbols().any(sym::equals) ||
                                //check if the symbol does not contain any operators
                                !reservedSymbols().any(op -> sym.symbol().contains(op.symbol())))
                        &&
                        //check the concatenation of sequential symbols not containing any operators
                        //get sequential non-operators -> MList<MList<Symbol>>
                        syms.splitAt(sym -> reservedSymbols().any(sym::equals))
                                //concatenate them -> MList<String>
                                .map(seq -> seq.map(Symbol::symbol).foldr(String::concat, ""))
                                //check non contains an operator
                                .all(not(seq -> reservedSymbols().map(Symbol::symbol).any(seq::contains)))
                );

        return xxx.head();
    }

    private MList<MParser<Character, Symbol>> fromSymbols(MList<Symbol> symbols) {
        return symbols.map(s -> MParser.accumulating(s.symbol().chars().mapToObj(c -> (char) c).map(MParser::tokenMatching).collect(toMList())

                        ).map(list -> list.foldr((val, acc) -> acc + val, ""))
                        .map(Symbol::new)
                        .consumeAll(spacyChar)
        );
    }

    private MList<Symbol> reservedSymbols() {
        return operatorSymbols.mplus(new MList<>("(", ")").map(Symbol::new));
    }
}
