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

package com.ifly6.rexisquexis.tests;

import com.ifly6.rexisquexis.GAResolution;
import com.ifly6.rexisquexis.cp1252escaper.EscapeCP1252;
import com.jcabi.xml.XML;

import java.io.IOException;

public class TestCP1252Escaper {

    public static void main(String[] args) {

        System.out.println(EscapeCP1252.unescape("arma virumque cano&#133;"));
        System.out.println(EscapeCP1252.unescape("&#147;bread and circuses&#148;"));
        System.out.println(EscapeCP1252.unescape("the romans say it costs 10&#128;"));
        System.out.println(EscapeCP1252.unescape("caligula&#153;"));

        System.out.println("\n------------------------");
        System.out.println("Getting text from NS API");
        System.out.println("------------------------\n");
        try {
            XML xml = GAResolution.queryApi(528);

            String text = GAResolution.cleanResolutionText(
                    xml.xpath("/WA/RESOLUTION/DESC/text()").get(0)
                            .replaceFirst("<!\\[CDATA\\[", "")
                            .replace("]]>", ""));
            System.out.println(text);

        } catch (IOException e) {
            System.err.println("Can't get text from NS. Shucks, bud.");
            e.printStackTrace();
        }
    }
}
