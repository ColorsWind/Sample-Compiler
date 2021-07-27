/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: CharExpression.java
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

import net.colors_wind.compiler.trans.SymbolType;
import net.colors_wind.compiler.lex.ListLexer;
import net.colors_wind.compiler.lex.Token;
import net.colors_wind.compiler.lex.TokenType;
import net.colors_wind.compiler.node.ImmediateNode;
import net.colors_wind.compiler.node.Node;
import net.colors_wind.compiler.node.SymbolNode;
import net.colors_wind.compiler.trans.Symbol;

import java.util.Optional;

public class CharExpression {
    private final Program program;
    private final ListLexer lexer;

    public CharExpression(Program program, ListLexer lexer) {
        this.program = program;
        this.lexer = lexer;
    }

    public Optional<Node> parseCharExpression() {
        Token token = lexer.preNextAndCheckEnd();
        if (token.getType() == TokenType.C_STRING) {
            lexer.next();
            Node node = new ImmediateNode(token.getPhrase(), SymbolType.CHAR);
            return Optional.of(node);
        } else if (token.getType() == TokenType.IDENTIFIER) {
            Optional<Symbol> symbolOptional = program.lookupAndCheck(token.getPhrase());
            return symbolOptional.map(symbol -> {
                lexer.next();
                return new SymbolNode(symbol);
            });
        } else {
            lexer.error("无法解析字符表达式: 未知Token: " + token);
            return Optional.empty();
        }
    }
}
