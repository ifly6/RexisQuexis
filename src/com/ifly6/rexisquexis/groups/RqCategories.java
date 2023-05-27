package com.ifly6.rexisquexis.groups;

import com.git.ifly6.nsapi.NSConnection;
import com.ifly6.rexisquexis.io.RqCacher;
import com.ifly6.rexisquexis.io.RqForumUtilities;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Creates GUI {@code JTextArea} and buttons. Button to parse active resolutions by category group. Invokes
 * {@link RqCategoryPrinter} to format as phpBB bbCode and then display for copy-paste to forum.
 * @author ifly6
 */
public class RqCategories {

    private static final Logger LOGGER = Logger.getLogger(RqCategories.class.getName());
    private static RqCategories instance = null;

    private JPanel panel;
    private JTextArea textArea;
    private JProgressBar progressBar;
    private JButton parseButton;
    private JButton cacheClear;

    private List<RqResolutionData> resolutions;
    private static final Path rqcCacheLocation = Paths.get("rqc_cache.json");

    public static RqCategories getInstance() {
        if (instance == null) instance = new RqCategories().completeInitialisation();
        return instance;
    }

    private RqCategories() {
        parseButton.addActionListener(event -> {
            Thread queryThread = new Thread(() -> { // has a runnable inside already
                try {
                    if (Objects.isNull(resolutions))
                        resolutions = parseResolutions();

                    RqCategoryPrinter printer = new RqCategoryPrinter(resolutions);
                    String newText = StringEscapeUtils.unescapeHtml4(printer.print());
                    textArea.setText(newText);
                    // parser unescapes html chars for printing

                } catch (IOException e) {
                    e.printStackTrace();
                    textArea.setText(e.toString());
                }
            });
            queryThread.start();
        });
        cacheClear.addActionListener(e -> {
            if (Files.exists(rqcCacheLocation)) {
                int result = JOptionPane.showConfirmDialog(cacheClear,
                        "Are you sure you want to delete the cache?",
                        "RexisQuexis",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.YES_OPTION)
                    try {
                        Files.deleteIfExists(rqcCacheLocation);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        textArea.setText("Could not delete cache. Shrug.");
                    }
            } else {
                JOptionPane.showMessageDialog(cacheClear, "Cache does not exist.",
                        "RexisQuexis",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
    }

    /**
     * Hacky way to execute initialisation code after creation; always works because singleton initialisation allows
     * explicit initalisation path
     * @return self
     */
    private RqCategories completeInitialisation() {
        progressBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        return this;
    }

    /**
     * Scrapes and parses the data from the RexisQuexis table of contents.
     * @return {@code List<RqResolutionData>} containing all relevant resolutions.
     * @throws IOException if error in getting data from Internet
     */
    private List<RqResolutionData> parseResolutions() throws IOException {

        List<RqResolutionData> resList = new ArrayList<>();
        Elements elements = Jsoup.parse(new URL("https://forum.nationstates.net/viewtopic.php?f=9&t=30"), 2000)
                .select("div#p310 div.content a");
        int numOfResolutions = elements.size();

        System.out.printf("For %d elements, this will take a max of %s%n",
                numOfResolutions,
                time((int) Math.round(
                        (double) NSConnection.WAIT_TIME * numOfResolutions / 1000)
                )
        );

        AtomicInteger counter = new AtomicInteger(1);
        for (Element element : elements) {
            // Get some basic information
            String postLink = element.attr("href");
            int postNum;
            try {
                postNum = Integer.parseInt(postLink.substring(postLink.indexOf("#p") + 2));
            } catch (StringIndexOutOfBoundsException e) {
                throw new RuntimeException(String.format("attempting to substring for #p in '%s'", postLink), e);
            }

            RqCacher cacher = new RqCacher(rqcCacheLocation);
            String resolutionURL = String.format(
                    "https://www.nationstates.net/cgi-bin/api.cgi?wa=1&id=%d&q=resolution",
                    counter.get()
            );
            if (!cacher.contains(resolutionURL)) {
                // Query the API
                NSConnection connection = new NSConnection(resolutionURL);
                LOGGER.info("Queried for resolution " + counter.get() + " of " + numOfResolutions);
                cacher.update(resolutionURL, connection.getResponse());
                cacher.save();
            }

            // Update GUI
            SwingUtilities.invokeLater(() -> {
                progressBar.setMaximum(numOfResolutions);
                progressBar.setValue(counter.get());
            });

            // Parse the API response
            XML xml = new XMLDocument(cacher.get(resolutionURL));
            final String name = RqForumUtilities.capitalise(xml.xpath("/WA/RESOLUTION/NAME/text()").get(0));
            final String category = xml.xpath("/WA/RESOLUTION/CATEGORY/text()").get(0);

            // determine strength
            final String strength;
            String rawStrength = xml.xpath("/WA/RESOLUTION/OPTION/text()").get(0);
            if (rawStrength.equals("0")) {
                if (category.equalsIgnoreCase("Environmental")) strength = "Automotive";
                else if (category.equalsIgnoreCase("Health")) strength = "Healthcare";
                else if (category.equalsIgnoreCase("Education and Creativity")) strength = "Artistic";
                else if (category.equalsIgnoreCase("Gun Control")) strength = "Tighten";
                else if (category.equalsIgnoreCase("Regulation")) strength = "Consumer Protection";
                else strength = "Mild";
            } else strength = rawStrength;

            boolean repealed = true;
            try {
                //noinspection ResultOfMethodCallIgnored
                xml.xpath("/WA/RESOLUTION/REPEALED/text()").get(0);
            } catch (RuntimeException e) {
                repealed = false;
            }

            // PROPOSED_BY
            final String proposedBy = xml.xpath("/WA/RESOLUTION/PROPOSED_BY/text()").get(0);

            // Make the resolution, tell us
            resList.add(new RqResolutionData(name, counter.get(), category, strength, postNum, repealed, proposedBy));
            System.out.printf("Parsed resolution GA %d <%s>%n", counter.get(), name);

            counter.getAndIncrement(); // increment for next
        }

        RqResolutionData.makeRepealsConsistent(resList);
        return resList;
    }

    private static String time(int seconds) {
        Duration d = Duration.of(seconds, ChronoUnit.SECONDS);
        return String.format("%dd:%dh:%dm:%ds", d.toDaysPart(), d.toHoursPart(), d.toMinutesPart(),
                d.toSecondsPart());
    }

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        SwingUtilities.invokeLater(() -> {
            // must wrap in JFrame for IntelliJ to work it out
            JFrame frame = new JFrame("RexisQuexis Categories");
            frame.setContentPane(RqCategories.getInstance().panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Dimension defaultDimension = new Dimension(500, 500); // sizing
            frame.setPreferredSize(defaultDimension);
            frame.setSize(defaultDimension);
            frame.setLocationRelativeTo(null); // centre

            frame.pack();
            frame.setVisible(true);
        });
    }
}
