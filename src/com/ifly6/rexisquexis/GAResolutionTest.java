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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ifly6 on 13/3/2017.
 */
public class GAResolutionTest {

	public static void main(String[] args) {

//		print(new GAResolution().format());

		try {
			parse(Paths.get(System.getProperty("user.dir"), "test-resolution.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void print(String format) {
		System.out.println(format);
	}

	private static void parse(Path path) throws IOException {
		List<String> lines = Files.readAllLines(path);
		GAResolution resolution = GAResolution.parse(lines.stream().collect(Collectors.joining("\n")));
		System.out.println(resolution.format());
	}

}
