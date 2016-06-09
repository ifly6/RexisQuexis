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

import java.util.ArrayList;
import java.util.List;

public class RQWriter {

	private WAResolution resolution;

	private boolean isRepeal;
	private boolean isRepealed;

	public RQWriter() {
		this.isRepeal = false;
		this.isRepealed = false;
	}

	public RQWriter(boolean isRepeal, boolean isRepealed) {
		this.isRepeal = isRepeal;
		this.isRepealed = isRepealed;
	}

	public String generateText() {

		List<String> outputText = new ArrayList<String>();

		outputText.add(RQbb.bold(resolution.title));
		outputText.add(RQbb.italicise(resolution.byLine));

		outputText.add("");

		outputText.add(RQbb.bold("Category:") + " " + resolution.category);
		outputText.add(RQbb.formatBoldTerm(resolution.strengthLine));
		outputText.add(RQbb.bold("Proposed by:") + " " + resolution.proposer);

		outputText.add("");

		// Split the text lines. Analyse.
		String[] textLines = resolution.text.split("\n");
		if (isRepeal) {
			if (textLines[0].startsWith("Description:")) {

			}
		}
		outputText.add("");

		return null;
	}
}
