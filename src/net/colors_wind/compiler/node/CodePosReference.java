/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: CodePosReference.java
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

public class CodePosReference {

    private CodePos codePos;

    public CodePosReference(CodePos codePos) {
        this.codePos = codePos;
    }

    public CodePosReference() {
        this(new CodePos());
    }

    public int getCodePos() {
        return codePos.getPos();
    }

    public void setCodePos(int pos) {
        codePos.setPos(pos);
    }

    public void set(CodePos codePos) {
        this.codePos = codePos;
    }

    @Override
    public String toString() {
        return codePos.toString();
    }

    public CodePos get() {
        return this.codePos;
    }
}
