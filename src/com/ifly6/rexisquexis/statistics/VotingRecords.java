/*
 * Copyright (c) 2017 Kevin Wong
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

package com.ifly6.rexisquexis.statistics;

import com.git.ifly6.nsapi.NSConnection;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ifly6 on 2017-11-05.
 */
public class VotingRecords {

	public static void main(String[] args) throws IOException {

		int resolutions = 412;
		List<VotingRecord> recordList = new ArrayList<>();

		for (int resolutionNum = 410; resolutionNum <= resolutions; resolutionNum++) {

			System.err.println("Got data for " + resolutionNum + " of " + resolutions);

			XML xml = queryApi(resolutionNum);
			String date = xml.xpath("/WA/RESOLUTION/IMPLEMENTED/text()").get(0);
			date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(date) * 1000));

			int votesFor = Integer.parseInt(xml.xpath("/WA/RESOLUTION/TOTAL_VOTES_FOR/text()").get(0));
			int votesAgainst = Integer.parseInt(xml.xpath("/WA/RESOLUTION/TOTAL_VOTES_AGAINST/text()").get(0));

			VotingRecord vr = new VotingRecord(resolutionNum, date, votesFor, votesAgainst);
			recordList.add(vr);

		}

		recordList.forEach(System.out::println);

	}

	private static XMLDocument queryApi(int rNum) throws IOException {
		NSConnection connection = new NSConnection(
				String.format("https://www.nationstates.net/cgi-bin/api.cgi?wa=%d&id=%d&q=resolution", 1, rNum));
		System.err.printf("Querying API for %d GA%n", rNum);
		return new XMLDocument(connection.getResponse());
	}

}

class VotingRecord {

	private int resolutionNum;

	private String date;
	private int votesFor;
	private int votesAgainst;

	VotingRecord(int resolutionNum, String date, int votesFor, int votesAgainst) {
		this.resolutionNum = resolutionNum;
		this.date = date;
		this.votesFor = votesFor;
		this.votesAgainst = votesAgainst;
	}

	@Override
	public String toString() {
		return date + "\t" + votesFor + "\t" + votesAgainst;
	}

	public int getResolutionNum() {
		return resolutionNum;
	}

	public void setResolutionNum(int resolutionNum) {
		this.resolutionNum = resolutionNum;
	}
}
