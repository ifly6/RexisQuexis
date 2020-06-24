/* Copyright (c) 2017 Kevin Wong
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

package com.ifly6.rexisquexis;

import com.git.ifly6.nsapi.NSConnection;
import com.jcabi.xml.XMLDocument;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RexisQuexis {

    private static final Logger LOGGER = Logger.getLogger(RexisQuexis.class.getName());

    private RexisQuexis() {

        JFrame frame = new JFrame("WA Resolutions Formatter");
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
            String url = JOptionPane.showInputDialog(frame, "Enter the URL for the debate topic.",
                    "Parameter Input", JOptionPane.PLAIN_MESSAGE)
                    .replace("&hilit=[^#]+", "")
                    .replace("&sid=[^#]", "");

            LOGGER.info("Attempting to parse resolution");
            GAResolution gaResolution = GAResolution.parse(textArea.getText());
            LOGGER.info(String.format("Parsed for GA resolution %d", gaResolution.resolutionNum));

            // parse the annoying af highlight thing out
            // https://forum.nationstates.net/viewtopic.php?f=9&t=485969&p=37173138&hilit=desalination#p37173138
            url = url.replace("&hilit=.+(?=#)", "");

            gaResolution.topicUrl = url;
            textArea.setText(gaResolution.format());
        });
        buttonPanel.add(parseButton);

        JButton repealButton = new JButton("Repeal Format (use bbCode form)");
        repealButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> textLines = Arrays.asList(textArea.getText().split("\n"));
                textArea.setText(repealFormat(textLines));
            }

            private String repealFormat(List<String> textLines) {
                int whichRepeal;
                try {
                    // extract resolution number, use API to get repealed resolution number
                    Scanner scanner = new Scanner(textLines.get(textLines.size() - 1));
                    int resNum = scanner.nextInt();
                    scanner.close();

                    String xmlRaw = new NSConnection("https://www.nationstates.net/cgi-bin/api.cgi?wa=1&id="
                            + resNum + "&q=resolution").getResponse();
                    whichRepeal = Integer.parseInt(new XMLDocument(xmlRaw).xpath("/WA/RESOLUTION/REPEALED_BY/text()")
                            .get(0));

                } catch (Exception e) {
                    e.printStackTrace();
                    whichRepeal = Integer.parseInt(JOptionPane.showInputDialog(frame,
                            "Cannot find repealed resolution from API, enter the number of the resolution that " +
                                    "repealed this resolution (e.g. 326)",
                            "Parameter input", JOptionPane.PLAIN_MESSAGE));
                }

                String urlRepeal;
                try {
                    String html = new NSConnection("http://forum.nationstates.net/viewtopic.php?f=9&t=30")
                            .getResponse();
                    Elements elements = Jsoup.parse(html).select("div#p310 div.content a");
                    urlRepeal = elements.get(whichRepeal + 1).attr("abs:href"); // adjust for 0 -> 1 index

                } catch (IOException | RuntimeException e) {
                    e.printStackTrace();
                    urlRepeal = JOptionPane.showInputDialog(frame,
                            "Cannot find repealing resolution in database, manually provide the RexisQuexis url of the " +
                                    "repealed resolution",
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

                return textLines.stream().collect(Collectors.joining("\n"));

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
    private int postInt(String postUrl) {
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