/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: ListLexer.java
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

package net.colors_wind.compiler.lex;

import net.colors_wind.compiler.parse.UnexpectedEndException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ListLexer {
    private final List<String> input;
    private final Lexer lexer;
    private int index;

    public ListLexer(List<String> input) {
        if (input.size() == 0)
            throw new NoSuchElementException("输入为空.");
        this.input = new ArrayList<>(input);
        this.lexer = new Lexer();
        this.index = 0;
        this.lexer.input("Line1", input.get(0));
    }

    public boolean hasNext() {
        while(index < input.size()) {
            if (lexer.hasNext()) {
                return true;
            } else {
                if (index + 1 < input.size()) {
                    index++;
                    lexer.input("Line" + (index + 1), input.get(index));
                } else {
                    return false;
                }
            }
        }
        throw new RuntimeException("不会发生.");
    }

    public Token next() {
        if (lexer.isEOF() && index + 1 != input.size())
            moveToAvailable();
        return lexer.next();
    }

    public Token preNextAndCheckEnd() {
        if (lexer.isEOF())
            moveToAvailable();
        if (lexer.isEOF())
            throw new UnexpectedEndException(lexer.getPrefix() + ": 意外到达文件结尾.");
        return lexer.preNext();
    }

    public Token preNext() {
        if (lexer.isEOF())
            moveToAvailable();
        return lexer.preNext();
    }

    private void moveToAvailable() {
        while(lexer.isEOF())
            if (++index < input.size())
                lexer.input("Line" + (index + 1), input.get(index));
            else
                break;
    }

    public void error(String msg) {
        lexer.error(msg);
    }

    public void info(String msg) {
        lexer.info(msg);
    }

    public void skipSemicolon() {
        if (preNextAndCheckEnd().getType() == TokenType.SEMICOLON)
            next();
    }

    public void printlnLoc() {
        System.out.println("Line = "  + (index + 1));
        System.out.println("Col = " + (lexer.getIndex() + 1));
    }
}
