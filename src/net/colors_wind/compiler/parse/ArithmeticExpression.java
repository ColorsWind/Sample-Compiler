/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: ArithmeticExpression.java
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

import net.colors_wind.compiler.trans.OperatorType;
import net.colors_wind.compiler.trans.Symbol;
import net.colors_wind.compiler.trans.SymbolType;
import net.colors_wind.compiler.lex.ListLexer;
import net.colors_wind.compiler.lex.Token;
import net.colors_wind.compiler.lex.TokenType;
import net.colors_wind.compiler.node.Node;
import net.colors_wind.compiler.node.NodeType;
import net.colors_wind.compiler.node.SymbolNode;

import java.util.Optional;

public class ArithmeticExpression {
    private final Program program;
    private final ListLexer lexer;

    public ArithmeticExpression(Program program) {
        this.program = program;
        this.lexer = program.getLexer();
    }


    private Node arithmeticMerge(Node[] nodes, OperatorType operatorType) {
        Symbol symbol = program.getSymbolTable().newTmp();
        symbol.setType(SymbolType.INTEGER);
        Node node = new SymbolNode(program.nextPos(), symbol);
        if (operatorType == OperatorType.DIVIDE && nodes[1].getType() == NodeType.IMMEDIATE
                && Integer.parseInt(nodes[1].getImmediate().toString()) == 0) {
            lexer.error("除数不能为0.");
            program.emit(OperatorType.ASSIGNMENT,
                    nodes[0].getVarOrImmediate(),
                    "-",
                    symbol.getName());
        } else {
            program.emit(operatorType,
                    nodes[0].getVarOrImmediate(),
                    nodes[1].getVarOrImmediate(),
                    symbol.getName());
        }

        return node;
    }

    public Optional<Node> parseArithmeticExpression() {
        return program.getExpression().parseBNFLoop(this::parseArithmeticTerm, TokenType::isPlusOrMinus, this::arithmeticMerge);
    }


    public Optional<Node> parseArithmeticTerm() {
        return program.getExpression().parseBNFLoop(this::parseArithmeticFactor, TokenType::isMultiplyOrDivide, this::arithmeticMerge);
    }

    public Optional<Node> parseArithmeticFactor() {
        if (lexer.preNextAndCheckEnd().getType() == TokenType.MINUS) {
            lexer.next();
            return parseArithmeticFactor().map(node -> {
                Symbol symbol = program.getSymbolTable().newTmp();
                symbol.setType(SymbolType.INTEGER);
                Node minus = new SymbolNode(symbol);
                program.emit(OperatorType.MINUS, node.getVarOrImmediate(), "-", symbol.getName());
                return minus;
            });
        }
        return parseArithmeticValue();
    }

    public Optional<Node> parseArithmeticValue() {
        Token token = lexer.preNextAndCheckEnd();
        if (token.getType() == TokenType.C_INTEGER)
            return program.getExpression().parseImmediate(SymbolType.INTEGER, Integer::valueOf);
        else if (token.getType() == TokenType.IDENTIFIER)
            return program.getExpression().parseSymbol(SymbolType.INTEGER);
        else if (token.getType() == TokenType.LEFT_PARENTHESIS)
            return program.getExpression().parseBrackets(this::parseArithmeticExpression);
        else
            lexer.error("格式不匹配， 预期: `<整数>│<标识符>│(<算术表达式)`");
        return Optional.empty();
    }
}
