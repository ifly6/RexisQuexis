/* Copyright (c) 2016 ifly6
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
package com.git.ifly6.rexisquexis;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

// This class, with public vars, is totally valid. Look:
// http://www.oracle.com/technetwork/java/javase/documentation/codeconventions-137265.html#177
public class WAResolution {
	
	public final String[] areaOfEffectTypes = {};
	
	public enum ResolutionType {
		NORMAL, REPEAL
	}
	
	public ResolutionType resolutionType = ResolutionType.NORMAL;
	
	// Information
	public String category = "Bookkeeping";
	public String strengthLine = "Sweeping";
	public String proposer = "Maxtopia";
	
	public int resolutionNum = 1;
	public String title = "The World Assembly";
	public String byLine = "A resolution to establish administrative parameters.";
	
	public String text = "The Adopted Nations of the World Assembly,\n"
			+ "NOTING that the United Nations has spectacularly imploded in a colossal fireball of extra-dimensional inanity;\n"
			+ "BELIEVING that there is a vital and needed role for a global organizational body;\n"
			+ "FURTHER NOTING that there are an awful lot of United Nations Resolutions;\n"
			+ "FURTHER BELIEVING that the demise of the United Nations may be fashioned into a grand opportunity for nations to draw a new destiny;\n"
			+ "HEREBY\n"
			+ "1. ESTABLISHES the World Assembly as the natural successor to the United Nations, with the full transfer of all Delegate ranks and associated endorsements;\n"
			+ "2. ARCHIVES all previously passed UN Resolutions for historical purposes, so that citizens of today may forever look back upon the masterwork of their ancestors;\n"
			+ "3. DECLARES the pages of international law to be blank;\n"
			+ "4. INVITES members of the World Assembly to begin work on a new volume, which may in time exceed even the grandeur of its predecessor.\n";
	
	public int votesFor = 11609;
	public int votesAgainst = 2259;
	
	public Date implementation = new GregorianCalendar(2008, 4, 6).getTime();

	@Override public String toString() {

		List<String> lines = new ArrayList<>();
		lines.add(RQbb.bold(title));
		lines.add(RQbb.italicise(byLine));
		lines.add("");
		lines.add(RQbb.bold("Category:") + " " + category);
		
		return lines.stream().collect(Collectors.joining("\n"));

	}
	
}