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

import com.tianwen.springcloud.ecrapi.util.captcha.background.BackgroundFactory;
import com.tianwen.springcloud.ecrapi.util.captcha.color.ColorFactory;
import com.tianwen.springcloud.ecrapi.util.captcha.filter.FilterFactory;
import com.tianwen.springcloud.ecrapi.util.captcha.font.FontFactory;
import com.tianwen.springcloud.ecrapi.util.captcha.renderer.TextRenderer;
import com.tianwen.springcloud.ecrapi.util.captcha.word.WordFactory;

import java.awt.image.BufferedImage;

public abstract class AbstractCaptchaService implements CaptchaService {

	protected FontFactory fontFactory;
	protected WordFactory wordFactory;
	protected ColorFactory colorFactory;
	protected BackgroundFactory backgroundFactory;
	protected TextRenderer textRenderer;
	protected FilterFactory filterFactory;
	protected int width;
	protected int height;

	public void setFontFactory(FontFactory fontFactory) {
		this.fontFactory = fontFactory;
	}

	public void setWordFactory(WordFactory wordFactory) {
		this.wordFactory = wordFactory;
	}

	public void setColorFactory(ColorFactory colorFactory) {
		this.colorFactory = colorFactory;
	}

	public void setBackgroundFactory(BackgroundFactory backgroundFactory) {
		this.backgroundFactory = backgroundFactory;
	}

	public void setTextRenderer(TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
	}

	public void setFilterFactory(FilterFactory filterFactory) {
		this.filterFactory = filterFactory;
	}

	public FontFactory getFontFactory() {
		return fontFactory;
	}

	public WordFactory getWordFactory() {
		return wordFactory;
	}

	public ColorFactory getColorFactory() {
		return colorFactory;
	}

	public BackgroundFactory getBackgroundFactory() {
		return backgroundFactory;
	}

	public TextRenderer getTextRenderer() {
		return textRenderer;
	}

	public FilterFactory getFilterFactory() {
		return filterFactory;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public Captcha getCaptcha() {
		BufferedImage bufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		backgroundFactory.fillBackground(bufImage);
		String word = wordFactory.getNextWord();
		textRenderer.draw(word, bufImage, fontFactory, colorFactory);
		bufImage = filterFactory.applyFilters(bufImage);
		return new Captcha(word, bufImage);
	}

}
