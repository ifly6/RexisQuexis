package com.ifly6.rexisquexis.groups;

import com.ifly6.rexisquexis.io.RqForumUtilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kevin
 */
public class RqResolutionData {

    private static final transient Logger LOGGER = Logger.getLogger(RqResolutionData.class.getName());

    private String resolutionName;
    private int num;
    private String category;
    private String strength;
    private boolean repealed;
    private String author;

    private int postNum;

    public RqResolutionData(String resolutionName, int resolutionNum, String category, String strength, int postNum,
                            boolean repealed, String author) {
        this.resolutionName = RqForumUtilities.cleanQuotes(resolutionName);  // clean quotation marks to straight
        this.num = resolutionNum;
        this.category = category;
        this.strength = strength;
        this.postNum = postNum;
        this.repealed = repealed;
        this.author = author;
    }

    public String name() {
        return resolutionName;
    }

    public int num() {
        return num;
    }

    public String strength() {
        return strength;
    }

    public String category() {
        return category;
    }

    public int postNum() {
        return postNum;
    }

    public boolean isRepealed() {
        return repealed;
    }

    public void setRepealed(boolean repealed) {
        this.repealed = repealed;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Takes {@code List<RqResolutionData>} and runs through it. Because data is cached, there are cases where a
     * resolution valid when the data was gathered is repealed by a later resolution. This method checks that data for
     * later resolutions. If a later repeal is present, it matches to the earlier resolution by name and then marks it
     * as repealed.
     * @param data to check for later repeals
     * @returns nothing, method acts in place
     */
    public static void makeRepealsConsistent(final List<RqResolutionData> data) {
        // initialise fast look up to find targets quickly
        // case insensitive by forcing lower; strips all titles to remove trailing spaces
        Map<String, RqResolutionData> fastLookup = new HashMap<>();
        for (final var d : data) fastLookup.put(d.resolutionName.toLowerCase().strip(), d);

        // do check for whether resolution is repealed or not
        for (final var d : data) {
            // is the current resolution a repealing resolution?
            boolean isRepealingResolution = d.resolutionName.toLowerCase().startsWith("repeal");
            if (isRepealingResolution) {
                // if so, find out what it is repealing
                Matcher m = Pattern.compile("(?<=\")[\\w\\s-]+(?=\")").matcher(d.resolutionName.toLowerCase());
                if (m.find()) {
                    String repealedTitle = m.group().strip(); // get match, strip to eliminate trailing spaces
                    if (fastLookup.containsKey(repealedTitle)) { // make sure that name is present
                        RqResolutionData repealTarget = fastLookup.get(repealedTitle); // use lookup table for fast search
                        if (!repealTarget.isRepealed()) {
                            repealTarget.setRepealed(true); // set resolution as repealed if not already set
                            LOGGER.info(String.format("Set resolution GA %d <%s> as repealed",
                                    repealTarget.num(),
                                    repealTarget.name()
                            ));
                        }
                    } else {
                        // ensure that titles match up with past data:
                        // 'space station research' != 'space research station'
                        throw new TitleSearchException(
                                String.format("cannot find repeal target <%s> by name in list of resolutions",
                                        repealedTitle));
                    }
                } else {
                    // if the matcher cannot find anything
                    // check the regex if it doesn't match, missing hyphen in character matcher caused error in past
                    throw new TitleParseException(
                            String.format("cannot parse target name of repeal resolution GA %d <%s>",
                                    d.num(), d.name()));
                }
            } // else (ie this resolution is not a repeal), do nothing
        }
    }

    /**
     * Indicates that parser could not parse title from provided input.
     */
    public static class TitleParseException extends RuntimeException {
        public TitleParseException(String format) {
            super(format);
        }
    }

    /**
     * Indicates that search of titles could not find match.
     */
    public static class TitleSearchException extends RuntimeException {
        public TitleSearchException(String format) {
            super(format);
        }
    }

}
