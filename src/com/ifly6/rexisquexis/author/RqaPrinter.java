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
package com.ifly6.rexisquexis.author;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.ifly6.rexisquexis.RQbb;
import org.apache.commons.lang3.text.WordUtils;

/**
 * @author Kevin
 *
 */
public class RqaPrinter {

	List<String> lines;

	private HashMap<RqaResolutionData, String> categoryMap;

	public RqaPrinter() {
		lines = new ArrayList<String>();
	}

	/**
	 * @param resolutionList
	 * @param categoryMap
	 */
	public RqaPrinter(List<RqaResolutionData> resolutionList, HashMap<RqaResolutionData, String> categoryMap) {
		this();
		this.categoryMap = categoryMap;
	}

	public String print() {

		Map<String, List<RqaResolutionData>> byCategory = mapByCategory();

		// Write each section
		SortedSet<String> keys = new TreeSet<String>(byCategory.keySet());
		for (String key : keys) {

			List<RqaResolutionData> resList = byCategory.get(key);

			if (resList.size() > 1) {

				Comparator<RqaResolutionData> comparator = Comparator.comparing(r -> r.num());
				resList.sort(comparator);

				append(RQbb.header(key, resList.size() + " " + ((resList.size() > 1) ? "resolutions" : "resolution")));

				append("[floatleft]");
				for (RqaResolutionData resolution : resList) {
					append(RQbb.align(WordUtils.capitalize(resolution.strength()) + ": ", RQbb.RIGHT));
				}
				append("[/floatleft]");

				for (RqaResolutionData resolution : resList) {

					String resolutionLink = RQbb.post(resolution.num() + " GA '" + resolution.name() + "'",
							resolution.postNum());

					// Strike through if repealed
					if (resolution.isRepealed()) {
						append(RQbb.strike(RQbb.tab(10) + resolutionLink));

					} else {
						append(RQbb.tab(10) + resolutionLink);
					}

				}
				appendln();
			}

		}
		return makeString();

	}

	private Map<String, List<RqaResolutionData>> mapByCategory() {

		Collection<String> categoryList = categoryMap.values();
		Map<String, List<RqaResolutionData>> byCategory = new TreeMap<String, List<RqaResolutionData>>();

		// Create empty lists
		for (String catName : categoryList) {
			byCategory.put(catName, new ArrayList<RqaResolutionData>());
		}

		// Populate empty lists
		for (Map.Entry<RqaResolutionData, String> entry : categoryMap.entrySet()) {

			List<RqaResolutionData> mapList = byCategory.get(entry.getValue());
			mapList.add(entry.getKey());

			mapList.sort(new Comparator<RqaResolutionData>() {
				@Override public int compare(RqaResolutionData o1, RqaResolutionData o2) {
					return Integer.compare(o1.num(), o2.num());
				}
			});
		}

		return byCategory;
	}

	private RqaPrinter append(String input) {
		lines.add(input);
		return this;
	}

	private RqaPrinter appendln() {
		this.append("\n");
		return this;
	}

	private String makeString() {

		StringBuilder builder = new StringBuilder();
		Iterator<String> iterator = lines.iterator();

		builder.append(iterator.next());
		while (iterator.hasNext()) {
			builder.append("\n" + iterator.next());
		}

		String last = builder.toString();
		return last.replace("[/floatleft]\n", "[/floatleft]").replace("[/align]\n", "[/align]");
	}
}
