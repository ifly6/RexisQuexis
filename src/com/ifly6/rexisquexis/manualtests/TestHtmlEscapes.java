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

package com.ifly6.rexisquexis.manualtests;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class TestHtmlEscapes {
    public static void main(String[] args) throws IOException {
        String w1252 = "&#126;&#151;&#161;";
        String output = StringEscapeUtils.unescapeHtml4(w1252);
        System.out.println(output);
        System.out.println(output.chars().mapToLong(Long::valueOf)
                .boxed().collect(Collectors.toList()));

        byte[] bytes = output.getBytes(StandardCharsets.UTF_8);
        String reenc = new String(bytes, "Windows-1252");
        System.out.println("re-encoded: " + reenc);
        System.out.println("code points: " + reenc.chars().mapToLong(Long::valueOf)
                .boxed().collect(Collectors.toList()));

        Parser.unescapeEntities(w1252, true);
    }
}
