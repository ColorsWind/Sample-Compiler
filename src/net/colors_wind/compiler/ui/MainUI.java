/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: MainUI.java
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

package net.colors_wind.compiler.ui;

import net.colors_wind.compiler.lex.Lexer;
import net.colors_wind.compiler.lex.Token;
import net.colors_wind.compiler.lex.TokenType;
import net.colors_wind.compiler.parse.Program;

import javax.swing.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static java.lang.System.*;

public class MainUI {
    public JPanel topPanel;
    private JButton lexButton;
    private JButton icodeButton;
    private JButton resetButton;
    private JList<String> outputList;
    private JTextArea inputArea;
    private JButton pasteButton;
    private JButton copyButton;

    private final Object outputLock = new Object();
    private List<String> outputBuffer = new ArrayList<>();

    public MainUI() {
        lexButton.addActionListener(e -> {
            Lexer lexer = new Lexer();
            StringTokenizer str = new StringTokenizer(inputArea.getText(), "\n");
            try {
                for (int i = 0; str.hasMoreTokens(); i++) {
                    lexer.input("Line" + (i + 1), str.nextToken());
                    while (lexer.hasNext()) {
                        Token token = lexer.preNext();
                        if (token.getType() != TokenType.EOF)
                            out.print(token + " ");

                        lexer.next();
                    }
                    out.println();
                }
            } catch (Exception exception) {
                exception.printStackTrace(((PrintInfoStream) err).out);
                err.println(exception.getMessage());
            }
            updateList();
        });
        icodeButton.addActionListener(e -> {
            StringTokenizer str = new StringTokenizer(inputArea.getText(), "\n");
            List<String> input = new ArrayList<>();
            while (str.hasMoreTokens()) input.add(str.nextToken());
            try {
                Program program = new Program(input);
                program.parse();
                program.printIntermediateCodes();
            } catch (Exception exception) {
                exception.printStackTrace(((PrintInfoStream) err).out);
                err.println(exception.getMessage());
            }
            updateList();
        });
        resetButton.addActionListener(e -> {
            inputArea.setText("");
            updateList();
        });
        pasteButton.addActionListener(e -> {
            String text = ClipboardTookit.getSysClipboardText();
            if (text != null)
                inputArea.setText(inputArea.getText() + text);
        });
        copyButton.addActionListener(e -> {
            ListModel<String> model = outputList.getModel();
            int size = model.getSize();
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<size;i++) {
                String msg = model.getElementAt(i);
                if (msg.startsWith("<html>"))
                    msg = msg.substring("<html><font color=red>".length(),
                            msg.length() - "</font></html>".length());
                sb.append(msg);
                sb.append('\n');
            }
            ClipboardTookit.setSysClipboardText(sb.toString());
        });

    }

    private void updateList() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        List<String> buffer;
        synchronized (outputLock) {
            buffer = outputBuffer;
            outputBuffer = new ArrayList<>();
        }
        buffer.forEach(model::addElement);
        outputList.setModel(model);
        outputList.updateUI();
    }


    public class PrintInfoStream extends PrintStream {
        private final StringBuilder buffer;
        protected final PrintStream out;

        public PrintInfoStream(PrintStream out) {
            super(out);
            this.out = out;
            this.buffer = new StringBuilder();
        }

        protected void writeBufferUI(String s) {
            buffer.append(s);
        }


        @Override
        public void println() {
            out.println();
            String str;
            synchronized (buffer) {
                str = buffer.toString();
                buffer.setLength(0);
                synchronized (outputLock) {
                    outputBuffer.add(str);
                }
            }

        }

        @Override
        public void print(String s) {
            out.print(s);
            synchronized (buffer) {
                writeBufferUI(s);
            }
        }

        @Override
        public void println(String x) {
            out.println(x);
            String str;
            synchronized (buffer) {
                writeBufferUI(x);
                str = buffer.toString();
                buffer.setLength(0);
                synchronized (outputLock) {
                    outputBuffer.add(str);
                }
            }

        }

    }

    public class PrintErrStream extends PrintInfoStream {
        public PrintErrStream(PrintStream out) {
            super(out);

        }

        @Override
        protected void writeBufferUI(String s) {
            super.writeBufferUI("<html><font color=red>" + s + "</font></html>");
        }
    }
}
