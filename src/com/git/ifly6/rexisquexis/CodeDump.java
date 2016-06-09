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
public class CodeDump {

	// private WAResolution newParse(String input, String topicUrl) {
	//
	// String[] rawLines = input.split("\n");
	// WAResolution resolution = new WAResolution();
	//
	// List<String> textLines = new ArrayList<>();
	// for (String element : textLines) {
	// if (element.trim().length() == 0) {
	// textLines.add(element);
	// }
	// }
	//
	// // ==== Three possibilities: Normal, Repeal, Proposal ====
	// // Also gets title, votes for, votes against, the resolution number, and the implementation date
	// if (rawLines[0].contains("RESOLUTION")) {
	//
	// if (rawLines[0].contains("GENERAL ASSEMBLY")) {
	//
	// if (rawLines[0].contains("Repeal")) {
	// resolution.resolutionType = ResolutionType.REPEAL;
	// } else {
	// resolution.resolutionType = ResolutionType.NORMAL;
	// }
	// }
	//
	// // Get vote tally
	// resolution.votesFor = Integer.parseInt(returnLineStartsWith("Votes For:", textLines).split(":")[1].trim());
	// resolution.votesAgainst = Integer
	// .parseInt(returnLineStartsWith("Votes Against:", textLines).split(":")[1].trim());
	//
	// // Get the resolution number
	// String[] titleArray = returnLineStartsWith("Votes For:", textLines).split(" ");
	// resolution.resolutionNum = Integer.parseInt(titleArray[titleArray.length].trim());
	//
	// // Get the date
	// try {
	// String implementationDate = returnLineStartsWith("Implemented:", textLines).split(":")[1].trim();
	// SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM DD yyyy");
	// resolution.implementation = formatter.parse(implementationDate);
	// } catch (ParseException e) {
	// System.err.println("Error. ParseException. Check the stack trace.");
	// e.printStackTrace();
	// }
	//
	// // Get the title
	// resolution.title = rawLines[2];
	//
	// } else {
	//
	// resolution.resolutionType = ResolutionType.PROPOSAL;
	//
	// resolution.votesFor = 0;
	// resolution.votesAgainst = 0;
	// resolution.resolutionNum = 0;
	//
	// resolution.implementation = new Date();
	//
	// resolution.title = rawLines[3];
	//
	// }
	//
	// // ==== Determine category, strength, proposer ====
	// resolution.category = returnLineStartsWith("Category:", textLines);
	// resolution.proposer = returnLineStartsWith("Proposed by:", textLines);
	//
	// if (resolution.resolutionType == ResolutionType.PROPOSAL) {
	//
	// // Not index 9 because of the ID index in the proposal
	// resolution.strengthLine = rawLines[10];
	//
	// } else {
	//
	// // Normally on index 9
	// resolution.strengthLine = rawLines[9];
	// }
	//
	// // ==== Text ====
	// // Use rawLines to preserve spacing
	// int startResText = findLineStartsWith("Description:", Arrays.asList(rawLines));
	// int endResText = (resolution.resolutionType == ResolutionType.PROPOSAL)
	// ? findLineStartsWith("Approvals:", Arrays.asList(rawLines)) - 1
	// : findLineStartsWith("Votes For:", Arrays.asList(rawLines)) - 1;
	//
	// List<String> lines = new ArrayList<>();
	// lines.add(rawLines[startResText].replace("Description:", "[b]Description:[/b]"));
	//
	// for (int x = startResText + 1; x < endResText; x++) {
	// lines.add(rawLines[x]);
	// }
	//
	// resolution.text = formatList(lines);
	//
	// // ==== Call the text generator ====
	// return resolution;
	// }

}
