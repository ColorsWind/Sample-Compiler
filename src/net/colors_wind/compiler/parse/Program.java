/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: Program.java
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
import net.colors_wind.compiler.trans.SymbolTable;
import net.colors_wind.compiler.trans.TACode;
import net.colors_wind.compiler.lex.ListLexer;
import net.colors_wind.compiler.lex.Token;
import net.colors_wind.compiler.lex.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Program {
    private final ListLexer lexer;
    private final SymbolTable symbolTable;
    private final Statement statement;
    private final Expression expression;
    private final Variable variable;
    private final List<TACode> codes;
    private final ArithmeticExpression arithmeticExpression;
    private final BooleanExpression booleanExpression;
    private String programName;



    public Program(List<String> input) {
        this.lexer = new ListLexer(input);
        this.symbolTable = new SymbolTable();
        this.statement = new Statement(this);
        this.expression = new Expression(this);
        this.variable = new Variable(this);
        this.codes = new ArrayList<>();
        this.arithmeticExpression = new ArithmeticExpression(this);
        this.booleanExpression = new BooleanExpression(this);
    }

    public boolean parse() {
        parseProgram();
        // variable
        Variable variable = new Variable(this);
        variable.parse();
        // statement
        Statement statement = new Statement(this);
        statement.parse();
        // .
        if (lexer.preNextAndCheckEnd().getType() != TokenType.DOT) {
            lexer.error("缺少程序结束符 `.` .");
            return false;
        }
        lexer.next();
        // EOF
        if (lexer.preNext().getType() != TokenType.EOF) {
            lexer.error("已读取到程序结束符, 后意外发现Token序列.");
        }
        lexer.next();
        this.emit(OperatorType.END, "-", "-", "-");
        return true;
    }


    public boolean parseProgram() {
        if (lexer.preNextAndCheckEnd().getType() != TokenType.PROGRAM)
            lexer.error("缺少 program 声明程序名称.");
        else
            lexer.next(); // program
        Token program = lexer.preNextAndCheckEnd();
        if (program.getType() != TokenType.IDENTIFIER)
            lexer.error("格式不匹配, 请检查 program `<标识符>` .");
        else
            lexer.next(); // example
        programName = program.getPhrase();
        this.emit(OperatorType.PROGRAM, programName, "-", "-");
        lexer.skipSemicolon(); // ;
        return true;
    }

    protected ListLexer getLexer() {
        return lexer;
    }

    public String getProgramName() {
        return programName;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public Statement getStatement() {
        return statement;
    }

    public Expression getExpression() {
        return expression;
    }

    public Variable getVariable() {
        return variable;
    }

    public TACode emit(OperatorType type, Object obj1, Object obj2, Object obj3) {
        TACode code = new TACode(nextPos(), type, obj1, obj2, obj3);
        this.codes.add(code);
        return code;
    }

    public int nextPos() {
        return codes.size();
    }

    public Optional<Symbol> lookupAndCheck(String name) {
        Optional<Symbol> symbol = symbolTable.lookup(name);
        if (!symbol.isPresent())
            lexer.error("找不到符号: " + name);
        return symbol;
    }

    public ArithmeticExpression getArithmeticExpression() {
        return arithmeticExpression;
    }

    public BooleanExpression getBooleanExpression() {
        return booleanExpression;
    }

    public void printIntermediateCodes() {
        for(int i=0;i<codes.size();i++)
            System.out.println(i + ": " + codes.get(i));
    }

}
