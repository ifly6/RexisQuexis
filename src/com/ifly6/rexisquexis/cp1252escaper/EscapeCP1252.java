/*
 * Copyright (c) 2020 ifly6
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

package com.ifly6.rexisquexis.cp1252escaper;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EscapeCP1252 {

    public EscapeCP1252() {
    }

    public static String translateCharacter(String html) {
        return translateCharacter(html, 0, Integer.MAX_VALUE);
    }

    public static String translateCharacter(String html, int min, int max) {
        // eg &#147;
        if (!html.endsWith(";") || !html.startsWith("&#"))
            throw new IllegalArgumentException(String.format("input '%s' is invalid HTML numeric entity", html));

        int codePoint = Integer.parseInt(html
                .replace(";", "")
                .replace("&#", ""));
        if (codePoint < min || codePoint > max)
            throw new IllegalArgumentException(String.format("input '%s' code point outside valid range [%d, %d]",
                    html, min, max));

        return Charset.forName("Windows-1252")
                .decode(ByteBuffer.wrap(new byte[]{(byte) codePoint}))  // decode
                .toString();
    }

    public static String unescape(String text) {
        Pattern p = Pattern.compile("&#\\d+;");
        Matcher m = p.matcher(text);

        StringBuffer sb = new StringBuffer();
        while (m.find())
            m.appendReplacement(sb, translateCharacter(m.group()));
        m.appendTail(sb);  // mysterious

        return sb.toString();
    }
}
