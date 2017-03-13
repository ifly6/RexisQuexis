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
package com.git.ifly6.rexisquexis;

import com.git.ifly6.nsapi.NSConnection;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// This class, with public vars, is totally valid. Look:
// http://www.oracle.com/technetwork/java/javase/documentation/codeconventions-137265.html#177
public class WAResolution {

	public WAResolution() {
		try {
			topicUrl = new URL("https://www.nationstates.net");
		} catch (MalformedURLException e) {
			e.printStackTrace();    // this should never happen
			System.err.println("This should never happen, as it is hardcoded.");
		}
	}

	public static enum ResolutionType {
		NORMAL, REPEAL
	}

	public ResolutionType resolutionType = ResolutionType.NORMAL;

	// Information
	public int resolutionNum = 1;
	public String title = "The World Assembly";
	public String byLine = "A resolution to establish administrative parameters.";

	public String category = "Bookkeeping";
	public String strengthLine = "Sweeping";
	public String proposer = "Maxtopia";

	public String text = "The Adopted Nations of the World Assembly,\n"
			+ "NOTING that the United Nations has spectacularly imploded in a colossal fireball of extra-dimensional inanity;\n"
			+ "BELIEVING that there is a vital and needed role for a global organizational body;\n"
			+ "FURTHER NOTING that there are an awful lot of United Nations Resolutions;\n"
			+ "FURTHER BELIEVING that the demise of the United Nations may be fashioned into a grand opportunity for nations to draw a new destiny;\n"
			+ "HEREBY\n"
			+ "1. ESTABLISHES the World Assembly as the natural successor to the United Nations, with the full transfer of all Delegate ranks and associated endorsements;\n"
			+ "2. ARCHIVES all previously passed UN Resolutions for historical purposes, so that citizens of today may forever look back upon the masterwork of their ancestors;\n"
			+ "3. DECLARES the pages of international law to be blank;\n"
			+ "4. INVITES members of the World Assembly to begin work on a new volume, which may in time exceed even the grandeur of its predecessor.\n";

	public int votesFor = 11609;
	public int votesAgainst = 2259;

	public Date implementation = new GregorianCalendar(2008, 4, 6).getTime();
	public URL topicUrl;    // see constructor

	public boolean isRepealed = false;

	/**
	 * Formats the <code>WAResolution</code> from the fields into the format used by RexisQuexis, with valid bbCode
	 * for immediate posting of the resolution, though there are defaults which must be dealt with if posting a
	 * repeal. There are five elements, <code>$rid</code>, <code>$rtit</code>, <code>$rcat</code>, <code>$rstr</code>,
	 * <code>$rpst</code>, for the target resolution's ID, title, category, strength, and post number.
	 * @return a single <code>String</code> holding the RexisQuexis bbCode formatting
	 */
	public String format() {

		Map<String, String> defaults = new HashMap<>();
		defaults.put("targetId", "$rid");
		defaults.put("targetTitle", "$rtit");
		defaults.put("targetCategory", "$rcat");
		defaults.put("targetStrength", "$rstr");
		defaults.put("targetPost", "$rpst");

		return format(defaults);
	}

	/**
	 * Formats the <code>WAResolution</code> from the fields into the format used by RexisQuexis with valid bbCode
	 * for immediate posting of the resolution.
	 * @param data a <code>Map</code> which declares information if necessary. It is only needed for repeals, where it
	 *             provides information on the target resolution's ID (<code>targetId</code>), title
	 *             (<code>targetTitle</code>), category (<code>targetCategory</code>), strength
	 *             (<code>targetStrength</code>), and repeal topic post number (<code>targetPost</code>).
	 * @return a single <code>String</code> holding the RexisQuexis bbCode formatting
	 */
	public String format(Map<String, String> data) {

		List<String> lines = new ArrayList<>();

		// Title, byline, category
		lines.add(RQbb.bold(title));
		lines.add(RQbb.italicise(byLine));
		lines.add("");
		lines.add(String.format("%s %s", RQbb.bold("Category:"), category));

		// Area of effect, strength, or resolution?
		String effect = properEffect(strengthLine);
		lines.add(String.format("%s %s", RQbb.bold(effect + ":"), strengthLine));

		// Proposer line
		lines.add(String.format("%s %s", RQbb.bold("Proposed by:"), proposer));
		lines.add("");

		// Add relevant description line and the text
		if (resolutionType == ResolutionType.REPEAL) {

			String descriptionLine = "%s (Category: %s, %s: %s) shall be struck out and rendered null and void.";

			int repealId = Integer.parseInt(data.get("targetId"));
			String repealTitle = data.get("targetTitle");
			int postNum = Integer.parseInt(data.get("targetPost"));
			String repealCategory = data.get("targetCategory");
			String repealStrength = data.get("targetStrength");

			descriptionLine = String.format(descriptionLine,
					RQbb.post(String.format("General Assembly Resolution %d \"%s\"", repealId, repealTitle), postNum),
					/* repeal category */ repealCategory,
					/* find proper effect */ properEffect(repealStrength),
					/* put in repealed resolution's strength */ repealStrength);
			lines.add(RQbb.bold("Description: ") + descriptionLine);
			lines.add("");

			lines.add(RQbb.bold("Argument: ") + text);

		} else {
			lines.add(RQbb.bold("Description: ") + text);
		}
		lines.add("");

		// Add voting data
		lines.add(String.format("%s %d (%d%%)", RQbb.bold("Votes For:"), votesFor,
				votesFor * 100 / (votesAgainst + votesFor)));
		lines.add(String.format("%s %d (%d%%)", RQbb.bold("Votes Against:"), votesAgainst,
				votesAgainst * 100 / (votesAgainst + votesFor)));
		lines.add("");

		// Add implementation date
		lines.add(RQbb.color(String.format("Implemented %s",
				new SimpleDateFormat("EEE MMM d yyyy").format(implementation)), "red"));
		lines.add("");

		// Add bottom helper links
		lines.add(RQbb.bold(String.format("[%s] [%s]",
				RQbb.url(resolutionNum + " GA on NS", "http://www.nationstates.net/page=WA_past_resolutions/" +
						"council=1/start=" + (resolutionNum - 1)),
				RQbb.url("Official Debate Topic", topicUrl.toString()))));

		// Determine if repealed (and therefore, if further formatting is necessary)
		if (isRepealed && resolutionType != ResolutionType.REPEAL)
			return strikeThrough(lines.stream().collect(Collectors.joining("\n")));
		return lines.stream().collect(Collectors.joining("\n"));

	}

	/**
	 * Strikes through the resolution formatting. Queries the API to get the repealing resolution and its post. If it
	 * cannot find it, it puts in <code>-1</code> for the repeal number and <code>$repealUrl</code>.
	 */
	private static String strikeThrough(String input) {
		List<String> lines = Arrays.asList(input.split("\n"));

		int whichRepeal = -1;
		try {
			// extract resolution number, use API to get repealed resolution number
			int resNum = new Scanner(lines.get(lines.size() - 1)).useDelimiter("\\d+").nextInt();
			String xmlRaw = new NSConnection("https://www.nationstates.net/cgi-bin/api.cgi?wa=1&id="
					+ resNum + "&q=resolution").getResponse();
			whichRepeal = Integer.parseInt(new XMLDocument(xmlRaw).xpath("/WA/RESOLUTION/REPEALED_BY/text()")
					.get(0));

		} catch (Exception e) {
			e.printStackTrace();
		}

		String urlRepeal = "$repealUrl";
		try {
			String html = new NSConnection("http://forum.nationstates.net/viewtopic.php?f=9&t=30")
					.getResponse();
			Elements elements = Jsoup.parse(html).select("div#p310 div.content a");
			urlRepeal = elements.get(whichRepeal + 1).attr("abs:href"); // adjust for 0 -> 1 index

		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		int postInt = Integer.parseInt(urlRepeal.substring(urlRepeal.indexOf("#p") + 2));
		String newStartLine = RQbb.color(RQbb.strike(lines.get(0)), "gray") + " "
				+ RQbb.bold("[Struck out by " + RQbb.post(whichRepeal + " GA", postInt) + "]");

		lines.set(0, newStartLine);
		lines.set(1, "[color=gray][strike]" + lines.get(1));

		int lastStrikeLine = RexisQuexisParser.indexStartsWith("[b]Votes Against", lines);
		lines.set(lastStrikeLine, lines.get(lastStrikeLine) + "[/strike][/color]");

		return lines.stream().collect(Collectors.joining("\n"));
	}

	/**
	 * Parses a WA resolution from a copy of that resolution. It then connects to the Internet and tries it best to
	 * get information which should be present in the string copy direct from the WA resolutions page.
	 * @param input a <code>String</code> which contains the copy of a resolution
	 * @return a <code>WAResolution</code> containing that information
	 */
	public static WAResolution parse(String input) {

		List<String> lines = Arrays.asList(input.split("\n"));
		for (int x = lines.size() - 1; x > -1; x--) {    // remove trailing empty lines
			if (lines.get(x).trim().length() != 0) break;
			lines.remove(x);
		}

		WAResolution resolution = new WAResolution();

		// Reference lines
		int R_ID = 0;
		int R_TITLE = 2;
		int R_BYLINE = 4;
		int R_CATEGORY = 6;
		int R_STRENGTH = 8;
		int R_AUTHOR = 10;
		int R_IMPLEMENTATION = lines.size() - 12;
		int R_FOR = lines.size() - 8;
		int R_AGAINST = lines.size() - 2;

		resolution.resolutionNum = Integer.parseInt(lines.get(R_ID).substring(lines.get(R_ID).indexOf('#') + 1).trim());
		resolution.title = lines.get(R_TITLE).replace("Repeal:", "Repeal");

		if (resolution.title.contains("Repeal") || resolution.title.contains("repeal"))
			resolution.resolutionType = WAResolution.ResolutionType.REPEAL;

		resolution.byLine = lines.get(R_BYLINE);
		resolution.category = colon(lines.get(R_CATEGORY));
		resolution.strengthLine = colon(lines.get(R_STRENGTH));
		resolution.proposer = colon(lines.get(R_AUTHOR));

		try {
			resolution.implementation = new SimpleDateFormat("EEE MMM d yyyy").parse(lines.get(R_IMPLEMENTATION));
		} catch (ParseException e) {
			e.printStackTrace();
			String[] elements = lines.get(R_IMPLEMENTATION).split(" "); // assumes form: Thu May 14 2015
			int month = Month.valueOf(elements[1].toUpperCase()).getValue();
			int day = Integer.parseInt(elements[2]);
			int year = Integer.parseInt(elements[3]);
			resolution.implementation = new GregorianCalendar(year, month, day).getTime();
		}

		resolution.votesFor = Integer.parseInt(lines.get(R_FOR));
		resolution.votesAgainst = Integer.parseInt(lines.get(R_AGAINST));

		// text, isRepealed
		try {
			NSConnection connection = new NSConnection(String.format("https://www.nationstates.net/cgi-bin/api" +
					".cgi?wa=%d&id=%d&q=resolution", 1, resolution.resolutionNum));
			XML xml = new XMLDocument(connection.getResponse());
			String[] apiTextLines = xml.xpath("/WA/RESOLUTION/DESC/text()").get(0).replaceFirst
					("<!\\[CDATA\\[", "").replace("]]>", "").split("\n");

			resolution.text = Stream.of(apiTextLines)
					.map(s -> {
						String[] obsceneTags = {"\\[b\\]", "\\[i\\]", "\\[u\\]", "\\[/b\\]", "\\[/i\\]", "\\[/u\\]"};
						for (String element : obsceneTags)
							s = s.replaceAll(element, "");
						return s;
					})
					.map(s -> Jsoup.parse(s).text())
					.collect(Collectors.joining("\n"));

			// for some reason, there is no XML tag for this. Check for existence of repealed_by tag.
			try {
				xml.xpath("/WA/RESOLUTION/REPEALED_BY/text()").get(0);
				resolution.isRepealed = true;
			} catch (Exception e) {
				resolution.isRepealed = false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			// use anchors to get text, excluding as relevant, repeal description
			List<String> subLines = lines.subList(resolution.resolutionType == ResolutionType.REPEAL
							? R_AUTHOR + 4
							: R_AUTHOR + 2,
					R_IMPLEMENTATION - 3);
			resolution.text = subLines.stream().collect(Collectors.joining("\n"));

		}

		return resolution;

	}

	private String properEffect(String strength) {
		String effectLine = "Area of Effect";
		if (Arrays.asList("Mild", "Significant", "Strength").contains(strength)) effectLine = "Strength";
		if (resolutionType == ResolutionType.REPEAL) effectLine = "Resolution";
		return effectLine;
	}

	private static String colon(String input) {
		return input.substring(input.indexOf(':') + 1).trim();
	}

	@Override
	public String toString() {
		return "WAResolution{" +
				"resolutionType=" + resolutionType +
				", category='" + category + '\'' +
				", strengthLine='" + strengthLine + '\'' +
				", proposer='" + proposer + '\'' +
				", resolutionNum=" + resolutionNum +
				", title='" + title + '\'' +
				", byLine='" + byLine + '\'' +
				", text='" + text + '\'' +
				", votesFor=" + votesFor +
				", votesAgainst=" + votesAgainst +
				", implementation=" + implementation +
				", topicUrl=" + topicUrl +
				'}';
	}
}