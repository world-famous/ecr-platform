/*
 * Copyright 2018 Jim Liu.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * By the way, this Soft can only by education, can not be used in commercial products. All right be reserved.
 */
package com.tianwen.springcloud.ecrapi.util.captcha.font;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class RandomFontFactory implements FontFactory {

	protected List<String> families;
	protected int minSize;
	protected int maxSize;
	protected boolean randomStyle;

	public RandomFontFactory() {
		families = new ArrayList<String>();
		families.add("Verdana");
		families.add("Tahoma");
		minSize = 45;
		maxSize = 45;
	}

	public RandomFontFactory(List<String> families) {
		this();
		this.families = families;
	}

	public RandomFontFactory(String[] families) {
		this();
		this.families = Arrays.asList(families);
	}
	
	public RandomFontFactory(int size, List<String> families) {
		this(families);
		minSize = maxSize = size;
	}

	public RandomFontFactory(int size, String[] families) {
		this(families);
		minSize = maxSize = size;
	}
	
	public void setFamilies(List<String> families) {
		this.families = families;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public void setRandomStyle(boolean randomStyle) {
		this.randomStyle = randomStyle;
	}

	@Override
	public Font getFont(int index) {
		Random r = new Random();
		String family = families.get(r.nextInt(families.size()));
		boolean bold = r.nextBoolean() && randomStyle;
		int size = minSize;
		if (maxSize - minSize > 0) {
			size += r.nextInt(maxSize - minSize);
		}
		return new Font(family, bold ? Font.BOLD : Font.PLAIN, size);
	}

}
