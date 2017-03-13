/* Copyright (c) 2017 ifly6
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. */
package com.git.ifly6.rexisquexis;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;

/**
 * @author ifly6
 */
public class RQbb {

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

	public static String italicise(String input) {
		return bbTag(input, "i");
	}

	public static String strike(String input) {
		return bbTag(input, "strike");
	}

	public static String url(String input, String url) {
		return bbTag(input, "url", url);
		// return "[url=" + url + "]" + input + "[/url]";
	}

	public static String url(String input, URL url) {
		return bbTag(input, "url", url.toString());
	}

	public static String post(String input, int post) {
		return bbTag(input, "post", Integer.toString(post));
		// return "[post=" + Integer.toString(post) + "]" + input + "[/post]";
	}

	public static String color(String input, String color) {
		return bbTag(input, "color", color);
		// return "[color=" + color + "]" + input + "[/color]";
	}

	public static String size(String input, int sizeVar) {
		return bbTag(input, "size", Integer.toString(sizeVar));
		// return "[size=" + sizeVar + "]" + input + "[/size]";
	}

	public static String header(String input, String subtitle, int size, boolean anchor) {
		return ((anchor) ? RQbb.bbTag("", "anchor", input.toLowerCase().replace(" ", "_")) : "") + size(input, size) + " "
				+ ((StringUtils.isEmpty(subtitle)) ? "" : RQbb.size(subtitle, 85)) + "\n[hr][/hr]";
	}

	public static String header(String input) {
		return header(input, "", 115, true);
	}

	public static String header(String input, String subtitle) {
		return header(input, subtitle, 115, true);
	}

	public static String box(String input) {
		return bbTag(input, "box");
	}

	public static String tab(int i) {
		return bbTag("", "tab", Integer.toString(i));
	}

	public static String align(String input, String direction) {
		return bbTag(input, "align", direction);
	}
}
