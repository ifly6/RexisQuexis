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

import static com.ifly6.rexisquexis.io.RqForumUtilities.cleanForumURL;

public class TestURLParser {

    public static void main(String[] args) {
        testURL("https://forum.nationstates.net/viewtopic.php?p=35629514#p35629514");
        testURL("https://forum.nationstates.net/viewtopic.php?f=9&t=495081");
        testURL("https://forum.nationstates.net/viewtopic.php?f=9&t=495070&sid=1387589fewlhiu");
        testURL("https://forum.nationstates.net/viewtopic.php?f=9&t=494438&p=37945397&hilit=passed#p37945397");
        testURL("https://forum.nationstates.net/viewtopic.php?f=9&t=484456&p=37051357&hilit=passed#p37051357");
        testURL("https://forum.nationstates.net/viewtopic.php?p=36298615#p36298615");
        testURL("https://forum.nationstates.net/viewtopic.php?f=9&p=38086143#p38086143");
    }

    private static void testURL(String s) {
        String output = cleanForumURL(s);
        System.out.println(String.format("input <%s>\t returned <%s>", s, output));
    }
}
