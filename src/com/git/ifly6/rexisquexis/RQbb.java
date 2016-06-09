/* Copyright (c) 2016 Kevin Wong
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

/**
 * @author Kevin
 *
 */
public class RQbb {

	private RQbb() {
		// No instantiation!
	}

	public static String bold(String input) {
		return "[b]" + input + "[/b]";
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
		return "[i]" + input + "[/i]";
	}

	public static String strike(String input) {
		return "[strike]" + input + "[/strike]";
	}

	public static String url(String input, String url) {
		return "[url=" + url + "]" + input + "[/url]";
	}

	public static String post(String input, int post) {
		return "[post=" + Integer.toString(post) + "]" + input + "[/post]";
	}

	public static String color(String input, String color) {
		return "[color=" + color + "]" + input + "[/color]";
	}

	public static String size(String input, int sizeVar) {
		return "[size=" + sizeVar + "]" + input + "[/size]";
	}
}
