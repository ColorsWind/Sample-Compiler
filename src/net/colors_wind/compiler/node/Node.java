/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: Node.java
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

import net.colors_wind.compiler.parse.CodePos;
import net.colors_wind.compiler.trans.Symbol;
import net.colors_wind.compiler.trans.SymbolType;

public abstract class Node {
    protected int codePos;

    protected Node(int codePos) {
        this.codePos = codePos;
    }

    // Immediate number NODE or Symbol NODE

    public Object getImmediate() {
        throw new UnsupportedOperationException();
    }
    public String getVarName() {
        throw new UnsupportedOperationException();
    }
    public Object getVarOrImmediate() {
        throw new UnsupportedOperationException();
    }
    public Symbol getSymbol() {
        throw new UnsupportedOperationException();
    }
    public SymbolType getSymbolType() {
        throw new UnsupportedOperationException();
    }

    // Boolean NODE

    public void setTrueGo(int pos) {
        throw new UnsupportedOperationException();
    }
    public void setFalseGo(int pos) {
        throw new UnsupportedOperationException();
    }
    public void setTrueGo(CodePos trueGo) {
        throw new UnsupportedOperationException();
    }
    public void setFalseGo(CodePos falseGo) {
        throw new UnsupportedOperationException();
    }

    public CodePosReference getTrueGo() {
        throw new UnsupportedOperationException();
    }
    public CodePosReference getFalseGo() {
        throw new UnsupportedOperationException();
    }

    // Base method


    public final int getCodePos() {
        return this.codePos;
    }
    public final void setCodePos(int codePos) {
        this.codePos = codePos;
    }
    public abstract NodeType getType();
}
