/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: TokenType.java
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

import java.util.*;
import java.util.stream.Collectors;

public enum TokenType {
    NOPE(0, "nope"),
    AND(1, "and"),
    ARRAY(2, "array"),
    BEGIN(3, "begin"),
    BOOL(4, "bool"),
    CALL(5, "call"),
    CASE(6, "case"),
    CHAR(7, "char"),
    CONSTANT(8, "constant"),
    DIM(9, "dim"),
    DO(10, "do"),
    ELSE(11, "else"),
    END(12, "end"),
    FALSE(13, "false"),
    FOR(14, "for"),
    IF(15, "if"),
    INPUT(16, "input"),
    INTEGER(17, "integer"),
    NOT(18, "not"),
    OF(19, "of"),
    OR(20, "or"),
    OUTPUT(21, "output"),
    PROCEDURE(22, "procedure"),
    PROGRAM(23, "program"),
    READ(24, "read"),
    REAL(25, "real"),
    REPEAT(26, "repeat"),
    SET(27, "set"),
    STOP(28, "stop"),
    THEN(29, "then"),
    TO(30, "to"),
    TRUE(31, "true"),
    UNTIL(32, "until"),
    VAR(33, "var"),
    WHILE(34, "while"),
    WRITE(35, "write"),
    IDENTIFIER(36, "identifier"),
    C_INTEGER(37, "c_integer"),
    C_STRING(38, "c_string"),
    LEFT_PARENTHESIS(39, "("),
    RIGHT_PARENTHESIS(40, ")"),
    MULTIPLY(41, "*"),
    COMMENT_END(42, "*/"),
    PLUS(43, "+"),
    COMMA(44, ","),
    MINUS(45, "-"),
    DOT(46, "."),
    DOUBLE_DOT(47, ".."),
    DIVISION(48, "/"),
    COMMENT_START(49, "/*"),
    COLON(50, ":"),
    ASSIGNMENT(51, ":="),
    SEMICOLON(52, ";"),
    LESS_THAN(53, "<"),
    LESS_OR_EQUAL(54, "<="),
    NOT_EQUAL(55, "<>"),
    EQUAL(56, "="),
    MORE_THAN(57, ">"),
    MORE_OR_EQUAL(58, ">="),
    LEFT_BRACKET(59, "["),
    RIGHT_BRACKET(60, "]"),
    EOF(61, "\0");

    private final int index;
    private final String name;

    TokenType(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public boolean isReserved() {
        switch(this) {
            case AND:
            case ARRAY:
            case BEGIN:
            case BOOL:
            case CALL:
            case CASE:
            case CHAR:
            case CONSTANT:
            case DIM:
            case DO:
            case ELSE:
            case EOF:
            case FALSE:
            case FOR:
            case IF:
            case INPUT:
            case INTEGER:
            case NOT:
            case OF:
            case OR:
            case OUTPUT:
            case PROCEDURE:
            case PROGRAM:
            case READ:
            case REAL:
            case REPEAT:
            case SET:
            case STOP:
            case THEN:
            case TO:
            case TRUE:
            case UNTIL:
            case VAR:
            case WHILE:
            case WRITE:
            case END:
                return true;
        }
        return false;
    }

    public boolean isSingularDelimiter() {
        switch(this) {
            case MULTIPLY:
            case PLUS:
            case COMMA:
            case MINUS:
            case DOT:
            case DIVISION:
            case COLON:
            case SEMICOLON:
            case LESS_THAN:
            case MORE_THAN:
            case LEFT_PARENTHESIS:
            case RIGHT_PARENTHESIS:
            case LEFT_BRACKET:
            case RIGHT_BRACKET:
                return true;
        }
        return false;
    }

    public boolean isInteger() {
        return this == C_INTEGER;
    }

    public boolean isString() {
        return this == C_STRING;
    }

    public boolean isIdentifier() {
        return this == IDENTIFIER;
    }

    public boolean isBoolean() {
        return this == TRUE || this == FALSE;
    }

    public boolean isConstant() {
        return isString() || isBoolean() || isInteger();
    }

    public boolean isDataType() {
        return this == INTEGER || this == BOOL || this == CHAR;
    }

    public boolean isPlusOrMinus() {
        return this == PLUS || this == MINUS;
    }

    public boolean isMultiplyOrDivide() {
        return this == MULTIPLY || this == DIVISION;
    }

    // for performance
    private static final Set<TokenType> RELATIONAL_OPERATORS =
            EnumSet.of(LESS_THAN, NOT_EQUAL, LESS_OR_EQUAL, MORE_OR_EQUAL, MORE_THAN, EQUAL);
    public boolean isRelationalOperators() {
        return RELATIONAL_OPERATORS.contains(this);
    }



    public boolean isDoubleDelimiter() {
        switch(this) {
            case NOT_EQUAL:
            case LESS_OR_EQUAL:
            case MORE_OR_EQUAL:
            case ASSIGNMENT:
            case COMMENT_START:
            case COMMENT_END:
            case DOUBLE_DOT:
                return true;
        }
        return false;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public static final Map<String, TokenType> RESERVED_WORDS = Arrays.stream(TokenType.values())
            .filter(TokenType::isReserved)
            .collect(Collectors.toMap(TokenType::getName, type -> type));


    public static Optional<TokenType> getReserved(String name) {
        return Optional.ofNullable(RESERVED_WORDS.get(name));
    }

}
