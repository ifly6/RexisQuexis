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
package com.ifly6.rexisquexis.author;

/**
 * @author ifly6
 *
 */
public class RqaResolutionData {

	private String resolutionName;
	private int num;
	private String category;
	private String strength;
	private boolean repealed;

	private String author;

	private int postNum;

	public RqaResolutionData(String resolutionName, int resolutionNum, String category, String strength, int postNum,
			boolean repealed, String author) {
		this.resolutionName = resolutionName;
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

	public String author() {
		return author;
	}
}
