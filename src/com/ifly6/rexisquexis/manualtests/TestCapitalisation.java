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

import com.ifly6.rexisquexis.io.RqForumUtilities;

public class TestCapitalisation {
    public static void main(String[] args) {
        testHelper("Repeal 'Ban on Secret Treaties'");
        testHelper("Repeal \"Ban on Secret Treaties\"");
        testHelper("GMO Health Act");
        testHelper("Christian DemoCrats");
        testHelper("strong");
        testHelper("tort reform");
        testHelper("Repeal \"On Universal Jurisdiction\"");
        testHelper("Repeal \"GMO Int'l Trade Accord\"");
        testHelper("GA#10");
        testHelper("Protection OF ReligiouS shit");
        testHelper("Repeal \"Supporting Protection OF ReligiouS shit\"");
        testHelper("Repeal \"protection OF ReligiouS shit\"");
        testHelper("Repeal \"the protection OF ReligiouS shit\"");
    }

    private static void testHelper(String s) {
        System.out.printf("input '%s' -> output '%s'%n", s, RqForumUtilities.capitalise(s));
    }
}
