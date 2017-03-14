/*
 * Copyright (c) 2017 Kevin Wong
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
package com.ifly6.rexisquexis;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;

/**
 * A static class designed to assist in easily generating phpBB bbCode.
 */
public class RQbb {

	public static final int TINY = 50;
	public static final int SMALL = 85;
	public static final int LARGE = 125;
	public static final int HUGE = 150;

	public static final String LEFT = "left";
	public static final String CENTRE = "center";
	public static final String RIGHT = "right";

	private RQbb() {
		// No instantiation!
	}

	// ======== Generalised tag forms ========

	public static String bbTag(String input, String tag) {
		return String.format("[%s]%s[/%s]", tag, input, tag);
	}

	public static String bbTag(String input, String tag, String var) {
		return String.format("[%s=%s]%s[/%s]", tag, var, input, tag);
	}

	// ======== Hardcoded tags ========

	/**
	 * Bolds the input string. For example, <code>hello!</code> turns into <code>[b]hello![/b]</code>.
	 * @param input <code>String</code> to be bolded
	 * @return the bold <code>String</code>
	 */
	public static String bold(String input) {
		return bbTag(input, "b");
	}

	public static String formatBoldTerm(String input) {
		if (input.contains(":")) {
			String[] splitArray = input.split(":");
			return bold(splitArray[0]) + ":" + (splitArray[1]);

		} else {
			return "-1";
		}
	}

	/**
	 * Italicises the input string. Ex: <code>hello!</code> turns into <code>[i]hello![/i]</code>.
	 * @param input <code>String</code> to be italicised
	 * @return the italicised <code>input</code>
	 */
	public static String italicise(String input) {
		return bbTag(input, "i");
	}

	/**
	 * Strikes-through the input string. Ex: <code>hello!</code> turns into <code>[strike]hello![/strike]</code>.
	 * @param input <code>String</code> to be struck
	 * @return the stuck <code>input</code>
	 */
	public static String strike(String input) {
		return bbTag(input, "strike");
	}

	/**
	 * Creates a bbCode URL with custom link text. Ex: <code>[url=$URL]$LINKTEXT[/url]</code>.
	 * @param linkText as <code>String</code>
	 * @param url      as <code>String</code>
	 * @return bbCode URL with specified parameters
	 */
	public static String url(String linkText, String url) {
		return bbTag(linkText, "url", url);
		// return "[url=" + url + "]" + linkText + "[/url]";
	}

	/**
	 * Creates a bbCode URL with custom link text. Ex: <code>[url=$URL.toString()]$LINKTEXT[/url]</code>.
	 * @param linkText as <code>String</code>
	 * @param url      as <code>URL</code>
	 * @return bbCode URL with specified parameters
	 */
	public static String url(String linkText, URL url) {
		return bbTag(linkText, "url", url.toString());
	}

	/**
	 * Creates a post link. This should only work on the NationStates forums, where a post tag is specified. Ex:
	 * <code>[post=$POST_ID]$LINK_TEXT[/post]</code>
	 * @param linkText as <code>String</code>
	 * @param post     id, which is always an integer, and therefore, <code>int</code>
	 * @return bbCode post link with specified parameters
	 */
	public static String post(String linkText, int post) {
		return bbTag(linkText, "post", Integer.toString(post));
		// return "[post=" + Integer.toString(post) + "]" + linkText + "[/post]";
	}

	public static String color(String input, String color) {
		return bbTag(input, "color", color);
		// return "[color=" + color + "]" + input + "[/color]";
	}

	public static String size(String input, int sizeVar) {
		return bbTag(input, "size", Integer.toString(sizeVar));
		// return "[size=" + sizeVar + "]" + input + "[/size]";
	}

	/**
	 * Creates a bbCode header with the header text at a specified size, the subtitle at 75% of that specified size,
	 * and a horizontal line break. Ex: with an anchor and <code>headerText</code> as 'A header',
	 * <code>subtitle</code> as 'Heads things', and <code>size</code> at 150 —
	 * <p>
	 * <code>[anchor=a_header][/anchor][size=150]A header[/size] [size=112]Heads things[/size]
	 * <br />[hr][/hr]</code>
	 * </p>
	 * @param headerText is the main text in the header
	 * @param subtitle   is the header's subtitle descriptor
	 * @param size       is the intended size of the header, expressed as an integer which is <i>i</i>% of normal sized
	 *                   text
	 * @param isAnchor   for whether an anchor should be generated
	 * @return applicable bbCode for this header
	 */
	public static String header(String headerText, String subtitle, int size, boolean isAnchor) {
		// So many ternary expressions!
		String anchor = ((isAnchor)
				? RQbb.bbTag("", "anchor", headerText.toLowerCase().replace(" ", "_"))
				: "");
		return anchor + size(headerText, size) + " " + ((StringUtils.isEmpty(subtitle))
				? ""
				: RQbb.size(subtitle, (int) Math.round(size * 0.75)))
				+ "\n[hr][/hr]";
	}

	/**
	 * Creates a header with no subtitle, size of 150, and an anchor
	 * @param headerText for the header
	 * @return applicable bbCode for this header
	 */
	public static String header(String headerText) {
		return header(headerText, "", 150, true);
	}

	public static String header(String input, String subtitle) {
		return header(input, subtitle, 150, true);
	}

	public static String box(String input) {
		return bbTag(input, "box");
	}

	public static String tab(int i) {
		return bbTag("", "tab", Integer.toString(i));
	}

	/**
	 * Creates an align tag. Ex: <code>[align=right]Text on the right.[/align]</code>
	 * @param content text
	 * @param direction should be either <code>RQbb.LEFT</code>, <code>RQbb.RIGHT</code>, or <code>RQbb.CENTRE</code>
	 * @return
	 */
	public static String align(String content, String direction) {
		return bbTag(content, "align", direction);
	}
}
