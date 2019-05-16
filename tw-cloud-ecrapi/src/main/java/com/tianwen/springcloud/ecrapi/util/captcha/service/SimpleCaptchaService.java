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
package com.tianwen.springcloud.ecrapi.util.captcha.service;

import com.tianwen.springcloud.ecrapi.util.captcha.background.SingleColorBackgroundFactory;
import com.tianwen.springcloud.ecrapi.util.captcha.color.SingleColorFactory;
import com.tianwen.springcloud.ecrapi.util.captcha.filter.FilterFactory;
import com.tianwen.springcloud.ecrapi.util.captcha.font.RandomFontFactory;
import com.tianwen.springcloud.ecrapi.util.captcha.renderer.BestFitTextRenderer;
import com.tianwen.springcloud.ecrapi.util.captcha.word.AdaptiveRandomWordFactory;

import java.awt.*;

public class SimpleCaptchaService extends AbstractCaptchaService {

	public SimpleCaptchaService(int width, int height, Color textColor, Color backgroundColor, int fontSize, FilterFactory ff) {
		backgroundFactory = new SingleColorBackgroundFactory(backgroundColor);
		wordFactory = new AdaptiveRandomWordFactory();
		fontFactory = new RandomFontFactory();
		textRenderer = new BestFitTextRenderer();
		colorFactory = new SingleColorFactory(textColor);
		filterFactory = ff;
		this.width = width;
		this.height = height;
	}
	
	public SimpleCaptchaService(int width, int height, Color textColor, Color backgroundColor, int fontSize, String[]fontNames, FilterFactory ff) {
		backgroundFactory = new SingleColorBackgroundFactory(backgroundColor);
		wordFactory = new AdaptiveRandomWordFactory();
		fontFactory = new RandomFontFactory(fontNames);
		textRenderer = new BestFitTextRenderer();
		colorFactory = new SingleColorFactory(textColor);
		filterFactory = ff;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public Captcha getCaptcha() {
		return null;
	}

}
