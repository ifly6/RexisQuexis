/*
 * Copyright (c) 2017 ifly6
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
package com.ifly6.rexisquexis.categories;

import com.git.ifly6.nsapi.NSConnection;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/** @author ifly6 */
public class RqcCategories {

	private JProgressBar progressBar;

	public RqcCategories() {

		JFrame frame = new JFrame();
		frame.setTitle("RexisQuexis Categories Parser");
		frame.setSize(400, 400);
		frame.setLocation(100, 100);

		JPanel panel = new JPanel();
		frame.setContentPane(panel);
		panel.setLayout(new BorderLayout());

		JLabel label = new JLabel("<html>Hit the button. There is no longer any need to paste in data.</html>");
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.add(label, BorderLayout.NORTH);

		final JTextArea ta = new JTextArea();
		ta.setFont(new Font(Font.MONOSPACED, 0, 11));
		ta.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		ta.setWrapStyleWord(true);
		ta.setLineWrap(true);

		panel.add(new JScrollPane(ta), BorderLayout.CENTER);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2, 1));
		panel.add(controlPanel, BorderLayout.SOUTH);

		progressBar = new JProgressBar();
		progressBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		controlPanel.add(progressBar);

		JButton button = new JButton("Parse");
		button.addActionListener(event -> {

			Runnable runnable = () -> {
				try {

					List<RqcResolutionData> resolutionList = parseSource();
					HashMap<RqcResolutionData, String> categoryMap = new HashMap<>();

					for (RqcResolutionData it : resolutionList)
						categoryMap.put(it, it.category());

					RqcPrinter printer = new RqcPrinter(categoryMap);
					ta.setText(printer.print());

				} catch (IOException e) {
					e.printStackTrace();
					ta.setText(e.toString());
				}

			};
			runnable.run();

		});
		controlPanel.add(button);

		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new RqcCategories());
	}

	/** Scrapes and parses the data from the RexisQuexis main page.
	 * @return <code>List&lt;RqcResolutionData&gt;</code> containing all relevant resolutions.
	 * @throws IOException */
	private List<RqcResolutionData> parseSource() throws IOException {

		// TODO parse the source code for the entire list. then, using the 'li' code, assign the resolution number
		// after that, then query the API for the resolution category and strength and return the list.

		List<RqcResolutionData> resList = new ArrayList<>();

		Elements elements = Jsoup.parse(new URL("http://forum.nationstates.net/viewtopic.php?f=9&t=30"), 2000)
				.select("div#p310 div.content a");
		int matches = elements.size();

		System.out.println(
				"For " + matches + " elements, this will take " + time(Math.round(NSConnection.WAIT_TIME * matches / 1000)));

		int counter = 1;
		progressBar.setValue(0);
		progressBar.setMaximum(matches);

		Iterator<Element> iterator = elements.iterator();
		while (iterator.hasNext()) {

			Element element = iterator.next();

			// Get some basic information
			String title = element.text();
			String postLink = element.attr("href");
			int postNum = Integer.parseInt(postLink.substring(postLink.indexOf("#p") + 2, postLink.length()));

			// Query the API
			NSConnection connection = new NSConnection(
					"https://www.nationstates.net/cgi-bin/api.cgi?wa=1&id=" + counter + "&q=resolution");
			System.out.println("Queried for resolution " + counter + " of " + matches);

			// Iterate progressBar
			((Runnable) () -> progressBar.setValue(progressBar.getValue() + 1)).run();
			
			// Parse the API response
			XML xml = new XMLDocument(connection.getResponse());
			String category = xml.xpath("/WA/RESOLUTION/CATEGORY/text()").get(0);
			String strength = xml.xpath("/WA/RESOLUTION/OPTION/text()").get(0);
			if (strength.equals("0")) {

				if (category.equalsIgnoreCase("Environmental")) {
					strength = "automotive";
				} else if (category.equalsIgnoreCase("Health")) {
					strength = "Healthcare";
				} else if (category.equalsIgnoreCase("Education and Creativity")) {
					strength = "Artistic";
				} else if (category.equalsIgnoreCase("Gun Control")) {
					strength = "Tighten";
				} else {
					strength = "mild";
				}
			}

			boolean repealed = true;
			try {
				xml.xpath("/WA/RESOLUTION/REPEALED/text()").get(0);
			} catch (RuntimeException e) {
				repealed = false;
			}

			// Make the resolution, add, and increment
			resList.add(new RqcResolutionData(title, counter, category, strength, postNum, repealed));
			counter++;
		}

		return resList;
	}

	public static String time(int seconds) {
		int minutes = seconds / 60;
		seconds -= minutes * 60;
		int hours = minutes / 60;
		minutes -= hours * 60;
		int days = hours / 24;
		hours -= days * 24;
		return days + "d:" + hours + "h:" + minutes + "m:" + seconds + "s";
	}
}
