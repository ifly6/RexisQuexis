/* Copyright (c) 2016 ifly6
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

/** @author ifly6 */
public class RexisQuexisParser {
	
	public RexisQuexisParser() {
		
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
				List<String> lines = rawLines.stream().filter(x -> !(x.trim().length() == 0)).map(String::trim)
						.collect(Collectors.toList());

				List<String> outputLines = new ArrayList<>();

				final int PREFIX = 0;
				final int TITLE = 1;
				final int DESCRIPTION = 2;
				final int CATEGORY = 3;
				final int STRENGTH = 4;
				final int PROPOSER = 5;
				final int REPEAL_DESCRIPTION = 6;

				// Get resolution number and is it a repeal?
				String resolutionNumber = lines.get(PREFIX).replace("General Assembly Resolution #", "").trim();
				boolean isRepeal = lines.get(TITLE).contains("Repeal") || lines.get(TITLE).contains("repeal");

				// Titles and category description
				outputLines.add(RQbb.bold(lines.get(TITLE)));
				outputLines.add(RQbb.italicise(lines.get(DESCRIPTION)));

				outputLines.add("");

				// Category
				if (isRepeal) {
					outputLines.add(formatTerm(lines.get(CATEGORY).replace("GA", "")));
				} else {
					outputLines.add(formatTerm(lines.get(CATEGORY)));
				}

				// Format Strength line for Repeals
				if (isRepeal) {
					String formatted = lines.get(STRENGTH).replaceAll("GA", "");
					outputLines.add(formatTerm(formatted));
				} else {
					outputLines.add(formatTerm(lines.get(STRENGTH)));
				}

				outputLines.add(formatTerm(lines.get(PROPOSER)));

				outputLines.add("");

				int startResText;
				int endResText;

				if (isRepeal) {
					
					// Get URL for the repealed resolution's post in RexisQuexis via a web scrape
					String repealUrl = "";
					try {
						
						Elements elements = Jsoup
								.parse(new URL("http://forum.nationstates.net/viewtopic.php?f=9&t=30"), 2000)
								.select("div#p310 div.content a");
						System.out.println("elements.toString()\t" + elements.toString());
						String repealDesc = lines.get(REPEAL_DESCRIPTION).replace("Description:", "");
						String repealedName = repealDesc.substring(repealDesc.indexOf(":") + 2, repealDesc.indexOf('(') - 1)
								.trim();

						repealUrl = elements.stream().filter(e -> e.text().trim().equalsIgnoreCase(repealedName)).findFirst()
								.get().attr("abs:href");

					} catch (Exception e) {
						e.printStackTrace();
						repealUrl = JOptionPane.showInputDialog(frame,
								"Enter the URL of the repeal resolution's post in RexisQuexis", "Parameter Input",
								JOptionPane.PLAIN_MESSAGE);
					}

					// Generate relevant description line
					String descriptionLine = lines.get(findLineStartsWith("Description:", lines));
					String nameLine = descriptionLine.replaceFirst("Description:", "");
					nameLine = nameLine.substring(nameLine.indexOf(":") + 1, nameLine.indexOf('(') - 1).trim();

					String referenceData = descriptionLine.substring(descriptionLine.indexOf('('), descriptionLine.length());
					String repealedResNumber = lines.get(STRENGTH).substring(lines.get(STRENGTH).indexOf("#") + 1,
							lines.get(STRENGTH).length());

					outputLines.add(RQbb.bold("Description:") + " "
							+ RQbb.post("WA General Assembly Resolution #" + repealedResNumber + ": " + nameLine,
									parsePostFromUrl(repealUrl))
							+ " " + referenceData);
					outputLines.add("");

					// Get argument lines -- use rawLines to preserve spacing
					startResText = findLineStartsWith("Argument:", rawLines);
					endResText = findLineStartsWith("Votes For:", rawLines) - 1;

					outputLines.add(rawLines.get(startResText).replace("Argument:", RQbb.bold("Argument:")));

				} else {
					
					// Use rawLines to preserve spacing
					startResText = findLineStartsWith("Proposed by:", rawLines) + 2;
					endResText = findLineStartsWith("Passed:", rawLines) - 1;

					outputLines.add(rawLines.get(startResText).replace("Description:", RQbb.bold("Description:")));

				}

				// Write in the argument/text lines
				// Note that the above blocks deal with the first lines, so that is not necessary
				for (int x = startResText + 1; x < endResText; x++) {
					outputLines.add(rawLines.get(x));
				}

				outputLines.add("");

				// Add vote outcome lines
				List<String> subLines = rawLines.subList(findLineStartsWith("Passed:", rawLines), rawLines.size());
				
				int ayes = Integer.parseInt(subLines.get(6).replaceAll(",", ""));
				int nays = Integer.parseInt(subLines.get(12).replaceAll(",", ""));
				int totalVotes = ayes + nays;
				double proAye = (double) ayes / totalVotes;
				double proNay = (double) nays / totalVotes;
				String pcAye = String.valueOf(Math.round(proAye * 100));
				String pcNay = String.valueOf(Math.round(proNay * 100));
				
				outputLines.add(formatTerm("Votes For: " + subLines.get(6) + " (" + pcAye + "%)"));
				outputLines.add(formatTerm("Votes For: " + subLines.get(12) + " (" + pcNay + "%)"));

				outputLines.add("");

				// Add implementation line
				outputLines.add(RQbb.color("Implemented " + subLines.get(2).trim(), "red"));

				outputLines.add("");

				// Add resolution number
				String nsLink = "[url=http://www.nationstates.net/page=WA_past_resolutions/council=1/start="
						+ (Integer.parseInt(resolutionNumber) - 1) + "]" + resolutionNumber + " GA on NS[/url]";

				String string = RQbb
						.size(RQbb.bold("[" + nsLink + "] [" + RQbb.url("Official Debate Topic", topicUrl)) + "]", 85);
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
				String urlRepeal = JOptionPane.showInputDialog("Enter the RexisQuexis url of the repealing resolution.");

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
		new RexisQuexisParser();
	}
}