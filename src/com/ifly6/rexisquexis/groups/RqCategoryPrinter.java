package com.ifly6.rexisquexis.groups;

import com.ifly6.rexisquexis.RQbb;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * RqCategoryPrinter is a utility to print an input <code>HashMap&lt;RqResolutionData, String&gt;</code> into the valid
 * phpBB bbCode for posting. This is here to produce the code necessary for RexisQuexis categories.
 */
@SuppressWarnings("WeakerAccess")
public class RqCategoryPrinter {

    private List<String> lines;
    private Map<RqResolutionData, String> categoryMap;

    private List<RqResolutionData> resolutions;

    public RqCategoryPrinter() {
        lines = new ArrayList<>();
    }

    /**
     * @param categoryMap containing each resolution along with its assigned corresponding category
     */
    public RqCategoryPrinter(Map<RqResolutionData, String> categoryMap) {
        this();
        this.categoryMap = categoryMap;
    }

    public String print() {

        Map<String, List<RqResolutionData>> byCategory = categoryMap.keySet().stream()
                .filter(r -> !r.isRepealed())
                .collect(Collectors.groupingBy(RqResolutionData::category));
        Comparator<RqResolutionData> comparator = Comparator.comparing(RqResolutionData::strength)
                .thenComparing(RqResolutionData::num);

        lines.add("&nbsp;");   // add a new line at the top

        // Write each section
        Set<String> keys = new TreeSet<>(byCategory.keySet());
        for (String categoryName : keys) {
            // Get the category resolution list
            List<RqResolutionData> categoryResolutions = byCategory.get(categoryName);

            if (!categoryName.equalsIgnoreCase("repeal") || categoryResolutions.isEmpty()) {
                categoryResolutions.sort(comparator);

                String pluralForm = categoryResolutions.size() > 1 ? "resolutions" : "resolution";
                lines.add(RQbb.header(categoryName, categoryResolutions.size() + " " + pluralForm));

                lines.add("[floatleft][align=right]");
                for (RqResolutionData resolution : categoryResolutions)
                    lines.add(WordUtils.capitalize(resolution.strength()) + ": ");

                lines.add("[/align][/floatleft]");

                for (RqResolutionData resolution : categoryResolutions) {
                    if (resolution.isRepealed())
                        lines.add(RQbb.strike(RQbb.tab(10) + RQbb.post(RQbb.color(resolution
                                        .num() + " GA '" + resolution.name() + "'", "gray"),
                                resolution.postNum())));
                    else
                        lines.add(RQbb.post(String.format("%sGA %d '%s'", RQbb.tab(10), resolution.num(), resolution.name()),
                                resolution.postNum()));
                }
                lines.add("\n");
            }

        }

        return makeString();
    }

    private String makeString() {
        // it gets rid of new lines here because floatleft and align are divs in bbCode, which have their own padding
        return String.join("\n", lines)
                .replace("[/floatleft]\n", "[/floatleft]")
                .replace("[/align]\n", "[/align]");
    }
}
