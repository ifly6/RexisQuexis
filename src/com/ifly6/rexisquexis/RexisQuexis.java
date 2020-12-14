package com.ifly6.rexisquexis;

import com.git.ifly6.nsapi.NSConnection;
import com.ifly6.rexisquexis.io.RqForumUtilities;
import com.jcabi.xml.XMLDocument;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.undo.UndoManager;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RexisQuexis {

    private static final Logger LOGGER = Logger.getLogger(RexisQuexis.class.getName());

    private RexisQuexis() {

        JFrame frame = new JFrame("WA resolutions formatter");
        frame.setSize(700, 800);
        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        panel.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        UndoManager undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton parseButton = new JButton("Parse");
        parseButton.addActionListener(e -> {
            LOGGER.info("Attempting to parse resolution");
            String url = JOptionPane.showInputDialog(frame, "Enter the URL for the debate topic. Auto-cleans.",
                    "Parameter Input", JOptionPane.PLAIN_MESSAGE);

            GAResolution gaResolution = GAResolution.parse(textArea.getText());
            gaResolution.topicUrl = RqForumUtilities.cleanForumURL(url);
            LOGGER.info(String.format("Parsed for GA resolution %d", gaResolution.resolutionNum));

            textArea.setText(gaResolution.format());
            LOGGER.info("Resolution parse done");
        });
        buttonPanel.add(parseButton);

        JButton repealButton = new JButton("Repeal Format (use bbCode form)");
        repealButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> textLines = Arrays.asList(textArea.getText().split("\n"));
                textArea.setText(repealFormat(textLines));
                LOGGER.info("Repeal format done");
            }

            private String repealFormat(List<String> textLines) {
                int whichRepeal;
                try {
                    // extract resolution number from the pasted information, use API to get repealed resolution number
                    Matcher m = Pattern.compile("(?<=GA\\s)\\d+").matcher(textLines.get(textLines.size() - 1));
                    if (m.find()) {
                        int resNum = Integer.parseInt(m.group());
                        String xmlRaw = new NSConnection("https://www.nationstates.net/cgi-bin/api.cgi?wa=1&id="
                                + resNum + "&q=resolution").getResponse();
                        whichRepeal = Integer.parseInt(
                                new XMLDocument(xmlRaw).xpath("/WA/RESOLUTION/REPEALED_BY/text()").get(0));

                    } else throw new RuntimeException("Couldn't find repealing resolution in API");

                } catch (Exception e) {
                    e.printStackTrace();
                    whichRepeal = Integer.parseInt(JOptionPane.showInputDialog(frame,
                            "Repealing resolution not in API. Enter repealing resolution number (eg 326).",
                            "Parameter input", JOptionPane.PLAIN_MESSAGE));
                }

                String urlRepeal;
                try {
                    urlRepeal = GAResolution.searchTopics(whichRepeal).toString();

                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                    urlRepeal = JOptionPane.showInputDialog(frame,
                            "Cannot find repealing resolution in forum database, "
                                    + "manually provide the RexisQuexis url of the repealed resolution",
                            "Parameter input", JOptionPane.PLAIN_MESSAGE);

                } catch (RuntimeException e) {
                    e.printStackTrace();
                    urlRepeal = JOptionPane.showInputDialog(frame,
                            "Repealing resolution forum search suffered unclear fatal error. Check log.",
                            "Parameter input", JOptionPane.PLAIN_MESSAGE);
                }

                String newStartLine = RQbb.color(RQbb.strike(textLines.get(0)), "gray") +
                        " " +
                        RQbb.bold(String.format("[Struck out by %s]",
                                RQbb.post(String.format("GA %d", whichRepeal), postInt(urlRepeal))));

                textLines.set(0, newStartLine);
                textLines.set(1, "[color=gray][strike]" + textLines.get(1));

                int lastStrikeLine = indexStartsWith("[b]Votes Against", textLines);
                textLines.set(lastStrikeLine, textLines.get(lastStrikeLine) + "[/strike][/color]");

                return String.join("\n", textLines);
            }
        });
        buttonPanel.add(repealButton);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        undoItem.addActionListener(e -> {
            if (undoManager.canUndo()) undoManager.undo();
        });
        editMenu.add(undoItem);

        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK));
        redoItem.addActionListener(e -> {
            if (undoManager.canRedo()) undoManager.redo();
        });
        editMenu.add(redoItem);

        JMenu viewMenu = new JMenu("View");
        menuBar.add(viewMenu);

        JCheckBoxMenuItem wordWrap = new JCheckBoxMenuItem("Word wrap");
        wordWrap.addActionListener(i -> {
            textArea.setWrapStyleWord(wordWrap.getState());
            textArea.setLineWrap(wordWrap.getState());
        });
        wordWrap.setState(true);
        viewMenu.add(wordWrap);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // ** Helpful parsing and search methods **
    static int postInt(String postUrl) {
        return Integer.parseInt(postUrl.substring(postUrl.indexOf("#p") + "#p".length()));
    }

    static int indexStartsWith(String term, List<String> list) {
        for (int x = 0; x < list.size(); x++)
            if (list.get(x).startsWith(term)) return x;
        return -1;
    }

    // ** main **
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        SwingUtilities.invokeLater(RexisQuexis::new);
    }
}