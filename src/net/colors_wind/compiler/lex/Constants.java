/*
 * MIT License
 *
 * Copyright (c) 2021 ColorsWind
 *
 * File: Constants.java
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

package net.colors_wind.compiler.lex;

import java.util.Arrays;
public class Constants {
    public static char[] concat(char[]... array) {
        char[] dest = new char[Arrays.stream(array).mapToInt(a -> a.length).sum()];
        int destPos = 0;
        for (char[] chars : array) {
            System.arraycopy(chars, 0, dest, destPos, chars.length);
            destPos += chars.length;
        }
        Arrays.sort(dest);
        return dest;
    }

    public static char[] range(char start, char end) {
        char[] dest = new char[end - start + 1];
        for(int i=0;i<dest.length;i++)
            dest[i] = (char)(start + i);
        return dest;
    }
    public static final char[] CHARSET_ALPHABET = concat(range('a', 'z'), range('A', 'Z'));
    public static final char[] CHARSET_DIGIT = range('0', '9');
    public static final char[] CHARSET_BLANK = {' ', '\t', '\0'};
    public static final char[] CHARSET_DELIMITER = concat(
            new char[]{'+', '-', '*', '/', '=', '<', '>', '(', ')', '[', ']', ':', '.', ';', ',', '\''}
            , CHARSET_BLANK);
    public static final char[] CHARSET = concat(CHARSET_ALPHABET, CHARSET_DIGIT, CHARSET_DELIMITER, CHARSET_BLANK);
    static {
        Arrays.sort(CHARSET_DELIMITER);
        Arrays.sort(CHARSET_BLANK);
    }

    public static boolean isDelimiter(char c) {
        return Arrays.binarySearch(CHARSET_DELIMITER, c) >= 0;
    }

    public static boolean isInCharSet(char c) {
        return Arrays.binarySearch(CHARSET, c) >= 0;
    }

    public static boolean isBlank(char c) {
        return Arrays.binarySearch(CHARSET_BLANK, c) >= 0;
    }

    public static boolean isAlphabet(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}
