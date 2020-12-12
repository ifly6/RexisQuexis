package com.ifly6.rexisquexis;

import com.git.ifly6.nsapi.NSConnection;
import com.ifly6.rexisquexis.cp1252escaper.EscapeCP1252;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

// This class, with public vars, is totally valid. Look:
// http://www.oracle.com/technetwork/java/javase/documentation/codeconventions-137265.html#177
@SuppressWarnings({"WeakerAccess", "MagicConstant"})
public class GAResolution {

    transient private static final Logger LOGGER = Logger.getLogger(GAResolution.class.getName());

    /**
     * Mapping HTML numeric entities wrongly coded as CP-1252 to HTML entities. Not exhaustive. Apply using for-loop.
     */
    private static final Map<String, String> CP1252_MAPPER;

    static {
        HashMap<String, String> initialMap = new HashMap<>();
        initialMap.put("&#145;", "&apos;");
        initialMap.put("&#146;", "&apos;");
        initialMap.put("&#147;", "&ldquo;");
        initialMap.put("&#148;", "&rdquo;");
        initialMap.put("&#133;", "&hellip;");
        initialMap.put("&#150;", "&ndash;");
        initialMap.put("&#151;", "&mdash;");
        CP1252_MAPPER = initialMap;
    }

    public enum GAType {
        NORMAL, REPEAL
    }

    public GAType type = GAType.NORMAL;

    // Information
    public int resolutionNum = 1;
    public String title = "The World Assembly";
    public String byLine = "A resolution to establish administrative parameters.";

    public String category = "Bookkeeping";
    public String strengthLine = "Sweeping";
    public String proposer = "Maxtopia";

    public String text = "The Adopted Nations of the World Assembly,\n\n"
            + "NOTING that the United Nations has spectacularly imploded in a colossal fireball of extra-dimensional inanity;\n\n"
            + "BELIEVING that there is a vital and needed role for a global organizational body;\n\n"
            + "FURTHER NOTING that there are an awful lot of United Nations Resolutions;\n\n"
            + "FURTHER BELIEVING that the demise of the United Nations may be fashioned into a grand opportunity for nations to draw a new destiny;\n\n"
            + "HEREBY\n\n"
            + "1. ESTABLISHES the World Assembly as the natural successor to the United Nations, with the full transfer of all Delegate ranks and associated endorsements;\n\n"
            + "2. ARCHIVES all previously passed UN Resolutions for historical purposes, so that citizens of today may forever look back upon the masterwork of their ancestors;\n\n"
            + "3. DECLARES the pages of international law to be blank;\n\n"
            + "4. INVITES members of the World Assembly to begin work on a new volume, which may in time exceed even the grandeur of its predecessor.";

    public int votesFor = 11609;
    public int votesAgainst = 2259;

    public Date implementation = new GregorianCalendar(2008, Calendar.APRIL, 6).getTime();
    public String topicUrl = "$topicURL";

    public boolean isRepealed = false;
    public GARepealData repealData = new GARepealData();

    /**
     * Formats the <code>GAResolution</code> from the fields into the format used by RexisQuexis with valid bbCode for
     * immediate posting of the resolution. There are defaults from initialisation which must be dealt with if posting a
     * repeal. There are five elements, <code>-1</code>, <code>$rtit</code>, <code>$rcat</code>, <code>$rstr</code>,
     * <code>-1</code>, for the target resolution's ID, title, category, strength, and post number.
     * <p>
     * Before directly applying these defaults, it attempts to search relevant databases for the information it needs to
     * fill in the correct information.
     * </p>
     * @return a single <code>String</code> holding the RexisQuexis bbCode formatting
     */
    @SuppressWarnings("ConstantConditions")
    public String format() {

        List<String> lines = new ArrayList<>();

        // Title, byline, category
        lines.add(RQbb.bold(title));
        lines.add(RQbb.italicise(byLine));
        lines.add("");
        lines.add(String.format("%s %s", RQbb.bold("Category:"), category));

        // Area of effect, strength, or resolution?
        String effect = createEffectLine(strengthLine, category);
        lines.add(String.format("%s %s", RQbb.bold(effect + ":"), strengthLine));

        // Proposer line
        lines.add(String.format("%s %s", RQbb.bold("Proposed by:"), proposer));
        lines.add("");

        // Add relevant description line and the text
        if (this.type == GAType.REPEAL) {

            String descriptionLine = "%s (Category: %s, %s: %s) shall be struck out and rendered null and void.";

            int repealId = repealData.targetId;
            String repealTitle = repealData.targetTitle;
            int postNum = repealData.targetPost;
            String repealCategory = repealData.targetCategory;
            String repealStrength = repealData.targetStrength;

            descriptionLine = String.format(descriptionLine,
                    RQbb.post(String.format("General Assembly Resolution #%d \"%s\"", repealId, repealTitle), postNum),
                    /* repeal category */ repealCategory,
                    /* find proper effect */ createEffectLine(repealStrength, repealCategory),
                    /* put in repealed resolution's strength */ translateEffect(repealStrength, repealCategory));
            lines.add(RQbb.bold("Description:") + " " + descriptionLine);
            lines.add("");

            lines.add(RQbb.bold("Argument:") + " " + text);

        } else lines.add(RQbb.bold("Text:") + " " + text);

        lines.add("");

        // Add voting data
        lines.add(String.format("%s %,d (%.1f%%)", RQbb.bold("Votes For:"), votesFor,
                (double) votesFor * 100 / (votesAgainst + votesFor)).replace(",", " "));
        lines.add(String.format("%s %,d (%.1f%%)", RQbb.bold("Votes Against:"), votesAgainst,
                (double) votesAgainst * 100 / (votesAgainst + votesFor)).replace(",", " "));
        lines.add("");

        // Add implementation date
        lines.add(RQbb.color(String.format("Implemented %s",
                new SimpleDateFormat("EEE MMM d yyyy").format(implementation)), "red"));
        lines.add("");

        // Add bottom helper links
        String linkSet = RQbb.bold(String.format("[%s] [%s]",
                RQbb.url(String.format("GA %d on NS", resolutionNum), "http://www.nationstates.net/page=WA_past_resolutions/" +
                        "council=1/start=" + (resolutionNum - 1)),
                RQbb.url("Official Debate Topic", topicUrl)));
        lines.add(RQbb.size(linkSet, RQbb.SMALL));

        // Determine if repealed (and therefore, if further formatting is necessary)
        if (isRepealed && type != GAType.REPEAL)
            lines = strikeThrough(lines, this);
        return String.join("\n", lines);

    }

    /**
     * Strikes through the resolution formatting. Queries the API to get the repealing resolution and its post. If it
     * cannot find it, it puts in <code>-1</code> for the repeal number and <code>$repealUrl</code>.
     */
    private static List<String> strikeThrough(List<String> lines, GAResolution gaResolution) {

        int whichRepeal = -1;   // get the id of the resolution which repealed this resolution
        try {
            int resNum = gaResolution.resolutionNum;
            String xmlRaw = new NSConnection("https://www.nationstates.net/cgi-bin/api.cgi?wa=1&id="
                    + resNum + "&q=resolution").getResponse();
            whichRepeal = Integer.parseInt(new XMLDocument(xmlRaw).xpath("/WA/RESOLUTION/REPEALED_BY/text()")
                    .get(0));

        } catch (Exception e) {
            e.printStackTrace();
        }

        String urlRepeal;
        urlRepeal = searchTopics(whichRepeal).toString();

        int postInt;    // post-int isn't always going to be there, because urlRepeal can return the NS forum URL
        try {
            postInt = Integer.parseInt(urlRepeal.substring(urlRepeal.indexOf("#p") + 2));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.INFO, "Cannot find repeal substring", e);
            postInt = -1;
        }
        String newStartLine = RQbb.color(RQbb.strike(lines.get(0)), "gray") + " "
                + RQbb.bold(String.format("[Struck out by %s]", RQbb.post(String.format("GA %d", whichRepeal), postInt)));

        lines.set(0, newStartLine);
        lines.set(1, "[color=gray][strike]" + lines.get(1));

        int lastStrikeLine = RexisQuexis.indexStartsWith("[b]Votes Against", lines);
        lines.set(lastStrikeLine, lines.get(lastStrikeLine) + "[/strike][/color]");

        return lines;
    }

    /**
     * Searches the RexisQuexis table of contents and returns the i-th element from that table of contents. This does
     * imply that if you query where <code>i = 1</code>, then you will get the link to the RexisQuexis copy of GA 1.
     * @param i-th GA resolution
     * @return URL to that resolution's copy on RexisQuexis, if it fails,
     * <code>https://forum.nationstates.net/</code>.
     */
    private static URL searchTopics(int i) {

        try {

            String html = new NSConnection("https://forum.nationstates.net/viewtopic.php?f=9&t=30").getResponse();
            Elements elements = Jsoup.parse(html).select("div#p310 div.content a");
            String urlRepeal = elements.get(i - 1).attr("href");

            // LOGGER.info("elements = " + elements);
            // LOGGER.info("urlRepeal = " + urlRepeal);
            if (urlRepeal.startsWith("http:")) urlRepeal = urlRepeal.replaceFirst("http:", "https:");
            if (!urlRepeal.startsWith("https:")) urlRepeal = "https:" + urlRepeal;
            // LOGGER.info("urlRepeal = " + urlRepeal);
            return new URL(urlRepeal);

        } catch (Exception e) {
            try {
                return new URL("https://forum.nationstates.net/");
            } catch (MalformedURLException e1) {
                throw new RuntimeException("This should never happen. Check GAResolution#searchTopics.");
            }
        }

    }

    /**
     * Parses a WA resolution from a copy of that resolution. It then connects to the Internet and tries it best to get
     * information which should be present in the string copy direct from the WA resolutions page.
     * @param input a <code>String</code> which contains the copy of a resolution
     * @return a <code>GAResolution</code> containing that information
     */
    public static GAResolution parse(String input) {

        List<String> lines = new ArrayList<>(Arrays.asList(input.split("\\R"))); // not `\n`

        lines.removeIf(s -> s.trim().equalsIgnoreCase("Repeal this Resolution"));   // remove trailing link
        for (int x = lines.size() - 1; x > -1; x--) {
            // remove trailing empty lines, counting from end to avoid shifting index
            if (lines.get(x).trim().length() != 0) break;
            lines.remove(x);
        }

        GAResolution resolution = new GAResolution();

        // Reference hardcoded lines
        final int R_ID = 0;
        final int LINE_2 = 2;

        resolution.resolutionNum = Integer.parseInt(lines.get(R_ID)
                .substring(lines.get(R_ID).indexOf('#') + 1)
                .trim()
                .replaceAll(",", ""));
        resolution.isRepealed = lines.get(LINE_2).startsWith("Repealed by GA#");    // startsWith that -> repealed

        final int R_TITLE;
        final int R_BYLINE;
        final int R_CATEGORY;
        final int R_STRENGTH;
        final int R_AUTHOR;
        final int R_IMPLEMENTATION;
        final int R_FOR;
        final int R_AGAINST;
        if (resolution.isRepealed) {
            R_TITLE = 4;
            R_BYLINE = 6;
            R_CATEGORY = 8;
            R_STRENGTH = 10;
            R_AUTHOR = 12;
            R_IMPLEMENTATION = lines.size() - 17;

        } else {
            R_TITLE = 2;
            R_BYLINE = 4;
            R_CATEGORY = 6;
            R_STRENGTH = 8;
            R_AUTHOR = 10;
            R_IMPLEMENTATION = lines.size() - 13;
        }
        R_FOR = lines.size() - 9;
        R_AGAINST = lines.size() - 3;

        resolution.title = lines.get(R_TITLE).replace("Repeal:", "Repeal");
        if (resolution.title.toLowerCase().contains("repeal"))
            resolution.type = GAType.REPEAL;

        resolution.title = capitalise(resolution.title);

        resolution.byLine = lines.get(R_BYLINE);
        resolution.category = fromColon(lines.get(R_CATEGORY));
        resolution.strengthLine = translateEffect(fromColon(lines.get(R_STRENGTH)), resolution.category)
                .replaceFirst("GA#", "#");
        resolution.proposer = fromColon(lines.get(R_AUTHOR));

        try {
            resolution.implementation = new SimpleDateFormat("EEE MMM d yyyy").parse(lines.get(R_IMPLEMENTATION));
        } catch (ParseException e) {
            try {
                e.printStackTrace();
                System.err.println("R_IMPLEMENTATION = " + R_IMPLEMENTATION);
                System.err.println("lines.get(R_IMPLEMENTATION) = " + lines.get(R_IMPLEMENTATION));

                String[] elements = lines.get(R_IMPLEMENTATION).split(" "); // assumes form: Thu May 14 2015
                int month = Month.valueOf(elements[1].toUpperCase()).getValue();
                int day = Integer.parseInt(elements[2]);
                int year = Integer.parseInt(elements[3]);
                resolution.implementation = new GregorianCalendar(year, month, day).getTime();
            } catch (IllegalArgumentException e1) {
                throw new RuntimeException("Can't find implementation date or implementation date is invalid; " +
                        "check the paste.");
            }
        }

        resolution.votesFor = Integer.parseInt(lines.get(R_FOR).replaceAll(",", ""));
        resolution.votesAgainst = Integer.parseInt(lines.get(R_AGAINST).replaceAll(",", ""));

        // get text, isRepealed, if repeal { get data }
        try {
            XML xml = queryApi(resolution.resolutionNum);
            resolution.text = cleanResolutionText(
                    xml.xpath("/WA/RESOLUTION/DESC/text()").get(0).replaceFirst("<!\\[CDATA\\[", "")
                            .replace("]]>", "")
            );

            // for some reason, there is no XML tag for this. Check for existence of repealed_by tag to set isRepealed
            try {
                //noinspection ResultOfMethodCallIgnored
                xml.xpath("/WA/RESOLUTION/REPEALED_BY/text()").get(0);
                resolution.isRepealed = true;
            } catch (Exception e) {
                resolution.isRepealed = false;
            }
            LOGGER.info("Resolution " + (resolution.isRepealed ? "was repealed" : "is active"));

            if (resolution.type == GAType.REPEAL) {

                if (!resolution.title.endsWith("\""))
                    resolution.title = (resolution.title + "\"").replaceFirst(" ", " \"");

                resolution.repealData.targetId =
                        Integer.parseInt(xml.xpath("/WA/RESOLUTION/REPEALS_COUNCILID/text()").get(0));
                String name = xml.xpath("/WA/RESOLUTION/NAME/text()").get(0);
                name = name.substring(name.indexOf("\"") + 1, name.lastIndexOf("\""));
                resolution.repealData.targetTitle = name;

                XML rXml = queryApi(resolution.repealData.targetId);
                resolution.repealData.targetCategory = rXml.xpath("/WA/RESOLUTION/CATEGORY/text()").get(0);
                resolution.repealData.targetStrength = rXml.xpath("/WA/RESOLUTION/OPTION/text()").get(0);

                try {
                    String urlString = searchTopics(resolution.repealData.targetId).toString();
                    String postCode = "#p";
                    resolution.repealData.targetPost = Integer.parseInt(
                            urlString.substring(urlString.lastIndexOf(postCode) + postCode.length()));

                } catch (NumberFormatException e) {
                    // catch if it isn't there
                    e.printStackTrace();
                    resolution.repealData.targetPost = -1;
                }

            }

        } catch (IOException e) {

            e.printStackTrace();

            // use anchors to get text, excluding as relevant, repeal description
            List<String> subLines = lines.subList(resolution.type == GAType.REPEAL
                            ? R_AUTHOR + 4
                            : R_AUTHOR + 2,
                    R_IMPLEMENTATION - 3);
            resolution.text = String.join("\n", subLines);

            resolution.isRepealed = false;  // no information -> no presumption of repeal
            // default to the default resolution.repealData

        }

        return resolution;

    }

    /**
     * Cleans resolution text. Replaces some of the code points based on the {@link #CP1252_MAPPER} and then applies
     * normal HTML unescapes from Apache Commons Text.
     * @param s input
     * @return cleaned resolution text
     */
    public static String cleanResolutionText(String s) {
        String[] badTags = {"\\[b\\]", "\\[i\\]", "\\[u\\]", "\\[/b\\]", "\\[/i\\]", "\\[/u\\]"};

        for (String element : badTags)
            s = s.replaceAll(element, "");  // removes all bold, italics, or underline tags

        s = EscapeCP1252.unescape(s);  // unescape text from CP-1252 numeric entity escapes
        return StringEscapeUtils.unescapeHtml4(s);  // unescape text from HTML escapes
    }

    public static String capitalise(String s) {

        final List<String> makeLower = Arrays.asList("for", "and", "nor", "but", "yet", "the", "or", "to", "on", "of");
        final List<String> makeUpper =
                Arrays.asList("ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x", "wa", "gmo", "ga");
//        final List<String> startingIgnores = Arrays.asList("\"", "'");

        final String ACTIONABLE_DELIMITERS = " -/\""; // if following, capitalise

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;
        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf(c) >= 0); // explicit cast not needed
        }

        String[] split = sb.toString().trim().split("\\s"); // split on spaces
        List<String> elements = new ArrayList<>();

        for (int i = 0; i < split.length; i++) {
            String e = split[i].trim();
            if (containsRequirements(makeLower, e) & i != 0) elements.add(e.toLowerCase());
            else if (containsRequirements(makeUpper, e)) elements.add(e.toUpperCase());
            else elements.add(e);
        }

        String out = String.join(" ", elements);
        if (out.matches("((ga)|(GA)|(Ga))#\\d+")) out = out.toUpperCase(); // post hoc check

        return out;
    }

    /**
     * String contains, but ignore case. Also ignore all quotes.
     * @param hay    to look in
     * @param needle to find, with exceptions
     * @return true if contains after applying checks
     */
    private static boolean containsRequirements(List<String> hay, String needle) {
        String n = needle.replaceAll("[\"']", "").toLowerCase();
        return hay.contains(n);
    }

    public static XML queryApi(int rNum) throws IOException {
        String url = String.format("https://www.nationstates.net/cgi-bin/api.cgi?wa=%d&id=%d&q=resolution", 1, rNum);
        NSConnection connection = new NSConnection(url);
        System.err.printf("Querying API for GA %d%n", rNum);
        System.err.printf("URL is %s%n", url);
        return new XMLDocument(connection.getResponse());
    }

    /**
     * Translates the strength line into the applicable effect. There is a bug in the NationStates API where certain
     * effects are stored as '0' rather than the actual effects, which is specific by category. This translates them
     * from 0 to the applicable effect.
     * @param strength to translate, if applicable
     * @param category to determine the correct effect
     * @return the real strength or effect of the resolution
     */
    private static String translateEffect(String strength, String category) {

        if (!strength.equals("0")) return GAResolution.capitalise(strength); // if strength != 0, skip, but capitalise

        if (category.equalsIgnoreCase("Environmental")) return "Automotive";
        if (category.equalsIgnoreCase("Health")) return "Healthcare";
        if (category.equalsIgnoreCase("Education and Creativity")) return "Artistic";
        if (category.equalsIgnoreCase("Gun Control")) return "Tighten";

        return "Mild";  // apparently, 0 -> Mild

    }

    /**
     * Returns the proper kind of effect. Ex: for the category Bookkeeping, the proper effect line is 'Effect', not
     * strength; Gun Control, it is 'Decision'; for Environmental, it is 'Industry Affected'; and for Repeal, it is
     * 'Resolution'.
     * @param strength to weed out normal resolutions (means no list of normal resolutions)
     * @param category to deal with other cases
     * @return the proper effect line (such as 'Area of Effect' or 'Resolution'
     */
    private static String createEffectLine(String strength, String category) {
        if (Stream.of("Mild", "Significant", "Strong").anyMatch(s -> s.equalsIgnoreCase(strength)))
            return "Strength";  // most resolutions
        if (category.equalsIgnoreCase("Repeal") || strength.matches("#\\d+") || strength.matches("GA#\\d+"))
            return "Resolution"; // repeals

        // edge cases
        if (category.equalsIgnoreCase("Bookkeeping")) return "Effect";
        if (category.equalsIgnoreCase("Environmental")) return "Industry Affected";
        if (category.equalsIgnoreCase("Gun Control")) return "Decision";
        if (category.equalsIgnoreCase("Recreational Drug Use")) return "Decision";

        return "Area of Effect";    // not mild, significant, strong, or other edge cases -> generic effect
    }

    private static String fromColon(String input) {
        return input.substring(input.indexOf(':') + 1).trim();
    }

}