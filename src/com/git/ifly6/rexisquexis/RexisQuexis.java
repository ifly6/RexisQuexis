package com.git.ifly6.rexisquexis;

import com.git.ifly6.nsapi.NSConnection;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by ifly6 on 11/3/2017.
 */
public class RexisQuexis {
	private JPanel panel;
	private JTextArea textArea;
	private JTextField textField;
	private JButton button;


	public RexisQuexis() {
		button.addActionListener(e -> {
			try {
				WAResolution resolution = loadResolution(Integer.parseInt(textField.getText()));


			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	private WAResolution loadResolution(int i) throws IOException {
		WAResolution resolution = new WAResolution();
		String queryString = "https://www.nationstates.net/cgi-bin/api.cgi?wa=1&id=$ID&q=resolution";
		String string = queryString.replace("$ID", Integer.toString(i));

		NSConnection connection = new NSConnection(string);
		XML xml = new XMLDocument(connection.getResponse());

		resolution.title = xml.xpath("/WA/RESOLUTION/NAME/text()").get(0);

		return resolution;
	}
}
