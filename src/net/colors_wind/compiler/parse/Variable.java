/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: Variable.java
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

import net.colors_wind.compiler.lex.ListLexer;
import net.colors_wind.compiler.lex.Token;
import net.colors_wind.compiler.lex.TokenType;
import net.colors_wind.compiler.trans.Symbol;
import net.colors_wind.compiler.trans.SymbolTable;
import net.colors_wind.compiler.trans.SymbolType;

import java.util.*;

public class Variable {
    private final Program program;
    private final ListLexer lexer;
    private SymbolTable symbolTable;
    public Variable(Program program) {
        this.program = program;
        this.lexer = program.getLexer();
    }

    public boolean parse() {
        symbolTable = program.getSymbolTable();
        if (lexer.preNextAndCheckEnd().getType() != TokenType.VAR)
            return true;
        lexer.next();
        parseVariableDefinition();
        while(lexer.preNextAndCheckEnd().getType() == TokenType.IDENTIFIER)
            parseVariableDefinition();
        return true;
    }

    public void parseVariableDefinition() {
        List<Symbol> symbols = parseVariableList(); // A, B, ...
        // :
        if (lexer.preNextAndCheckEnd().getType() != TokenType.COLON)
            lexer.error("??????`:`, ????????? <??????> var <???????????????> `:` <??????>;.");
        else
            lexer.next(); // :
        // DataType
        Token dataType = lexer.preNextAndCheckEnd();
        if (!dataType.getType().isDataType()) {
            lexer.error("???????????????, ?????? `<??????>`, ?????????????????????.");
        } else
            lexer.next(); // integer
        lexer.skipSemicolon(); // ;
        symbols.forEach(symbol -> symbol.setType(SymbolType.fromTokenType(dataType.getType())));
    }

    public Optional<Symbol> parseSingleVariable() {
        if (lexer.preNextAndCheckEnd().getType() != TokenType.IDENTIFIER) {
            lexer.error("???????????????, ????????? var `<?????????>`.");
            return Optional.empty();
        }
        Token id = lexer.next();
        if (symbolTable.lookup(id.getPhrase()).isPresent()) {
            lexer.error("???????????????: " + id.getPhrase());
            return Optional.empty();
        } else {
            Symbol symbol = symbolTable.enter(id.getPhrase());
            return Optional.of(symbol);
        }
    }

    public List<Symbol> parseVariableList() {
        List<Symbol> names = new ArrayList<>();
        parseSingleVariable().ifPresent(names::add);
        while(lexer.preNextAndCheckEnd().getType() == TokenType.COMMA) {
            lexer.next();
            parseSingleVariable().ifPresent(names::add);
        }
        return names;
    }

    public Program getProgram() {
        return program;
    }
}
