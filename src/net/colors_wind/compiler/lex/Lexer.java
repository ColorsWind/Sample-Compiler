/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: Lexer.java
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

import java.util.NoSuchElementException;

public class Lexer {
    private String prefix;
    private String input;
    // index == size: TOKEN AVAILABLE
    // index == size: EOF
    // index >  size: NO MORE TOKEN
    private int index;

    private int preIndex;
    private Token preToken;

    public void input(String prefix, String str) {
        this.prefix = prefix;
        this.input = str;
        this.index = 0;
        this.preIndex = 0;
        this.preToken = null;
        this.skipBlank();
    }

    public boolean hasNext() {
        return index <= input.length();
    }

    public boolean isEOF() {
        return index == input.length();
    }

    public Token next()  throws NoSuchElementException {
        Token token = this.next0();
        this.skipBlank();
        return token;
    }

    public void skipBlank() {
        while((index < input.length()) && Constants.isBlank(input.charAt(index)))
            index++;
        if (index + 1 < input.length() && input.charAt(index) == '/' && input.charAt(index + 1) == '*') {
            index += 2;
            while(index < input.length()) {
                if (index + 1 < input.length() && input.charAt(index) == '*' && input.charAt(index + 1) == '/') {
                    index += 2;
                    return;
                }
                index++;
            }
            error("未结束的注释.");
        }

    }

    public Token preNext() throws NoSuchElementException {
        if (preToken != null)
            return preToken;
        int lastIndex = index;
        Token token;
        try {
            token = next();
        } catch (NoSuchElementException ex) {
            index = lastIndex;
            throw ex;
        }
        preIndex = index;
        preToken = token;
        index = lastIndex;

        return token;
    }


    public Token next0() throws NoSuchElementException {
        // support pre read
        if (preToken != null) {
            index = preIndex;
            Token token = preToken;
            preToken = null;
            return token;
        }

        if (index > input.length())
            throw new NoSuchElementException("没有更多的Token");
        else if (index == input.length()) {
            index++;
            return new Token(TokenType.EOF, "-");
        }
        char c = input.charAt(index);
        if (Constants.isAlphabet(c)) {
            return stateLetter();
        } else if (Constants.isDigit(c)) {
            return stateInteger();
        } else if (c == '\'') {
            return stateConstant();
        } else if (c == '(') {
            index++;
            return new Token(TokenType.LEFT_PARENTHESIS, "(");
        } else if (c == ')') {
            index++;
            return new Token(TokenType.RIGHT_PARENTHESIS, ")");
        } else if (c == '*') {
            index++;
            return new Token(TokenType.MULTIPLY, "*");
        } else if (c == '+') {
            index++;
            return new Token(TokenType.PLUS, "+");
        } else if (c == ',') {
            index++;
            return new Token(TokenType.COMMA, ",");
        } else if (c == '-') {
            index++;
            return new Token(TokenType.MINUS, "-");
        } else if (c == '.')  {
            // DOT or DOUBLE_DOT
            return stateDot();
        } else if (c == '/') {
            index++;
            return new Token(TokenType.DIVISION, "/");
        } else if (c == ':')  {
            // COLON or ASSIGNMENT
            return stateColon();
        } else if (c == ';') {
            index++;
            return new Token(TokenType.SEMICOLON, ";");
        } else if (c == '<') {
            // LESS, LESS_OR_EQUAL or NOT_EQUAL
            return stateLess();
        } else if (c == '=') {
            index++;
            return new Token(TokenType.EQUAL, "=");
        } else if (c == '>') {
            // MORE or MORE_OR_EQUAL
            return stateMore();
        } else if (c == '[') {
            index++;
            return new Token(TokenType.LEFT_BRACKET, "[");
        } else if (c == ']') {
            index++;
            return new Token(TokenType.RIGHT_BRACKET, "]");
        } else {
            error("未知字符: `" + c + "` (" + (int)c + ").");
            index++;
            return new Token(TokenType.NOPE, "ERROR");
        }
    }

    private Token stateLetter() {
        int begin = index;
        while (index<input.length() &&
                (Constants.isAlphabet(input.charAt(index)) || Constants.isDigit(input.charAt(index)))) {
            index++;
        }
        String str = input.substring(begin, index);
        return TokenType.getReserved(str).map(type -> new Token(type, str)).orElseGet(() -> new Token(TokenType.IDENTIFIER, str));
    }

    private Token stateConstant() {
        int begin = ++index;
        while (index<input.length() &&
                input.charAt(index) != '\'') {
            index++;
        }
        if (index == input.length()) {
            error("字符文字的行结尾不合法");
            return new Token(TokenType.NOPE, "ERROR");
        } else {
            return new Token(TokenType.C_STRING, input.substring(begin, index++));
        }

    }



    private Token stateInteger() {
        int begin = index;
        while (((index < input.length()) &&
                Constants.isDigit(input.charAt(index))))
            index++;
        if (!Constants.isDelimiter(safeRead()))
            error("缺少分界符.");
        String str;
        if (index == input.length())
            str = input.substring(begin);
        else
            str = input.substring(begin, index);

        return new Token(TokenType.C_INTEGER, str);
    }


    private Token stateMore() {
        index++;
        if (safeRead() == '=') {
            index++;
            return new Token(TokenType.MORE_OR_EQUAL, ">=");
        }
        return new Token(TokenType.MORE_THAN, ">");
    }

    private Token stateLess() {
        index++;
        char c = safeRead();
        if (c == '=') {
            index++;
            return new Token(TokenType.LESS_OR_EQUAL, "<=");
        } else if (c == '>'){
            index++;
            return new Token(TokenType.NOT_EQUAL, "<>");
        }
        return new Token(TokenType.LESS_THAN, "<");
    }

    private Token stateColon() {
        index++;
        if (safeRead() == '=') {
            index++;
            return new Token(TokenType.ASSIGNMENT, ":=");
        }
        return new Token(TokenType.COLON, ":");
    }


    private Token stateDot() {
        index++;
        if (safeRead() == '.')
            return new Token(TokenType.DOUBLE_DOT, "..");
        return new Token(TokenType.DOT, ".");
    }


    protected void info(String msg) {
        System.out.println(prefix + " " + msg);
    }

    protected void error(String msg) {
        System.err.println(prefix + ":" + (index + 1) + " " + msg);
        //index = input.length() + 1;
    }

    private char safeRead() {
        if (index < input.length())
            return input.charAt(index);
        return '\0';
    }


    public String getPrefix() {
        return prefix;
    }

    int getIndex() {
        return this.index;
    }
}
