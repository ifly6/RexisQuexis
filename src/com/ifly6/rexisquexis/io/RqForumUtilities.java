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

package com.ifly6.rexisquexis.io;

import com.git.ifly6.nsapi.NSConnection;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

public class RqForumUtilities {

    private RqForumUtilities() {
    }

    /**
     * Capitalises strings into proper title case
     * @param s to capitalise
     * @return capitalised string
     */
    public static String capitalise(String s) {

        final List<String> makeLowerWords =
                List.of("for", "and", "nor", "but", "yet", "the", "or", "to", "on", "of");
        final List<String> makeUpperWords =
                List.of("ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x", "wa", "gmo", "ga", "leo",
                        "lgbtqia", "lgbtiqa");
        final List<String> suffixes =
                List.of("'l", "'s", "'t", "'st"); // nat'l, harry's, don't, whom'st
        final List<String> prefixes =
                List.of("GA#");

        final String ACTIONABLE_DELIMITERS = " -/\"'"; // if following, capitalise

        s = cleanQuotes(s); // clean smart quotes
        StringBuilder sb = new StringBuilder();
        boolean capNext = true;
        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf(c) >= 0); // explicit cast not needed
        }

        String[] split = sb.toString().trim().split("\\s"); // split on spaces

        // deal with whole words that should be made upper or lower
        List<String> elements = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            String e = split[i].trim();
            if (ignoreCaseContains(makeLowerWords, e) && i != 0) e = e.toLowerCase();
            else if (ignoreCaseContains(makeUpperWords, e)) e = e.toUpperCase();

            // while here also deal with suffixes
            for (String suffix : suffixes)
                if (endsWithIgnoreCase(e, suffix))
                    e = replaceLast(e, "(?i)" + suffix, suffix);

            // while here also also deal with prefixes
            for (String prefix : prefixes)
                if (startsWithIgnoreCase(e, prefix))
                    e = e.replaceFirst("(?i)" + prefix, prefix);

            elements.add(e);
        }

        /* This regex is complex. It first looks for a starting quote; those quotes are either single or double quotes.
         * The it looks at the end for the same thing matched in the first matching group, in the positive lookbehind.
         * It also then checks whether the lookahead ending portion itself is next to whitespace or to the end of line.
         * This tries to minimise the number of false matches to inter-word apostrophes. No guarantees on perfection.
         *
         * It is not possible to add something like «(\\s|^)» to the start, as the lookbehind needs to be fixed-width.
         * If that were possible, then it might be perfect, absent cases where apostrophes start and end words, which
         * is something that I don't think happens regularly in English.
         */
        Matcher m = Pattern.compile("(?<=([\"']))(.*)(?=\\1(\\s|$))")
                .matcher(String.join(" ", elements));
        StringBuilder buffer = new StringBuilder();  // must use string buffer; matcher api requires
        while (m.find()) {
            // recursively capitalise anything in quotes
            String quoteContents = m.group();
            m.appendReplacement(buffer, capitalise(quoteContents));
        }

        m.appendTail(buffer);  // append everything left
        return buffer.toString();
    }

    /**
     * Parses a NationStates forum URL from provided string. The forum URL is extracted and then reconstructed.
     * @param forumURL to parse from unclean URL
     * @return clean URL
     */
    public static String cleanForumURL(String forumURL) {
        // https://forum.nationstates.net/viewtopic.php?p=35629514#p35629514
        // https://forum.nationstates.net/viewtopic.php?f=9&t=495081
        // https://forum.nationstates.net/viewtopic.php?f=9&t=494438&p=37945397&hilit=passed#p37945397&sid=dlkafjkld
        // https://forum.nationstates.net/viewtopic.php?f=9&t=484456&p=37051357&hilit=passed#p37051357
        // https://forum.nationstates.net/viewtopic.php?p=36298615#p36298615
        // https://forum.nationstates.net/viewtopic.php?f=9&p=38086143#p38086143
        if (!forumURL.startsWith("https://forum.nationstates.net"))
            throw new UnsupportedOperationException("Cannot parse URL that is not a NS forum URL");

        Matcher threadMatcher = Pattern.compile("(?<=t=)\\d+").matcher(forumURL);
        while (threadMatcher.find()) try {
            var postNum = Integer.parseInt(threadMatcher.group());
            return String.format("https://forum.nationstates.net/viewtopic.php?t=%d", postNum);
        } catch (NumberFormatException e) {
            // continue;
        }

        Matcher postMatcher = Pattern.compile("(?<=#p)\\d+").matcher(forumURL);
        while (postMatcher.find()) try {
            var postNum = Integer.parseInt(postMatcher.group());
            return String.format("https://forum.nationstates.net/viewtopic.php?p=%d#p%d", postNum, postNum);
        } catch (NumberFormatException e) {
            // continue;
        }

        throw new IllegalArgumentException(String.format("Input %s does not contain post or thread code", forumURL));
    }

    /**
     * Cleans 'smart' quotes into standard quotes
     * @param text to clean
     * @return sanitised text
     */
    public static String cleanQuotes(String text) {
        return text.replaceAll("[\\u2018\\u2019]", "'")
                .replaceAll("[\\u201C\\u201D]", "\"");
    }

    /**
     * String contains, but ignore case. Also ignore all quotes (including smart quotes).
     * @param haystack to look in
     * @param needle   to find, with exceptions
     * @return true if contains after applying checks
     */
    public static boolean ignoreCaseContains(final List<String> haystack, final String needle) {
        List<String> hay = haystack.stream().map(String::toLowerCase).collect(Collectors.toList());
        String n = needle.toLowerCase();
        return hay.contains(n);
    }

    /**
     * Replaces last occurrence of provided regex with replace. See <a
     * href="https://stackoverflow.com/a/2282998/2741091">post</a> for source.
     * @param text        to search
     * @param regex       to find
     * @param replacement to put
     * @return text, with area found replaced
     */
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    /**
     * Queries the NationStates API for resolution number.
     * @param rNum to query
     * @return XML document
     * @throws IOException if cannot connect
     */
    public static XML queryApi(int rNum) throws IOException {
        String url = String.format("https://www.nationstates.net/cgi-bin/api.cgi?wa=%d&id=%d&q=resolution", 1, rNum);
        NSConnection connection = new NSConnection(url);
        System.err.printf("Querying API for GA %d%n", rNum);
        System.err.printf("URL is %s%n", url);
        return new XMLDocument(connection.getResponse());
    }

}
