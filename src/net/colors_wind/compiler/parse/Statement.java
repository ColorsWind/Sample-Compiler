/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: Statement.java
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
import net.colors_wind.compiler.lex.ListLexer;
import net.colors_wind.compiler.lex.Token;
import net.colors_wind.compiler.lex.TokenType;
import net.colors_wind.compiler.node.BranchNode;
import net.colors_wind.compiler.node.Node;
import net.colors_wind.compiler.node.NodeType;
import net.colors_wind.compiler.trans.Symbol;

import java.util.Optional;

public class Statement {
    private final Program program;
    private final ListLexer lexer;

    public Statement(Program program) {
        this.program = program;
        this.lexer = program.getLexer();
    }

    public boolean parse() {
        if (lexer.preNextAndCheckEnd().getType() != TokenType.BEGIN) {
            lexer.error("格式不匹配， 预期: `begin` ... .");
        }
        lexer.next();
        parseStatementList();
        if (lexer.preNextAndCheckEnd().getType() != TokenType.END) {
            lexer.error("格式不匹配， 预期: ... `end`.");
        }
        lexer.next();
        return true;
    }

    private boolean parseStatement() {
        Token token = lexer.preNextAndCheckEnd();
        if (token.getType() == TokenType.IDENTIFIER)
            return parseAssignment();
        else if (token.getType() == TokenType.IF)
            return parseIf();
        else if (token.getType() == TokenType.WHILE)
            return parseWhile();
        else if (token.getType() == TokenType.REPEAT)
            return parseRepeat();
        else if (token.getType() == TokenType.BEGIN) {
            lexer.next(); // BEGIN
            boolean r = parseStatementList();
            if (lexer.preNextAndCheckEnd().getType() != TokenType.END)
                lexer.error("缺少 `END`.");
            else
                lexer.next(); // END
            return r;
        } else {
            lexer.error("无法解析语句: 意外读取到: `" + token + "`.");
            lexer.next();
            return false;
        }
    }

    private boolean parseStatementList() {
        parseStatement();
        lexer.skipSemicolon();
        while(lexer.preNextAndCheckEnd().getType() != TokenType.END) {
            parseStatement();
            lexer.skipSemicolon();
        }
        return true;
    }


    private boolean parseRepeat() {
        lexer.next(); // REPEAT
        int pos = program.nextPos();
        parseStatement();
        if (lexer.preNextAndCheckEnd().getType() != TokenType.UNTIL) {
            lexer.error("缺少 `until`.");
            return false;
        }
        lexer.next(); // UNIT
        program.getBooleanExpression().parseBooleanExpression().map(program.getBooleanExpression()::extendToBranch)
            .ifPresent(branchNode -> {
                branchNode.setTrueGo(program.nextPos());
                branchNode.setFalseGo(pos);
            });
        return true;
    }

    private boolean parseWhile() {
        lexer.next(); // WHILE
        program.getBooleanExpression().parseBooleanExpression().map(program.getBooleanExpression()::extendToBranch)
                .ifPresent(node -> {
                    if (lexer.preNextAndCheckEnd().getType() != TokenType.DO) {
                        lexer.error("缺少 `DO`");
                        return;
                    }
                    lexer.next(); // DO
                    node.setTrueGo(program.nextPos());
                    parseStatement();
                    program.emit(OperatorType.JUMP_NO_CONDITION, "-", "-", node.getCodePos());
                    node.setFalseGo(program.nextPos());
                });
        return true;
    }

    private boolean parseIf() {
        lexer.next();
        Optional<Node> nodeOptional = program.getBooleanExpression().parseBooleanExpression();
        if (!nodeOptional.isPresent())
            return false;
        BranchNode branchNode = program.getBooleanExpression().extendToBranch(nodeOptional.get());
        if (lexer.preNextAndCheckEnd().getType() == TokenType.THEN)
            lexer.next();
        else
            lexer.error("缺少 `then`");
        branchNode.setTrueGo(program.nextPos());
        CodePos end = new CodePos();
        lexer.preNextAndCheckEnd();
        parseStatement();
        branchNode.setFalseGo(program.nextPos());
        if (lexer.preNextAndCheckEnd().getType() == TokenType.ELSE) {
            lexer.next();
            CodePos pos = new CodePos();
            program.emit(OperatorType.JUMP_NO_CONDITION, "-", "-", pos);
            branchNode.setFalseGo(program.nextPos());
            parseStatement();
            pos.setPos(program.nextPos());
        }
        end.setPos(program.nextPos());
        return true;
    }

    private boolean parseAssignment() {
        Token variable = lexer.next();
        Optional<Symbol> symbolOptional = program.getSymbolTable().lookup(variable.getPhrase());
        if (!symbolOptional.isPresent()) {
            lexer.error("找不到变量: `" + variable.getPhrase() + "`, 请检查赋值表达式.");
            return false;
        }
        if (lexer.preNextAndCheckEnd().getType() != TokenType.ASSIGNMENT) {
            lexer.error("格式不匹配, 缺少 `:=`, 请检查赋值表达式.");
            return false;
        }
        lexer.next();
        program.getExpression().parseExpression().ifPresent(node -> {
            if (node.getType() == NodeType.VARIABLE && node.getType() == NodeType.IMMEDIATE) {
                lexer.error("赋值号右边不是一个右值表达式.");
                return;
            }
            Symbol symbol = symbolOptional.get();
            if (node.getSymbolType() != symbol.getType()) {
                lexer.error("类型不匹配: 尝试将一个 " + node.getSymbolType() + " 的右值赋值给 "
                        + symbol.getName() + ", 但 " + symbol.getName() + " 是 " + symbol.getType() + " 类型的.");
                return;

            }
            program.emit(OperatorType.ASSIGNMENT, node.getVarOrImmediate(), "-", symbolOptional.get().getName());
        });
        lexer.skipSemicolon(); // ;
        return true;
    }
}
