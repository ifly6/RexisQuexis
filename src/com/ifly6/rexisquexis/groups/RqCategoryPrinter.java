package com.ifly6.rexisquexis.groups;

import com.ifly6.rexisquexis.RQbb;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@code RqCategoryPrinter} is a utility to print an input {@code HashMap<RqResolutionData, String>} into the valid
 * phpBB bbCode for posting.
 */
@SuppressWarnings("WeakerAccess")
public class RqCategoryPrinter {

    private List<String> lines;

    /**
     * Map with keys of {@link RqResolutionData} and key {@code String} with category.
     */
    private Map<RqResolutionData, String> categoryMap;

    /**
     * Prevent instantiation unless appropriate variables are provided.
     */
    private RqCategoryPrinter() {
    }

    /**
     * @param resolutions containing all resolutions
     */
    public RqCategoryPrinter(List<RqResolutionData> resolutions) {
        lines = new ArrayList<>();
        this.categoryMap = resolutions.stream()
                .collect(Collectors.toMap(Function.identity(), RqResolutionData::category));
    }

    /**
     * Prints the {@code RqCategories} data to phpBB bbCode. After instantiation with appropriate variables, this is
     * basically a kingdom of the nouns {@code execute()}.
     * @return the sought-after phpBB bbCode to copy-paste to the forum.
     */
    public String print() {
        // invert mapping to get a 1:m with sub-lists for category to resolutions
        Map<String, List<RqResolutionData>> byCategory = categoryMap.keySet().stream()
                .filter(r -> !r.isRepealed()) // ignore repealed resolutions
                .filter(r -> r.category().equalsIgnoreCase("repeal")) // ignore all repeals
                .collect(Collectors.groupingBy(RqResolutionData::category));

        // create comparator to operate within category, strength first; numbers tie-break
        Comparator<RqResolutionData> comparator = Comparator.comparing(RqResolutionData::strength)
                .thenComparing(RqResolutionData::num);

        lines.add("&nbsp;");   // add a new line at the top

        // write sections one by one
        // use tree-set to sort category names
        for (String categoryName : new TreeSet<>(byCategory.keySet())) {
            // get resolutions in category
            List<RqResolutionData> categoryResolutions = byCategory.get(categoryName);
            if (!categoryName.equalsIgnoreCase("repeal") || categoryResolutions.isEmpty()) {
                categoryResolutions.sort(comparator);

                // add header, change pluralisation as needed
                lines.add(RQbb.header(categoryName, categoryResolutions.size() + " "
                        + (categoryResolutions.size() != 1 ? "resolutions" : "resolution")));

                // add category data
                lines.add("[floatleft][align=right]");
                for (RqResolutionData resolution : categoryResolutions)
                    lines.add(WordUtils.capitalize(resolution.strength()) + ": ");
                lines.add("[/align][/floatleft]");

                // add resolutions
                // nb repealed-resolution handling code removed; repealed resolutions no longer displayed
                for (RqResolutionData resolution : categoryResolutions)
                    lines.add(RQbb.post(String.format("%sGA %d '%s'", RQbb.tab(10), resolution.num(), resolution.name()),
                            resolution.postNum()));

                lines.add("\n");
            }
        }

        return makeString();
    }

    /**
     * Joins {@code List<String>} and cleans.
     * @return joined cleaned {@code String}
     */
    private String makeString() {
        // it gets rid of new lines here because floatleft and align are divs in bbCode, which have their own padding
        return String.join("\n", lines)
                .replace("[/floatleft]\n", "[/floatleft]")
                .replace("[/align]\n", "[/align]");
    }
}
