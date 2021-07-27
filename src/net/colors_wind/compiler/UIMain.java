/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: UIMain.java
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

import net.colors_wind.compiler.ui.MainUI;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;

public class UIMain {


    public static void main(String[] args) {
        MainUI ui = new MainUI();
        PrintStream err = System.err;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.setOut(ui.new PrintInfoStream(System.out));
            System.setErr(ui.new PrintErrStream(System.err));
        } catch (Exception e) {
            e.printStackTrace(err);
        }
        JFrame frame = new JFrame();
        frame.setTitle("Sample Compiler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(ui.topPanel);
        frame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = frame.getSize();
        int x = screenSize.width / 2 - size.width / 2;
        int y = screenSize.height / 2 - size.height / 2;
        frame.setLocation(x, y);
        frame.setVisible(true);
    }
}
