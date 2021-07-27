/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: Main.java
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

package net.colors_wind.compiler;

import java.awt.*;
import java.util.Arrays;
import java.util.Scanner;


import static java.lang.System.out;


public class Main {

    public static String welcome(Scanner scanner) {
        out.println("Copyright ColorsWind 2021");
        out.println();
        out.println("请输入程序名称: ");
        String name = scanner.nextLine();
        out.println("请输入SAMPLE语言源代码, 按两下Enter结束.");
        return name;
    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "--lex":
                    LexMain.main(new String[0]);
                    return;
                case "--icode":
                    ICodeMain.main(new String[0]);
                    return;
                case "--ui":
                    UIMain.main(new String[0]);
                    return;
                case "--help":
                    break;
                default:
                    out.println("未知参数: " + Arrays.toString(args));
                    break;
            }
        } else if (System.console() == null && !GraphicsEnvironment.isHeadless()){
            UIMain.main(new String[0]);
            return;
        }
        out.println("SampleCompiler [--Option]:");
        out.println(" --help 显示帮助");
        out.println(" --lex 命令行词法生成器");
        out.println(" --icode 中间代码生成器");
        out.println(" --ui 使用UI");
    }
}
