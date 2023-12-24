/*
 * Copyright (c) 2023 ifly6
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

package com.ifly6.rexisquexis.manualtests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class TestDateParser {
    public static void main(String[] args) {
        List<String> list = List.of(
                "Mon Sep 7 2015",
                "Mon Sept 7 2015",
                "Sun Dec 24 2023"
        );
        for (String i : list)
            try {
                new SimpleDateFormat("EEE MMM d yyyy").parse(i);
            } catch (ParseException e) {
                System.out.println("error on: " + i);
                e.printStackTrace();
            }
    }
}
