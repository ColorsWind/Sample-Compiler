/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: BooleanExpression.java
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
import net.colors_wind.compiler.node.BranchNode;
import net.colors_wind.compiler.node.Node;
import net.colors_wind.compiler.node.NodeType;

import java.util.Optional;

public class BooleanExpression {
    private final Program program;
    private final ListLexer lexer;

    public BooleanExpression(Program program) {
        this.program = program;
        this.lexer = program.getLexer();
    }

    private Node andMerge(Node[] nodes, OperatorType operatorType) {
        BranchNode first = extendToBranch(nodes[0]);
        BranchNode second = extendToBranch(nodes[1]);
        first.setTrueGo(second.getCodePos());
        second.setFalseGo(first.getFalseGoPos());
        second.setCodePos(first.getCodePos());
        return second;
    }

    private Node orMerge(Node[] nodes, OperatorType operatorType) {
        BranchNode first = extendToBranch(nodes[0]);
        BranchNode second = extendToBranch(nodes[1]);
        second.setTrueGo(first.getTrueGoPos());
        first.setFalseGo(second.getCodePos());
        second.setCodePos(first.getCodePos());
        return second;
    }

    public Optional<Node> parseBooleanExpression() {
        return program.getExpression().parseBNFLoop(this::parseBooleanTerm, type -> type == TokenType.AND, this::andMerge);
    }

    public Optional<Node> parseBooleanTerm() {
        return program.getExpression().parseBNFLoop(this::parseBooleanFactor, type -> type == TokenType.OR, this::orMerge);
    }

    public Optional<Node> parseBooleanFactor() {
        if (lexer.preNextAndCheckEnd().getType() == TokenType.NOT) {
            lexer.next();
            parseBooleanFactor().ifPresent(node -> {
                int trueGo = node.getTrueGo().getCodePos();
                int falseGo = node.getFalseGo().getCodePos();
                node.setTrueGo(falseGo);
                node.setFalseGo(trueGo);
            });
        }
        return parseBooleanValue();
    }

    private Optional<Node> parseBooleanValue() {
        Token next = lexer.preNext();
        Optional<Symbol> symbolOptional = program.getExpression().lookup(next);
        if (next.getType().isBoolean()) {
            return program.getExpression().parseImmediate(SymbolType.BOOLEAN, Boolean::valueOf);
        } else if (symbolOptional.isPresent() && symbolOptional.get().getType() == SymbolType.BOOLEAN) {
            return program.getExpression().parseSymbol(SymbolType.BOOLEAN);
        } else if (next.getType() == TokenType.C_INTEGER ||
                (symbolOptional.isPresent() && symbolOptional.get().getType() == SymbolType.INTEGER)) {
            return parseRationalExpression();
        } else if (next.getType() == TokenType.LEFT_BRACKET) {
            return program.getExpression().parseBrackets(program.getExpression()::parseExpression);
        } else {
            lexer.error("解析布尔量失败: 意外读取到: `" + next + "`.");
            return Optional.empty();
        }
    }

    private Optional<Node> parseRationalExpression() {
        Optional<Node> ae1 = program.getArithmeticExpression().parseArithmeticExpression();
        if (!ae1.isPresent()) {
            lexer.error("缺少 `<算术表达式1>`, 请检查关系运算表达式.");
            return Optional.empty();
        }
        Token ro = lexer.preNextAndCheckEnd();
        if (!ro.getType().isRelationalOperators()) {
            lexer.error("缺少 `<关系运算符>`, 请检查关系运算表达式.");
            return Optional.empty();
        }
        lexer.next();
        Optional<Node> ae2 = program.getArithmeticExpression().parseArithmeticExpression();
        if (!ae2.isPresent()) {
            lexer.error("缺少 `<算术表达式2>`, 请检查关系运算表达式.");
            return Optional.empty();
        }
        Node rationalNode = new BranchNode(program.nextPos());
        program.emit(OperatorType.fromTokenType(ro.getType()),
                ae1.get().getVarOrImmediate(), ae2.get().getVarOrImmediate(), rationalNode.getTrueGo());
        program.emit(OperatorType.JUMP_NO_CONDITION, "-", "-", rationalNode.getFalseGo());
        return Optional.of(rationalNode);
    }

    BranchNode extendToBranch(Node node) {
        if (node.getType() == NodeType.BRANCH)
            return (BranchNode) node;
        BranchNode branchNode = new BranchNode(program.nextPos());
        program.emit(OperatorType.JUMP_IF_EQUAL, node.getVarOrImmediate(), "TRUE", branchNode.getTrueGo());
        program.emit(OperatorType.JUMP_NO_CONDITION, "-", "-", branchNode.getFalseGo());
        return branchNode;
    }
}
