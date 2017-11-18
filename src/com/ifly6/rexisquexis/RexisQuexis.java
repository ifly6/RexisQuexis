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

package com.ifly6.rexisquexis;

import com.git.ifly6.nsapi.NSConnection;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;

import javax.swing.*;
import java.io.IOException;

public class RexisQuexis {

	private JPanel panel;
	private JTextArea textArea;
	private JTextField textField;
	private JButton button;

	public RexisQuexis() {
		button.addActionListener(e -> {
			try {
				GAResolution resolution = loadResolution(Integer.parseInt(textField.getText()));


			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	private GAResolution loadResolution(int i) throws IOException {
		GAResolution resolution = new GAResolution();
		String queryString = "https://www.nationstates.net/cgi-bin/api.cgi?wa=1&id=$ID&q=resolution";
		String string = queryString.replace("$ID", Integer.toString(i));

		NSConnection connection = new NSConnection(string);
		XML xml = new XMLDocument(connection.getResponse());

		resolution.title = xml.xpath("/WA/RESOLUTION/NAME/text()").get(0);

		return resolution;
	}
}
