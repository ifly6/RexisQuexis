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
package com.git.ifly6.rexisquexis.categories;

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

import org.apache.commons.lang3.text.WordUtils;

import com.git.ifly6.rexisquexis.RQbb;

/**
 * @author ifly6
 *
 */
public class RqcPrinter {

	List<String> lines;

	private HashMap<RqcResolutionData, String> categoryMap;

	public RqcPrinter() {
		lines = new ArrayList<String>();
	}

	/**
	 * @param resolutionList
	 * @param categoryMap
	 */
	public RqcPrinter(List<RqcResolutionData> resolutionList, HashMap<RqcResolutionData, String> categoryMap) {
		this();
		this.categoryMap = categoryMap;
	}

	public String print() {

		Map<String, List<RqcResolutionData>> byCategory = mapByCategory();

		// Write header stuff
		append(RQbb.box("Welcome to " + RQbb.bold("RexisQuexis™ Categories")
				+ ", the premier resolutions database, now sorted by category!"));

		// Write each section
		SortedSet<String> keys = new TreeSet<String>(byCategory.keySet());
		for (String key : keys) {

			if (!key.equalsIgnoreCase("repeal")) {
				List<RqcResolutionData> catResolutions = byCategory.get(key);

				Comparator<RqcResolutionData> comparator = Comparator.comparing(r -> r.strength());
				comparator = comparator.thenComparing(Comparator.comparing(r -> r.num()));
				catResolutions.sort(comparator);

				append(RQbb.header(key));

				append("[floatleft]");
				for (RqcResolutionData resolution : catResolutions) {
					append(RQbb.align(WordUtils.capitalize(resolution.strength()) + ": ", RQbb.RIGHT));
				}
				append("[/floatleft]");

				for (RqcResolutionData resolution : catResolutions) {

					// Strike through if repealed
					if (resolution.isRepealed()) {
						append(RQbb.strike(RQbb.tab(10) +
								RQbb.post(RQbb.color(resolution.num() + " GA '" + resolution.name() + "'", "gray"), resolution.postNum())));

					} else {
						append(RQbb.post(RQbb.tab(10) + resolution.num() + " GA '" + resolution.name() + "'",
								resolution.postNum()));
					}

				}
				appendln();
			}

		}
		return makeString();

	}

	private Map<String, List<RqcResolutionData>> mapByCategory() {

		Collection<String> categoryList = categoryMap.values();
		Map<String, List<RqcResolutionData>> byCategory = new TreeMap<String, List<RqcResolutionData>>();

		// Create empty lists
		for (String catName : categoryList) {
			byCategory.put(catName, new ArrayList<RqcResolutionData>());
		}

		// Populate empty lists
		for (Map.Entry<RqcResolutionData, String> entry : categoryMap.entrySet()) {

			List<RqcResolutionData> mapList = byCategory.get(entry.getValue());
			mapList.add(entry.getKey());

			mapList.sort(new Comparator<RqcResolutionData>() {
				@Override public int compare(RqcResolutionData o1, RqcResolutionData o2) {
					return Integer.compare(o1.num(), o2.num());
				}
			});
		}

		return byCategory;
	}

	private RqcPrinter append(String input) {
		lines.add(input);
		return this;
	}

	private RqcPrinter appendln() {
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
