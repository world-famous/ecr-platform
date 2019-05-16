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
package com.tianwen.springcloud.ecrapi.util.captcha.renderer;

import java.util.ArrayList;

public class TextString {

	private ArrayList<TextCharacter> characters = new ArrayList<TextCharacter>();
	
	public void clear() {
		characters.clear();
	}
	
	public void addCharacter(TextCharacter tc) {
		characters.add(tc);
	}
	
	public ArrayList<TextCharacter> getCharacters() {
		return characters;
	}
	
	public double getWidth() {
		double minx = 0;
		double maxx = 0;
		boolean first = true;
		for (TextCharacter tc : characters) {
			if (first) {
				minx = tc.getX();
				maxx = tc.getX() + tc.getWidth();
				first = false;
			} else {
				if (minx > tc.getX()) {
					minx = tc.getX(); 
				}
				if (maxx < tc.getX() + tc.getWidth()) {
					maxx = tc.getX() + tc.getWidth();
				}
			}
			
		}
		return maxx - minx;
	}
	
	public double getHeight() {
		double miny = 0;
		double maxy = 0;
		boolean first = true;
		for (TextCharacter tc : characters) {
			if (first) {
				miny = tc.getY();
				maxy = tc.getY() + tc.getHeight();
				first = false;
			} else {
				if (miny > tc.getY()) {
					miny = tc.getY(); 
				}
				if (maxy < tc.getY() + tc.getHeight()) {
					maxy = tc.getY() + tc.getHeight();
				}
			}
			
		}
		return maxy - miny;
	}

}
