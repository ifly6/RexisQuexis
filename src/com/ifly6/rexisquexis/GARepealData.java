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

package com.ifly6.rexisquexis;

/**
 * Holds information necessary for the repeal.
 * @since 2017-03-14
 */
class GARepealData {

//	Map<String, String> defaults = new HashMap<>();
//		defaults.put("targetId", "-1");
//		defaults.put("targetTitle", "$rtit");
//		defaults.put("targetCategory", "$rcat");
//		defaults.put("targetStrength", "$rstr");
//		defaults.put("targetPost", "-1");

	int targetId = -1;
	String targetTitle = "$rtit";
	String targetCategory = "$rcat";
	String targetStrength = "$rstr";
	int targetPost = -1;

}
