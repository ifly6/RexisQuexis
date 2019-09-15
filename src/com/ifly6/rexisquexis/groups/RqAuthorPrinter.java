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
package com.ifly6.rexisquexis.groups;

import com.ifly6.rexisquexis.RQbb;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author ifly6
 */
@SuppressWarnings({"JavaDoc", "WeakerAccess", "UnusedReturnValue"})
public class RqAuthorPrinter {

    List<String> lines;

    private Map<RqResolutionData, String> categoryMap;

    public RqAuthorPrinter() {
        lines = new ArrayList<>();
    }

    public RqAuthorPrinter(Map<RqResolutionData, String> categoryMap) {
        this();
        this.categoryMap = categoryMap;
    }

    public String print() {

        Set<RqResolutionData> resolutions = categoryMap.keySet();
        Comparator<RqResolutionData> comparator = Comparator.comparing(RqResolutionData::num);

        Map<String, List<RqResolutionData>> authorGroups =
                resolutions.stream().collect(Collectors.groupingBy(RqResolutionData::getAuthor));

        // Write each section
        SortedSet<String> keys = new TreeSet<>(authorGroups.keySet());
        for (String authorName : keys) {

            List<RqResolutionData> resList = authorGroups.get(authorName);
            if (resList.size() > 1) {

                resList.sort(comparator); // sort

                // append header
                lines.add(RQbb.header(WordUtils.capitalize(authorName),
                        String.format("%d resolution%s",
                                resList.size(),
                                resList.size() > 1 ? "s" : "")));

                for (RqResolutionData resolution : resList) {
                    String resolutionLink = RQbb.post(String.format("%d GA '%s'", resolution.num(), resolution.name()),
                            resolution.postNum());
                    String line = RQbb.tab(10) + resolutionLink;
                    lines.add(resolution.isRepealed() ? RQbb.strike(line) : line); // Strike through if repealed
                }
                lines.add("\n");

            }
        }

        return makeString();
    }

    private String makeString() {
        return String.join("\n", lines)
                .replace("[/floatleft]\n", "[/floatleft]")
                .replace("[/align]\n", "[/align]");
    }
}
