package com.ifly6.rexisquexis.groups;

import com.ifly6.rexisquexis.RQbb;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Deprecated
@SuppressWarnings({"JavaDoc", "WeakerAccess", "UnusedReturnValue"})
public class RqAuthorPrinter {

    private static Comparator<RqResolutionData> comparator = Comparator.comparing(RqResolutionData::num);

    private List<String> lines = new ArrayList<>();
    private Map<RqResolutionData, String> categoryMap;

    public RqAuthorPrinter() {
    }

    public RqAuthorPrinter(Map<RqResolutionData, String> categoryMap) {
        this();
        this.categoryMap = categoryMap;
    }

    public String print() {

        Set<RqResolutionData> resolutions = categoryMap.keySet();


        Map<String, List<RqResolutionData>> authorGroups =
                resolutions.stream().collect(Collectors.groupingBy(RqResolutionData::getAuthor));

        lines.add(RQbb.header("Authors", "by number of resolutions", 150, false));
        lines.add(makeTable(authorGroups));
        lines.add("\n");

        lines.add(RQbb.header("Authors", "by resolution", 150, false));
        lines.addAll(makeList(authorGroups));

        return makeString();
    }

    private String makeTable(Map<String, List<RqResolutionData>> authorGroups) {
        List<String> internalList = new ArrayList<>();
        internalList.add("[tr][td][b]Author[/b][/td][td][b]Resolution count[/b][/td][/tr]");

        authorGroups.entrySet().stream()
                .sorted(Comparator
                        .comparing((Map.Entry<String, List<RqResolutionData>> i) -> 1 - i.getValue().size())
                        .thenComparing(Map.Entry::getKey))
                .map(entry -> String.format("[tr][td]%s[/td][td]%d[/td][/tr]",  // map to this format
                        formatAuthorName(entry.getKey()),  // capitalise name
                        entry.getValue().size()))  // get count
                .forEach(internalList::add);  // add to internal list

        return RQbb.bbTag(String.join("", internalList), "table");  // surround list with table tags
    }

    private Collection<? extends String> makeList(Map<String, List<RqResolutionData>> authorGroups) {
        List<String> internalLines = new ArrayList<>();
        // Write each section
        SortedSet<String> keys = new TreeSet<>(authorGroups.keySet());
        for (String authorName : keys) {

            List<RqResolutionData> resList = authorGroups.get(authorName);
            if (resList.size() > 1) {

                resList.sort(comparator); // sort

                // append header
                internalLines.add(RQbb.header(formatAuthorName(authorName),
                        String.format("%d resolution%s",
                                resList.size(),
                                resList.size() > 1 ? "s" : ""),
                        134,
                        true));

                for (RqResolutionData resolution : resList) {
                    String resolutionLink = RQbb.post(String.format("GA %d '%s'", resolution.num(), resolution.name()),
                            resolution.postNum());
                    String line = RQbb.tab(10) + resolutionLink;
                    internalLines.add(resolution.isRepealed() ? RQbb.strike(line) : line); // Strike through if repealed
                }
                internalLines.add("\n");

            }
        }
        return internalLines;
    }

    private String makeString() {
        return String.join("\n", lines)
                .replace("[/floatleft]\n", "[/floatleft]")
                .replace("[/align]\n", "[/align]");
    }

    private String formatAuthorName(String s) {
        return WordUtils.capitalize(s.replace("_", " "));
    }

}
