package lincks.maximilian.parser;

import lincks.maximilian.impl.monad.Either;
import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.parser.parser.ast.AstExpression;
import lincks.maximilian.parser.parser.ast.Expression;
import lincks.maximilian.parser.parser.ast.Literal;
import lincks.maximilian.parser.parser.ast.SymbolLiteral;
import lincks.maximilian.parser.parser.ast.productions.Production;
import lincks.maximilian.parser.token.OperatorToken;
import lincks.maximilian.parser.token.Symbol;

import java.util.*;

import static lincks.maximilian.parser.parser.ast.productions.Production.INITIAL_PRODUCTION;
import static lincks.maximilian.parser.parser.ast.productions.Production.isEpsilonProduction;
import static lincks.maximilian.parser.token.SyntaxToken.L_BRACE;
import static lincks.maximilian.parser.token.SyntaxToken.R_BRACE;

public class Parser<T> {
    private final Lexer lexer;
    private final Map<Symbol, OperatorToken<T>> operators;

    //highes operator strength in operatorStrength
    private final int maxStrength;
    //mapping of OperatorToken.getLevel() to minimal Integer > 0 preserving "<" relation between levels
    private final Map<Integer, Integer> operatorStrength;

    public Parser(Lexer lexer, Map<Symbol, OperatorToken<T>> operators) {
        this.lexer = lexer;
        this.operators = operators;

        HashMap<Integer, Integer> operatorStrength = new HashMap<>();
        List<Integer> levels = operators.values().stream().map(OperatorToken::getLevel).distinct().sorted().toList();
        for (int i = 0; i < levels.size(); i++) {
            operatorStrength.put(levels.get(i), i);
        }
        this.operatorStrength = operatorStrength;
        this.maxStrength = levels.size() - 1; //used to determine when the last level of precedence is reached
    }

    public AstExpression<T> run(String input) {
        Deque<Symbol> symbols = new ArrayDeque<>(lexer.getSymbols(input).toList());
        System.out.println(symbols);
        //translate Symbols into expressions with the parsing-table from the readme
        //if a symbol is not in operators.values(), L_BRACE.getSymbol() or R_BRACE.getSymbol() it has to be a Literal
        return generateAst(INITIAL_PRODUCTION.apply(operatorStrength::get), symbols);
    }

    private AstExpression<T> generateAst(Either<MList<Production>, Production.OperatorProduction> production, Deque<Symbol> symbols) {
        switch (production) {
            case Either.Left<MList<Production>, Production.OperatorProduction> v -> {
                System.out.println("LIST: " + v.value());
            }
            case Either.Right<MList<Production>, Production.OperatorProduction> v -> {
                System.out.println("OPERATOR (" + v.value().operator() + "): " + v.value().productions());
            }
        }
        return switch (production) {
            case Either.Left<MList<Production>, Production.OperatorProduction> v -> {
                var x = generateAstList(v.value(), symbols);
                if (x.size() > 1) {
                    if (x.tail().head() instanceof Expression<T> expression) {
                        //infix expressions are created here
                        yield doInfix(expression, x.head());
                    }
                    System.err.printf("Should '%s' be of size 1%n", x);
                }
                yield x.head();
            }
            case Either.Right<MList<Production>, Production.OperatorProduction> v ->
                    generateAstOperatorExpression(v.value(), symbols);
        };

    }

    private MList<AstExpression<T>> generateAstList(MList<Production> productions, Deque<Symbol> symbols) {
        MList<AstExpression<T>> expressions = new MList<>();
        MList<Production> prods = productions;
        while (!prods.isEmpty()) {
            Production production = prods.head();
            prods = prods.tail();

            Symbol symbol = symbols.peek();
            boolean bracedExpression = production.hasType(Production.Type.LAST_PRODUCTION) && L_BRACE.getSymbol().equals(symbol);

            if (bracedExpression) {
                //remove "("
                var removedL = symbols.removeFirst();
                if (!removedL.equals(L_BRACE.getSymbol())) {
                    System.err.println("Something went wrong");
                }
            }
            var next = production.nextProductions(symbol, operators, maxStrength);
            if (!isEpsilonProduction(next)) {
                expressions = expressions.append(generateAst(next, symbols));
                if (bracedExpression) {
                    //remove ")"
                    var removedR = symbols.removeFirst();
                    if (!removedR.equals(R_BRACE.getSymbol())) {
                        System.err.println("Something went wrong");
                    }
                }
            }
        }
        return expressions;
    }

    private AstExpression<T> generateAstOperatorExpression(Production.OperatorProduction operatorProduction, Deque<Symbol> symbols) {
        if (operatorProduction.productions().isEmpty()) {
            //No parameter expression => is a literal
            var removed = symbols.removeFirst();
            System.out.println(removed);
            return new SymbolLiteral<>(operatorProduction.operator());
        }
        var removed = symbols.removeFirst();
        System.out.println(removed);
        return new Expression<>(operatorProduction.operator(), generateAstList(operatorProduction.productions(), symbols));
    }

    //evaluates left to right
    private AstExpression<T> doInfix(Expression<T> expression, AstExpression<T> literal) {
        //if expression.getArgs().size() == 1 -> base case
        if(expression.getArgs().size() == 2 && expression.getArgs().tail().head() instanceof Expression<T> inner) {
            var args = new MList<>(literal, expression.getArgs().head());
            return doInfix(inner, new Expression<>(expression.getSymbol(),args));
        } else {
            return new Expression<>(expression.getSymbol(), expression.getArgs().prepend(literal));
        }
    }
}
