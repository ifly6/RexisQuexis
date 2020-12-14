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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCapitalisation {

    public static void main(String[] args) {
        List<String> testList = List.of(
                "Repeal \"The Protection of Religious Shit\"",
                "Repeal 'Ban on Secret Treaties'",
                "Repeal \"Ban on Secret Treaties\"",
                "GMO Health Act",
                "Christian Democrats",
                "Strong",
                "Tort Reform",
                "GMO Int'l Trade Accord",
                "WA Trade Rights",
                "Repeal \"GMO Int'l Trade Accord\"",
                "GA#10",
                "Repeal \"Promulgation of Law Int'l\"",
                "Repeal 'Promulgation of Law Int'l'",
                "\"GA#14k\"",
                "'GA 15'",
                "Imperium Anglorum",
                "\"Galaxy\"",
                "\"Whom'st\"",
                "Queen Anne's Revenge \"Law Violators\"",
                "\"Law Violators\" Queen Anne's Revenge",
                "Queen Anne's Revenge \"Int'l Law Violators\""
//                "CIA \"Operatives\" Save Children from Wildfire"
        );

        Map<String, Boolean> results = new HashMap<>();
        for (String s : testList)
            results.put(s, testHelper(s));

        long passed = results.values().stream().filter(b -> b).count();
        long failed = results.values().stream().filter(b -> !b).count();
        System.out.printf("Ran %d tests; %d passed, %d failed; %d pc pass rate%n",
                results.size(), passed, failed, Math.round((double) (100 * passed / (passed + failed)))
        );
    }

    /**
     * Administer test for input string. Randomises case. See {@link #randomiseCase(String)}.
     * @param expected from which to derive data to test
     * @return {@code true} if test passed
     */
    private static boolean testHelper(String expected) {
        String randomisedCase = randomiseCase(expected);
        String output = RqForumUtilities.capitalise(randomisedCase);
        boolean passed = output.equals(expected);
        System.out.printf("[%s] input <%s> -> output <%s>%n",
                passed ? "PASSED" : "FAILED",
                randomisedCase,
                output);
        return passed;
    }

    /**
     * Take input and randomise the case of every character; 50-50 for lower or upper.
     * @param s to randomise case
     * @return string with randomised case
     */
    private static String randomiseCase(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray())
            sb.append(Math.random() > 0.5
                    ? Character.toLowerCase(c)
                    : Character.toUpperCase(c)
            );
        return sb.toString();
    }
}
