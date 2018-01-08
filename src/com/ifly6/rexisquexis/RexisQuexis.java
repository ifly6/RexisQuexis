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

package com.ifly6.rexisquexis;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.undo.UndoManager;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.git.ifly6.nsapi.NSConnection;
import com.jcabi.xml.XMLDocument;

public class RexisQuexis {
	
	private static final Logger LOGGER = Logger.getLogger(RexisQuexis.class.getName());
	
	private RexisQuexis() {
		
		JFrame frame = new JFrame("WA Resolutions Formatter");
		frame.setSize(700, 800);
		JPanel panel = new JPanel();
		frame.setContentPane(panel);
		
		panel.setLayout(new BorderLayout());
		
		JTextArea textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
		
		UndoManager undoManager = new UndoManager();
		textArea.getDocument().addUndoableEditListener(undoManager);
		
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		
		JButton parseButton = new JButton("Parse");
		parseButton.addActionListener(new ActionListener() {
			
			@Override public void actionPerformed(ActionEvent e) {
				String url = JOptionPane.showInputDialog(frame, "Enter the URL for the debate topic.",
						"Parameter Input", JOptionPane.PLAIN_MESSAGE)
						.replace("&hilit=[^#]+", "")
						.replace("&sid=[^#]", "");
				
				LOGGER.info("Attempting to parse resolution");
				GAResolution gaResolution = GAResolution.parse(textArea.getText());
				LOGGER.info(String.format("Parsed for GA resolution %d", gaResolution.resolutionNum));
				
				gaResolution.topicUrl = url;
				textArea.setText(gaResolution.format());
			}
			
			// ** Parses the string **
			// private String parse(String input, String topicUrl) {
			//
			// final List<String> rawLines = new ArrayList<>(Arrays.asList(input.split("\n")));
			// final List<String> lines = rawLines.stream()
			// .filter(x -> !(x.trim().length() == 0))
			// .map(String::trim)
			// .collect(Collectors.toList());
			//
			// List<String> outputLines = new ArrayList<>();
			//
			// final int PREFIX = 0;
			// final int TITLE = 1;
			// final int DESCRIPTION = 2;
			// final int CATEGORY = 3;
			// final int STRENGTH = 4;
			// final int PROPOSER = 5;
			// final int REPEAL_DESCRIPTION = 6;
			//
			// // Get resolution number and is it a repeal?
			// int resolutionNumber =
			// Integer.parseInt(lines.get(PREFIX).replace("General Assembly Resolution #", "").trim());
			// boolean isRepeal = lines.get(TITLE).contains("Repeal") || lines.get(TITLE).contains("repeal");
			//
			// // Titles and category description
			// outputLines.add(RQbb.bold(lines.get(TITLE).replaceAll("[\\u0093\\u0094]", "\"").replace("Repeal:",
			// "Repeal")));
			// outputLines.add(RQbb.italicise(lines.get(DESCRIPTION)));
			//
			// outputLines.add("");
			//
			// // Category
			// if (isRepeal) outputLines.add(formatTerm(lines.get(CATEGORY).replace("GA", "")));
			// else outputLines.add(formatTerm(lines.get(CATEGORY)));
			//
			// // Format Strength line for Repeals
			// if (isRepeal) {
			// String formatted = lines.get(STRENGTH).replaceAll("GA", "");
			// outputLines.add(formatTerm(formatted));
			// } else outputLines.add(formatTerm(lines.get(STRENGTH)));
			//
			// outputLines.add(formatTerm(lines.get(PROPOSER)));
			//
			// outputLines.add("");
			//
			// if (isRepeal) {
			//
			// // Generate relevant description line
			// String descriptionLine = lines.subList(1, lines.size()).get(indexStartsWith("General " +
			// "Assembly Resolution", lines.subList(1, lines.size())));
			// String targetName = descriptionLine.replaceFirst("General Assembly Resolution", "");
			// targetName = targetName.substring(targetName.indexOf("“") + 1, targetName.indexOf('”')).trim();
			//
			// String referenceData = descriptionLine.substring(descriptionLine.indexOf('('), descriptionLine.length());
			// String targetNum = lines.get(STRENGTH).substring(lines.get(STRENGTH).indexOf("#") + 1,
			// lines.get(STRENGTH).length());
			//
			// // Get URL for the repealed resolution's post in RexisQuexis via a web scrape
			// String repealUrl;
			// try {
			//
			// Elements elements = Jsoup
			// .parse(new URL("http://forum.nationstates.net/viewtopic.php?f=9&t=30"), 2000)
			// .select("div#p310 div.content a");
			// LOGGER.info("elements.toString()\t" + elements.toString());
			// String finalTargetName = targetName;
			// repealUrl = elements.stream().filter(e -> e.text().trim().equalsIgnoreCase(finalTargetName))
			// .findFirst().get().attr("abs:href");
			//
			// } catch (Exception e) {
			// e.printStackTrace();
			// System.err.println("targetName:\t" + targetName);
			// repealUrl = JOptionPane.showInputDialog(frame,
			// "Enter the URL of the repeal resolution's post in RexisQuexis", "Parameter Input",
			// JOptionPane.PLAIN_MESSAGE);
			// }
			//
			// outputLines.add(String.format("%s %s %s",
			// RQbb.bold("Description:"),
			// RQbb.post(String.format("General Assembly Resolution #%s: %s", targetNum, targetName),
			// postInt(repealUrl)),
			// referenceData));
			// outputLines.add("");
			//
			// }
			//
			// /* Write in the text of the resolution. First, we need to parse out what the text is. Due to formatting
			// * considerations, use an API connection to query for the text with full formatting. If that fails, use
			// * the old method which tries to identify the start and end of the text from the copy-paste of the
			// * resolution given in the TextArea. */
			// try {
			// NSConnection connection = new NSConnection(String.format("https://www.nationstates.net/cgi-bin/api" +
			// ".cgi?wa=%d&id=%d&q=resolution", 1, resolutionNumber));
			// XML xml = new XMLDocument(connection.getResponse());
			// String[] apiTextLines = xml.xpath("/WA/RESOLUTION/DESC/text()").get(0).replaceFirst("<!\\[CDATA\\[", "")
			// .replace("]]>", "").split("\n");
			// String text = Stream.of(apiTextLines)
			// .map(s -> {
			// String[] obsceneTags = { "\\[b\\]", "\\[i\\]", "\\[u\\]", "\\[/b\\]", "\\[/i\\]",
			// "\\[/u\\]" };
			// for (String element : obsceneTags)
			// s = s.replaceAll(element, "");
			// return s;
			// })
			// .map(s -> Jsoup.parse(s).text())
			// .collect(Collectors.joining("\n"));
			//
			// outputLines.add(String.format("%s %s", RQbb.bold(isRepeal ? "Argument:" : "Description:"), text));
			//
			// } catch (IOException e) {
			//
			// e.printStackTrace();
			//
			// int startText = indexStartsWith("Proposed by:", rawLines) + 2;
			// int endText = indexStartsWith("Passed:", rawLines) - 1;
			// if (isRepeal) {
			// startText = indexStartsWith("Proposed by:", rawLines) + 4;
			// endText = indexStartsWith("Passed:", rawLines) - 1;
			// }
			//
			// // Write in the argument (or description) lines
			// outputLines.add(String.format("%s %s", RQbb.bold(isRepeal ? "Argument:" : "Description:"),
			// rawLines.get(startText)));
			// for (int x = startText + 1; x < endText; x++)
			// outputLines.add(rawLines.get(x));
			//
			// }
			//
			// outputLines.add("");
			//
			// // Add vote outcome lines
			// List<String> subLines = rawLines.subList(indexStartsWith("Passed:", rawLines), rawLines.size());
			//
			// int ayes = Integer.parseInt(subLines.get(6).replaceAll(",", ""));
			// int nays = Integer.parseInt(subLines.get(12).replaceAll(",", ""));
			// int totalVotes = ayes + nays;
			// String pcAye = String.valueOf(Math.round((double) ayes / totalVotes * 100));
			// String pcNay = String.valueOf(Math.round((double) nays / totalVotes * 100));
			//
			// outputLines.add(formatTerm("Votes For: " + subLines.get(6) + " (" + pcAye + "%)"));
			// outputLines.add(formatTerm("Votes Against: " + subLines.get(12) + " (" + pcNay + "%)"));
			//
			// outputLines.add("");
			//
			// // Add implementation line
			// outputLines.add(RQbb.color("Implemented " + subLines.get(2).trim(), "red"));
			// outputLines.add("");
			//
			// // Add resolution number
			// String officialUrl = RQbb.url(resolutionNumber + " GA on NS", "http://www.nationstates" +
			// ".net/page=WA_past_resolutions/council=1/start=" + (resolutionNumber - 1));
			// String finalLine = RQbb.bold(String.format("[%s] [%s]", officialUrl,
			// RQbb.url("Official Debate Topic", topicUrl)));
			// String sizedLine = RQbb.size(finalLine, RQbb.SMALL);
			// outputLines.add(sizedLine);
			//
			// return joinList(outputLines);
			// }
			//
			// // Formats things like Category: Human Rights
			// private String formatTerm(String input) {
			// String[] list = input.split(":");
			// return RQbb.bold(list[0].trim() + ":") + " " + list[1].trim();
			// }
			
		});
		buttonPanel.add(parseButton);
		
		JButton repealButton = new JButton("Repeal Format (use bbCode form)");
		repealButton.addActionListener(new ActionListener() {
			
			@Override public void actionPerformed(ActionEvent e) {
				
				List<String> textLines = Arrays.asList(textArea.getText().split("\n"));
				textArea.setText(repealFormat(textLines));
				
			}
			
			private String repealFormat(List<String> textLines) {
				
				int whichRepeal;
				try {
					// extract resolution number, use API to get repealed resolution number
					Scanner scanner = new Scanner(textLines.get(textLines.size() - 1));
					int resNum = scanner.useDelimiter("\\d+").nextInt();
					scanner.close();
					
					String xmlRaw = new NSConnection("https://www.nationstates.net/cgi-bin/api.cgi?wa=1&id="
							+ resNum + "&q=resolution").getResponse();
					whichRepeal = Integer.parseInt(new XMLDocument(xmlRaw).xpath("/WA/RESOLUTION/REPEALED_BY/text()")
							.get(0));
					
				} catch (Exception e) {
					e.printStackTrace();
					whichRepeal = Integer.parseInt(JOptionPane.showInputDialog(frame,
							"Cannot find repealed resolution from API, enter the number of the resolution that " +
									"repealed this resolution (e.g. 326 GA)",
							"Parameter input", JOptionPane.PLAIN_MESSAGE));
				}
				
				String urlRepeal;
				try {
					String html = new NSConnection("http://forum.nationstates.net/viewtopic.php?f=9&t=30")
							.getResponse();
					Elements elements = Jsoup.parse(html).select("div#p310 div.content a");
					urlRepeal = elements.get(whichRepeal + 1).attr("abs:href"); // adjust for 0 -> 1 index
					
				} catch (IOException | RuntimeException e) {
					e.printStackTrace();
					urlRepeal = JOptionPane.showInputDialog(frame,
							"Cannot find repealing resolution in database, manually provide the RexisQuexis url of the " +
									"repealed resolution",
							"Parameter input", JOptionPane.PLAIN_MESSAGE);
				}
				
				String newStartLine = RQbb.color(RQbb.strike(textLines.get(0)), "gray") + " "
						+ RQbb.bold("[Struck out by "
								+ RQbb.post(whichRepeal + " GA", postInt(urlRepeal)) + "]");
				
				textLines.set(0, newStartLine);
				textLines.set(1, "[color=gray][strike]" + textLines.get(1));
				
				int lastStrikeLine = indexStartsWith("[b]Votes Against", textLines);
				textLines.set(lastStrikeLine, textLines.get(lastStrikeLine) + "[/strike][/color]");
				
				return textLines.stream().collect(Collectors.joining("\n"));
				
			}
		});
		buttonPanel.add(repealButton);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);
		
		JMenuItem undoItem = new JMenuItem("Undo");
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()));
		undoItem.addActionListener(e -> {
			if (undoManager.canUndo()) undoManager.undo();
		});
		editMenu.add(undoItem);
		
		JMenuItem redoItem = new JMenuItem("Redo");
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK));
		redoItem.addActionListener(e -> {
			if (undoManager.canRedo()) undoManager.redo();
		});
		editMenu.add(redoItem);
		
		JMenu viewMenu = new JMenu("View");
		menuBar.add(viewMenu);
		
		JCheckBoxMenuItem wordWrap = new JCheckBoxMenuItem("Word wrap");
		wordWrap.addActionListener(i -> {
			textArea.setWrapStyleWord(wordWrap.getState());
			textArea.setLineWrap(wordWrap.getState());
		});
		wordWrap.setState(true);
		viewMenu.add(wordWrap);
		
		panel.add(buttonPanel, BorderLayout.SOUTH);
		frame.setVisible(true);
	}
	
	// ** Helpful parsing and search methods **
	private int postInt(String postUrl) {
		return Integer.parseInt(postUrl.substring(postUrl.indexOf("#p") + "#p".length()));
	}
	
	static int indexStartsWith(String term, List<String> list) {
		for (int x = 0; x < list.size(); x++)
			if (list.get(x).startsWith(term)) return x;
		return -1;
	}
	
	// ** main **
	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
		SwingUtilities.invokeLater(RexisQuexis::new);
	}
}