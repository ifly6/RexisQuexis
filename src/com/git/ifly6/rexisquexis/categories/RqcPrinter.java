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
package com.git.ifly6.rexisquexis.categories;

import com.git.ifly6.rexisquexis.RQbb;
import org.apache.commons.lang3.text.WordUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RqcPrinter {

	private List<String> lines;
	private HashMap<RqcResolutionData, String> categoryMap;
	
	public RqcPrinter() {
		lines = new ArrayList<>();
	}
	
	/** @param categoryMap containing each resolution along with a corresponding category */
	public RqcPrinter(HashMap<RqcResolutionData, String> categoryMap) {
		this();
		this.categoryMap = categoryMap;
	}
	
	public String print() {

		Map<String, List<RqcResolutionData>> byCategory = mapByCategory();
		
		// Write each section
		SortedSet<String> keys = new TreeSet<>(byCategory.keySet());
		for (String key : keys) {

			// Get the category resolution list
			List<RqcResolutionData> catResList = byCategory.get(key);

			if (!key.equalsIgnoreCase("repeal") || catResList.isEmpty()) {	// if it is empty, ignore it
				// note that in mapByCategory(), repealed resolutions are not added to the list
				// also note that the first term excludes repeals from the category lists
				
				Comparator<RqcResolutionData> comparator = Comparator.comparing(r -> r.strength());
				comparator = comparator.thenComparing(Comparator.comparing(r -> r.num()));
				catResList.sort(comparator);
				
				append(RQbb.header(key, catResList.size() + " " + (catResList.size() > 1 ? "resolutions" : "resolution")));
				
				append("[floatleft][align=right]");
				for (RqcResolutionData resolution : catResList)
					append(WordUtils.capitalize(resolution.strength()) + ": ");

				append("[/align][/floatleft]");
				
				for (RqcResolutionData resolution : catResList) {
					if (resolution.isRepealed()) append(RQbb.strike(RQbb.tab(10) + RQbb.post(RQbb.color(resolution
									.num() + " GA '" + resolution.name() + "'", "gray"),
							resolution.postNum())));
					else append(RQbb.post(RQbb.tab(10) + resolution.num() + " GA '" + resolution.name() + "'",
							resolution.postNum()));
				}
				append("\n");
			}
			
		}
		
		return makeString();
	}
	
	private String makeString() {
		String string = lines.stream().collect(Collectors.joining("\n"));
		return string.replace("[/floatleft]\n", "[/floatleft]").replace("[/align]\n", "[/align]");
		// it gets rid of new lines here because floatleft and align are divs in bbCode, which have their own padding
	}
	
	private Map<String, List<RqcResolutionData>> mapByCategory() {

		Collection<String> categoryList = categoryMap.values();
		Map<String, List<RqcResolutionData>> byCategory = new TreeMap<>();
		
		// Create empty lists
		categoryList.stream().forEach(x -> byCategory.put(x, new ArrayList<RqcResolutionData>()));
		
		// Populate empty lists
		for (Map.Entry<RqcResolutionData, String> entry : categoryMap.entrySet()) {
			if (!entry.getKey().isRepealed()) {	// do not include repealed resolutions
				List<RqcResolutionData> mapList = byCategory.get(entry.getValue()); 	// get the list in the category
				mapList.add(entry.getKey());	// add it
				mapList.sort((o1, o2) -> Integer.compare(o1.num(), o2.num()));	// what does this do?
			}
		}
		
		return byCategory;
	}
	
	private RqcPrinter append(String input) {
		lines.add(input);
		return this;
	}
}
