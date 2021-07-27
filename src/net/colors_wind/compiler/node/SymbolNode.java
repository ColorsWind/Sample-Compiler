/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: SymbolNode.java
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

package net.colors_wind.compiler.node;

import net.colors_wind.compiler.trans.Symbol;
import net.colors_wind.compiler.trans.SymbolType;

public class SymbolNode extends Node {
    private final Symbol symbol;

    public SymbolNode(Symbol symbol) {
        this(-1, symbol);
    }
    public SymbolNode(int codePos, Symbol symbol) {
        super(codePos);
        this.symbol = symbol;
    }

    @Override
    public NodeType getType() {
        return NodeType.VARIABLE;
    }

    @Override
    public String getVarName() {
        return symbol.getName();
    }

    @Override
    public Object getVarOrImmediate() {
        return getVarName();
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public SymbolType getSymbolType() {
        return symbol.getType();
    }
}
