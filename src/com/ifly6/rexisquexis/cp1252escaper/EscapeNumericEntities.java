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
import java.nio.charset.IllegalCharsetNameException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Escapes numeric HTML entities.
 */
public class EscapeNumericEntities {

    public static Map<String, EscapeNumericEntities> cache = new HashMap<>();
    public static EscapeNumericEntities WINDOWS_1252 = EscapeNumericEntities.get("Windows-1252");

    private String CHARSET_NAME;

    private EscapeNumericEntities(String charsetName) {
        // prevent instantiation outside of get
        CHARSET_NAME = charsetName;
    }

    /**
     * Gets an escaper for the provided character set name.
     * @param charsetName to get numeric escaper for
     * @return escaper for provided character set
     * @throws IllegalCharsetNameException if charset is unsupported
     */
    public static EscapeNumericEntities get(String charsetName) {
        if (!Charset.isSupported(charsetName))
            throw new IllegalCharsetNameException(String.format("Unsupported charset '%s'", charsetName));

        if (!cache.containsKey(charsetName))
            cache.put(charsetName, new EscapeNumericEntities(charsetName));

        return cache.get(charsetName);
    }

    /**
     * Translates a character, provided as an HTML numeric character entity, in the form <code>&#111;</code> where the
     * digits are anything you'd like. The specified numeric character entity is taken as a code point: the code point
     * is the mapped with the specified set. Code points are inclusive: that is, value <code>&#147;</code> will be
     * parsed if <code>min = 147</code>.
     * @param html numeric character entity
     * @param min  code point to unescape
     * @param max  code point to unescape
     * @return character corresponding to the input
     * @throws InvalidEntityException    if input does not match pattern for a valid HTML numeric character entity
     * @throws InvalidCodepointException if corresponding code point is outside defined range
     */
    private String translateCharacter(String html, int min, int max) {
        // eg &#147;
        if (!html.endsWith(";") || !html.startsWith("&#") || !html.matches("&#\\d+;"))
            throw new InvalidEntityException(String.format("Input '%s' is invalid HTML numeric entity", html));

        try {
            int codePoint = Integer.parseInt(html
                    .replace(";", "")
                    .replace("&#", ""));

            if (codePoint < min || codePoint > max)
                throw new InvalidCodepointException(String.format("input '%s' code point outside valid range [%d, %d]",
                        html, min, max));

            return Charset.forName(CHARSET_NAME)
                    .decode(ByteBuffer.wrap(new byte[] {(byte) codePoint}))  // decode
                    .toString();
        } catch (NumberFormatException e) {
            throw new NumberFormatException(String.format("Input string '%s' does not contain a number", html));
        }

    }

    /**
     * Unescapes <b>numeric</b> HTML numeric character entities to their corresponding code points. This method applies
     * only to numeric HTML character entities and assumes that <b>all</b> numeric characters are so encoded. Use {@link
     * #unescape(String, int, int)} if you want to specify valid code point ranges. It ignores HTML named entities etc.
     * To unescape such entities, you may want to invoke {@link org.apache.commons.text.StringEscapeUtils#escapeHtml4(String)}
     * on what is returned from this method.
     * @param text to unescape to characters
     * @return unescaped text
     */
    public String unescape(String text) {
        return unescape(text, 0, Integer.MAX_VALUE);
    }

    /**
     * Unescapes <b>numeric</b> HTML character entities to corresponding code points. This method applies only to
     * numeric HTML numeric character entities. Parameters <code>min</code> and <code>max</code> specify a range of code
     * points to parse; ones outside that range will be ignored. Code points are inclusive: that is, value 147 (ie
     * <code>&#147;</code> in Windows-1252) will be parsed if <code>min = 147</code>.
     * <p>It ignores HTML named entities etc. To unescape such entities, you may want to invoke {@link
     * org.apache.commons.text.StringEscapeUtils#escapeHtml4(String)} on what is returned from this method.</p>
     * @param text to unescape
     * @param min  code point to unescape
     * @param max  code point to unescape
     * @return unescaped text
     */
    public String unescape(String text, int min, int max) {
        Matcher m = Pattern.compile("&#\\d+;").matcher(text);
        StringBuffer sb = new StringBuffer();  // must use string buffer; matcher api requires
        while (m.find())
            try {
                // for each thing you find, take the match, translate the character, and append the replacement
                m.appendReplacement(sb, translateCharacter(m.group(), min, max));
            } catch (InvalidCodepointException | InvalidEntityException e) {
                // if code point is out of bounds or invalid, pass
                m.appendReplacement(sb, m.group());
            }

        m.appendTail(sb);  // append everything left
        return sb.toString();  // return string
    }
}
