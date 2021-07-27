/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: OperatorType.java
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

package net.colors_wind.compiler.trans;

import net.colors_wind.compiler.lex.TokenType;

public enum OperatorType {

    PROGRAM("program"),
    ASSIGNMENT(":="),
    JUMP_IF_LESS_THAN("j<"),
    JUMP_IF_LESS_OR_EQUAL("j<="),
    JUMP_IF_MORE_THAN("j>"),
    JUMP_IF_MORE_OR_EQUAL("j>="),
    JUMP_IF_EQUAL("j="),
    JUMP_IF_NOT_EQUAL("j<>"),
    JUMP_NO_CONDITION("j"),
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    OR("or"),
    AND("and"),
    NOT("not"),
    END("sys");

    private final String name;

    OperatorType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static OperatorType fromTokenType(TokenType type) {
        switch(type) {
            case PLUS:
                return PLUS;
            case MINUS:
                return MINUS;
            case MULTIPLY:
                return MULTIPLY;
            case DIVISION:
                return DIVIDE;
            case OR:
                return OR;
            case AND:
                return AND;
            case NOT:
                return NOT;
            case LESS_THAN:
                return JUMP_IF_LESS_THAN;
            case LESS_OR_EQUAL:
                return JUMP_IF_LESS_OR_EQUAL;
            case MORE_THAN:
                return JUMP_IF_MORE_THAN;
            case MORE_OR_EQUAL:
                return JUMP_IF_MORE_OR_EQUAL;
            case EQUAL:
                return JUMP_IF_EQUAL;
            case NOT_EQUAL:
                return JUMP_IF_NOT_EQUAL;
            default:
                throw new IllegalArgumentException("无法将 TokenType 转换为 OperatorType: "  + type.name());
        }
    }

}
