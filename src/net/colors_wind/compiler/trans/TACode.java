/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: TACode.java
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

public class TACode {
    private final int id;
    private final OperatorType type;
    private Object obj1;
    private Object obj2;
    private Object obj3;

    public TACode(int id, OperatorType type, Object obj1, Object obj2, Object obj3) {
        this.id = id;
        this.type = type;
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.obj3 = obj3;
    }

    @Override
    public String toString() {
        return "(" + type.getName() + ", " + obj1.toString() + ", " + obj2.toString() + ", " + obj3.toString() + ")";
    }

    public OperatorType getType() {
        return type;
    }

    public Object getObj1() {
        return obj1;
    }

    public Object getObj2() {
        return obj2;
    }

    public Object getObj3() {
        return obj3;
    }

    public void setObj1(Object obj1) {
        this.obj1 = obj1;
    }

    public void setObj2(Object obj2) {
        this.obj2 = obj2;
    }

    public void setObj3(Object obj3) {
        this.obj3 = obj3;
    }
}
