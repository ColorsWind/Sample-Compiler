/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: LexMain.java
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
 * LIABILITY, WHETHER IN000 AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.colors_wind.compiler;

import net.colors_wind.compiler.lex.Lexer;
import net.colors_wind.compiler.lex.Token;
import net.colors_wind.compiler.lex.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;

public class LexMain {



    public static List<String> input(Scanner scanner) {
        List<String> list = new ArrayList<>();
        String line;

        while(!(line = scanner.nextLine()).isEmpty()) {
            list.add(line);
        }
        return list;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = Main.welcome(scanner);
        List<String> list = input(scanner);
        out.println("对程序 " + name + " 进行词法分析, 输出Token串: ");
        Lexer lexer = new Lexer();
        for(int i=0;i<list.size();i++) {
            lexer.input("Line" + (i + 1), list.get(i));
            while (lexer.hasNext()) {
                Token token = lexer.preNext();
                if (token.getType() != TokenType.EOF)
                    System.out.print(token.toString() + " ");
                lexer.next();

            }
            out.println();
        }
        scanner.close();

    }
}
