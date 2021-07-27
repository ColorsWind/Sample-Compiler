/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: Expression.java
 * Author: ColorsWind
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.colors_wind.compiler.parse;

import net.colors_wind.compiler.node.ImmediateNode;
import net.colors_wind.compiler.node.SymbolNode;
import net.colors_wind.compiler.trans.OperatorType;
import net.colors_wind.compiler.trans.Symbol;
import net.colors_wind.compiler.trans.SymbolType;
import net.colors_wind.compiler.lex.ListLexer;
import net.colors_wind.compiler.lex.Token;
import net.colors_wind.compiler.lex.TokenType;
import net.colors_wind.compiler.node.Node;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.function.*;


public class Expression {
    private final Program program;
    private final ListLexer lexer;

    public Expression(Program program) {
        this.program = program;
        this.lexer = program.getLexer();
    }

    public Optional<Node> parseExpression() {
        Token next = lexer.preNextAndCheckEnd();
        TokenType type = next.getType();
        Optional<Symbol> symbolOptional = lookup(next);
        if (type == TokenType.LEFT_PARENTHESIS) {
            return parseBrackets(this::parseExpression);
        } else if ((symbolOptional.isPresent() && symbolOptional.get().getType() == SymbolType.INTEGER)
                || type == TokenType.C_INTEGER || type == TokenType.MINUS) {
            return program.getArithmeticExpression().parseArithmeticExpression();
        } else if ((symbolOptional.isPresent() && symbolOptional.get().getType() == SymbolType.BOOLEAN)
                || type.isBoolean() || type == TokenType.NOT) {
            return program.getBooleanExpression().parseBooleanExpression();
        }
        return Optional.empty();
    }

    Optional<Node> parseBNFLoop(Supplier<Optional<Node>> nodeParser,
                                Predicate<TokenType> operatorPredicate, BiFunction<Node[], OperatorType, Node> merger) {
        Queue<Node> queue = new ArrayDeque<>(3);
        nodeParser.get().ifPresent(queue::offer);
        while(operatorPredicate.test(lexer.preNextAndCheckEnd().getType())) {
            Token operator = lexer.next();
            nodeParser.get().ifPresent(queue::offer);
            if (queue.size() >= 2) {
                Node node = merger.apply(new Node[]{queue.poll(), queue.poll()},
                        OperatorType.fromTokenType(operator.getType()));
                queue.offer(node);
            }
        }
        return queue.size() == 1 ? Optional.of(queue.poll()) : Optional.empty();
    }


    Optional<Node> parseImmediate(SymbolType symbolType, Function<String, Object> converter) {
        Token value = lexer.next();
        return Optional.of(new ImmediateNode(converter.apply(value.getPhrase()), symbolType));
    }

    Optional<Node> parseSymbol(SymbolType symbolType) {
        Token id = lexer.next();
        return program.lookupAndCheck(id.getPhrase()).flatMap(symbol -> {
            if (symbol.getType() != symbolType) {
                lexer.error("类型不匹配: " + symbol.getName() + " 不是 " + symbolType.name() + " 类型的.");
                return Optional.empty();
            }
            return Optional.of(new SymbolNode(symbol));
        });
    }


    Optional<Node> parseBrackets(Supplier<Optional<Node>> expressParser) {
        lexer.next();
        Optional<Node> node = expressParser.get();
        if (lexer.preNext().getType() == TokenType.RIGHT_PARENTHESIS)
            lexer.next();
        else
            lexer.error("缺少 `)`.");
        return node;
    }

    public Optional<Symbol> lookup(Token token) {
        if (token.getType() == TokenType.IDENTIFIER)
            return program.getSymbolTable().lookup(token.getPhrase());
        else
            return Optional.empty();
    }
}
