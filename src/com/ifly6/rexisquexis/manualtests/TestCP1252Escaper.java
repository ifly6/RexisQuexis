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

import com.ifly6.rexisquexis.GAResolution;
import com.ifly6.rexisquexis.cp1252escaper.EscapeNumericEntities;
import com.ifly6.rexisquexis.io.RqForumUtilities;
import com.jcabi.xml.XML;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;

public class TestCP1252Escaper {

    public static void main(String[] args) {

        EscapeNumericEntities escaper = EscapeNumericEntities.WINDOWS_1252;
        System.out.println(escaper.unescape("virgil1:\t" + "arma virumque cano&#133;"));

        String virgil2 = "&quot;arma virumque cano&#133;&quot;";
        System.out.println("virgil2:\t" + escaper.unescape(virgil2));
        System.out.println("virgil2:\t" + StringEscapeUtils.unescapeHtml4(escaper.unescape(virgil2)));

        System.out.println(escaper.unescape("panem:\t" + "&#147;bread and circuses&#148;"));
        System.out.println(escaper.unescape("cave:\t" + "the romans say it costs 10&#128;"));
        System.out.println(escaper.unescape("gaius:\t" + "caligula&#153;"));

        System.out.println("\n------------------------");
        System.out.println("Getting text from NS API");
        System.out.println("------------------------\n");
        try {
            XML xml = RqForumUtilities.queryApi(528);
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
