package lincks.maximilian.impl;

import lincks.maximilian.applicative.ApplicativeConstructor;
import lincks.maximilian.monads.Monad;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MParser<S, T> implements Monad<MParser<S, ?>, T> {

    private final Function<List<S>, List<ParseResult<S, T>>> parse;

    public MParser(Function<List<S>, List<ParseResult<S, T>>> parse) {
        this.parse = parse;
    }

    @ApplicativeConstructor
    public MParser(T value) {
        this.parse = (input) -> List.of(new ParseResult<>(value, input));
    }

    public static <S, T> MParser<S, T> unwrap(Monad<MParser<S, ?>, T> m) {
        return (MParser<S, T>) m;
    }

    public static <S> MParser<S, S> matching(Predicate<S> f) {
        return new MParser<>(input ->
                input.isEmpty() || !f.test(input.getFirst())
                        ? List.of()
                        : List.of(new ParseResult<>(input.getFirst(), input.subList(1, input.size())))
        );
    }

    public static <S> MParser<S, S> tokenMatching(S token) {
        return matching(token::equals);
    }

    public static <S> MParser<S, Void> empty() {
        return new MParser<>(null);
    }

    @Override
    public <R> MParser<S, R> bind(Function<T, Monad<MParser<S, ?>, R>> f) {
        return new MParser<>(input ->
                parse(input).stream()
                        .map(result ->
                                unwrap(f.apply(result.value))
                                        .parse(result.remainingTokens))
                        .flatMap(List::stream)
                        .toList());
    }

    @Override
    public <R> MParser<S, R> map(Function<T, R> f) {
        return unwrap(Monad.super.map(f));
    }

    @Override
    public <R> MParser<S, R> then(Supplier<Monad<MParser<S, ?>, R>> f) {
        return unwrap(Monad.super.then(f));
    }

    public List<ParseResult<S, T>> parse(List<S> tokens) {
        return parse.apply(tokens);
    }

    public <R, A> MParser<S, R> then(BiFunction<T, A, R> combine, Monad<MParser<S, ?>, A> other) {
        return bind(i -> other.bind(j -> new MParser<>(combine.apply(i, j))));
    }

    /**
     * Tries this and other parser on the same input, joins the results
     *
     * @param other the other parser.
     * @return joined results of both parsers.
     */
    public MParser<S, T> plus(MParser<S, T> other) {
        return new MParser<>(input -> {
            List<ParseResult<S, T>> result = new ArrayList<>();
            result.addAll(parse(input));
            result.addAll(other.parse(input));
            return result;
        });
    }

    /**
     * Same as plus but returns only one result at max
     *
     * @param other the other parser.
     * @return zero or one result. Results from this parser are preferred.
     */
    public MParser<S, T> either(MParser<S, T> other) {
        return new MParser<>(input -> {
            List<ParseResult<S, T>> result = plus(other).parse(input);
            return result.isEmpty() ? result : List.of(result.getFirst());
        });
    }

    /**
     * Applies the current parser as often as possible
     *
     * @return a parser appling the current parse as often as possible but at least once.
     */
    public MParser<S, List<T>> many() {
        return bind(x -> many().bind(xs -> {
            ArrayList<T> results = new ArrayList<>();
            results.add(x);
            results.addAll(xs);
            return new MParser<S, List<T>>(results);
        }).either(new MParser<>(List.of(x))));
    }

    /**
     * Creates a Parser which applies the current parser and then, if possible the other parser.
     * Can be used to optionally add elements to a parsed list etc...
     *
     * @param combine if other can parse successfully then use this function to merge its value
     *                with the current parsing result.
     * @param other   the parser to apply if possible.
     * @param <A>     the type wrapped by the other parser
     * @return A parser which definitely parses the same as this parser but might parse using other afterward.
     */
    public <A> MParser<S, T> maybe(BiFunction<T, A, T> combine, MParser<S, A> other) {
        return new MParser<>((List<S> input) ->
                parse(input).stream().flatMap(outer -> {
                            List<ParseResult<S, A>> results = other.parse(outer.remainingTokens);
                            return results.isEmpty() ? Stream.of(outer) : results.stream().map(inner ->
                                    new ParseResult<>(combine.apply(outer.value, inner.value), inner.remainingTokens));
                        }
                ).toList()
        );
    }

    /**
     * @return a new version of the current parser. If the parsing fails the result is {@link Maybe#nothing()}.
     */
    public MParser<S, Maybe<T>> maybeMParser() {
        return new MParser<>(input -> {
            List<ParseResult<S, Maybe<T>>> results = map(Maybe::new).parse(input);
            return results.isEmpty() ? List.of(new ParseResult<>(Maybe.nothing(), input)) : results;
        });
    }

    /**
     * Wrapper for the Result of Running a Parser.
     *
     * @param value           the parsed value.
     * @param remainingTokens all unparsed tokens.
     * @param <S>             the type of the tokens.
     * @param <T>             the type of the result.
     */
    public record ParseResult<S, T>(T value, List<S> remainingTokens) {
    }
}
