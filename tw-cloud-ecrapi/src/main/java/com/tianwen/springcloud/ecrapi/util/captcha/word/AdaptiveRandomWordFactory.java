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
package com.tianwen.springcloud.ecrapi.util.captcha.word;

import java.util.Random;

public class AdaptiveRandomWordFactory extends RandomWordFactory {

	protected String wideCharacters;

	public void setWideCharacters(String wideCharacters) {
		this.wideCharacters = wideCharacters;
	}

	public AdaptiveRandomWordFactory() {
		characters = "abcdefghijkmnpqrstuwxyz23456789";
		wideCharacters = "mw";
	}
	
	@Override
	public String getNextWord() {
		Random rnd = new Random();
		StringBuffer sb = new StringBuffer();
		StringBuffer chars = new StringBuffer(characters);
		int l = minLength + (maxLength > minLength ? rnd.nextInt(maxLength - minLength) : 0);
		for (int i = 0; i < l; i++) {
			int j = rnd.nextInt(chars.length());
			char c = chars.charAt(j);
			if (wideCharacters.indexOf(c) != -1) {
				for (int k = 0; k < wideCharacters.length(); k++) {
					int idx = chars.indexOf(String.valueOf(wideCharacters.charAt(k)));
					if (idx != -1) {
						chars.deleteCharAt(idx);
					}
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

}
