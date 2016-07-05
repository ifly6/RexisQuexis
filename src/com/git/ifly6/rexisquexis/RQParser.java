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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Kevin
 *
 */
public class RQParser {

	public RQParser() {

		JFrame frame = new JFrame("WA Resolutions Reformatter");
		frame.setSize(700, 800);
		JPanel panel = new JPanel();
		frame.setContentPane(panel);

		panel.setLayout(new BorderLayout());

		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));

		JButton parseButton = new JButton("Parse");
		parseButton.addActionListener(new ActionListener() {

			@Override public void actionPerformed(ActionEvent e) {
				String topicUrl = JOptionPane.showInputDialog("Enter the URL for the debate topic.");

				String input = textArea.getText();

				textArea.setText(this.parse(input, topicUrl));
			}

			// Parses the string
			private String parse(String input, String topicUrl) {

				List<String> rawLines = Arrays.asList(input.split("\n"));
				List<String> lines = new ArrayList<>();
				for (int x = 0; x < rawLines.size(); x++) {
					if (!(rawLines.get(x).trim().length() == 0)) {
						lines.add(rawLines.get(x));
					}
				}

				List<String> outputLines = new ArrayList<String>();

				// Get resolution number and is it a repeal?
				String resolutionNumber = lines.get(0).replace("GENERAL ASSEMBLY RESOLUTION #", "").trim();
				boolean isRepeal = lines.get(1).contains("Repeal");

				// Titles and category description
				outputLines.add(RQbb.bold(lines.get(1)));
				outputLines.add(RQbb.italicise(lines.get(2)));

				outputLines.add("");

				// Category
				outputLines.add(formatTerm(lines.get(3)));

				// Format Strength line for Repeals
				if (lines.get(1).contains("Repeal")) {

					String formatted = lines.get(4);
					formatted.replace("GA", "");
					outputLines.add(formatTerm(formatted));

				} else {
					outputLines.add(formatTerm(lines.get(4)));
				}

				outputLines.add(formatTerm(lines.get(5)));

				outputLines.add("");

				int startResText;
				int endResText;

				if (lines.get(1).contains("Repeal")) {

					String repealUrl = JOptionPane
							.showInputDialog("Enter the url of the repeal resolution's post in RexisQuexis.");

					// Generate relevant description line
					String descriptionLine = lines.get(findLineStartsWith("Description:", lines));
					String nameLine = descriptionLine.replaceFirst("Description:", "");
					nameLine = nameLine.trim();
					nameLine = nameLine.substring(nameLine.indexOf(":") + 2, nameLine.indexOf('(') - 1);

					String referenceData = descriptionLine.substring(descriptionLine.indexOf('('), descriptionLine.length());

					outputLines.add(RQbb.bold("Description:") + " "
							+ RQbb.post(" WA General Assembly Resolution #" + resolutionNumber + ": " + nameLine,
									parsePostFromUrl(repealUrl))
							+ " " + referenceData);
					outputLines.add("");

					// Get argument lines -- use rawLines to preserve spacing
					startResText = findLineStartsWith("Argument:", rawLines);
					endResText = findLineStartsWith("Votes For:", rawLines) - 1;

					outputLines.add(rawLines.get(startResText).replace("Argument:", "[b]Argument:[/b]"));

				} else {

					// Use rawLines to preserve spacing
					startResText = findLineStartsWith("Description:", rawLines);
					endResText = findLineStartsWith("Votes For:", rawLines) - 1;

					outputLines.add(rawLines.get(startResText).replace("Description:", "[b]Description:[/b]"));

				}

				// Write in the argument/text lines
				for (int x = startResText + 1; x < endResText; x++) {
					outputLines.add(rawLines.get(x));
				}

				outputLines.add("");

				// Add vote outcome lines
				outputLines.add(formatTerm(lines.get(findLineStartsWith("Votes For:", lines))));
				outputLines.add(formatTerm(lines.get(findLineStartsWith("Votes Against:", lines))));

				outputLines.add("");

				// Add implementation line
				String implementedLine = lines.get(findLineStartsWith("Implemented:", lines));
				String[] implementedLineArray = implementedLine.split(":");
				outputLines.add(RQbb.color("Implemented " + implementedLineArray[1].trim(), "red"));

				outputLines.add("");

				// Add resolution number
				String nsLink = "[url=http://www.nationstates.net/page=WA_past_resolutions/council=1/start="
						+ (Integer.parseInt(resolutionNumber) - 1) + "]" + resolutionNumber + " GA on NS[/url]";

				String string = RQbb.size(RQbb.bold("[" + nsLink + "] [" + RQbb.url("Official Debate Topic", topicUrl)), 85)
						+ "]";
				outputLines.add(string);

				return formatList(outputLines);
			}

			// Formats things like Category: Human Rights
			private String formatTerm(String input) {
				String[] list = input.split(":");
				return RQbb.bold(list[0].trim() + ":") + " " + list[1].trim();
			}

		});
		buttonPanel.add(parseButton);

		JButton repealButton = new JButton("Repeal Format (use bbCode form)");
		repealButton.addActionListener(new ActionListener() {

			@Override public void actionPerformed(ActionEvent e) {

				List<String> textLines = Arrays.asList(textArea.getText().split("\n"));
				textArea.setText(repealFormat(textLines));

			}

			private String repealFormat(List<String> textLines) {

				int whichRepeal = Integer.parseInt(
						JOptionPane.showInputDialog("Enter the number of the resolution that repealed this resolution."));
				String urlRepeal = JOptionPane.showInputDialog("Enter the url of the repealing resolution.");

				String strikePhrase = "[Struck out by " + RQbb.post(whichRepeal + " GA", parsePostFromUrl(urlRepeal)) + "]";
				String newStartLine = RQbb.color(RQbb.strike(textLines.get(0)), "gray") + " " + RQbb.bold(strikePhrase);

				textLines.set(0, newStartLine);

				textLines.set(1, "[color=gray][strike]" + textLines.get(1));

				int lastStrikeLine = findLineStartsWith("[b]Votes Against", textLines);
				textLines.set(lastStrikeLine, textLines.get(lastStrikeLine) + "[/strike][/color]");

				return formatList(textLines);

			}
		});
		buttonPanel.add(repealButton);

		panel.add(buttonPanel, BorderLayout.SOUTH);
		frame.setVisible(true);
	}

	// === Helpful Parsing and Search Methods ===

	private int parsePostFromUrl(String postUrl) {
		return Integer.parseInt(postUrl.substring(postUrl.indexOf("#p") + 2, postUrl.length()));
	}

	private int findLineStartsWith(String term, List<String> list) {
		for (int x = 0; x < list.size(); x++) {
			if (list.get(x).startsWith(term)) { return x; }
		}
		return -1;
	}

	private String formatList(List<String> list) {
		StringBuilder builder = new StringBuilder();
		for (String element : list) {
			builder.append(element + "\n");
		}
		return builder.toString();
	}

	public static void main(String[] args) {
		new RQParser();
	}
}