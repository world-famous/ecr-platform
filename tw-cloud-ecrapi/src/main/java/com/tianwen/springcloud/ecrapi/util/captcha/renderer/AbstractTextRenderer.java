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

import com.tianwen.springcloud.ecrapi.util.captcha.color.ColorFactory;
import com.tianwen.springcloud.ecrapi.util.captcha.font.FontFactory;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class AbstractTextRenderer implements TextRenderer {

	protected int leftMargin;
	protected int rightMargin;
	protected int topMargin;
	protected int bottomMargin;

	protected abstract void arrangeCharacters(int width, int height, TextString ts);

	public AbstractTextRenderer() {
		leftMargin = rightMargin = 5;
		topMargin = bottomMargin = 5;
	}

	@Override
	public void setLeftMargin(int leftMargin) {
		this.leftMargin = leftMargin;
	}

	@Override
	public void setRightMargin(int rightMargin) {
		this.rightMargin = rightMargin;
	}

	@Override
	public void setTopMargin(int topMargin) {
		this.topMargin = topMargin;
	}

	@Override
	public void setBottomMargin(int bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	@Override
	public void draw(String text, BufferedImage canvas, FontFactory fontFactory, ColorFactory colorFactory) {
		Graphics2D g = (Graphics2D) canvas.getGraphics();
		TextString ts = convertToCharacters(text, g, fontFactory, colorFactory);
		arrangeCharacters(canvas.getWidth(), canvas.getHeight(), ts);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		for (TextCharacter tc : ts.getCharacters()) {
			g.setColor(tc.getColor());
			g.drawString(tc.iterator(), (float) tc.getX(), (float) tc.getY());
		}
	}

	protected TextString convertToCharacters(String text, Graphics2D g, FontFactory fontFactory, ColorFactory colorFactory) {
		TextString characters = new TextString();
		FontRenderContext frc = g.getFontRenderContext();
		double lastx = 0;
		for (int i = 0; i < text.length(); i++) {
			Font font = fontFactory.getFont(i);
			char c = text.charAt(i);
			FontMetrics fm = g.getFontMetrics(font);
			Rectangle2D bounds = font.getStringBounds(String.valueOf(c), frc);
			TextCharacter tc = new TextCharacter();
			tc.setCharacter(c);
			tc.setFont(font);
			tc.setWidth(fm.charWidth(c));
			tc.setHeight(fm.getAscent() + fm.getDescent());
			tc.setAscent(fm.getAscent());
			tc.setDescent(fm.getDescent());
			tc.setX(lastx);
			tc.setY(0);
			tc.setFont(font);
			tc.setColor(colorFactory.getColor(i));
			lastx += bounds.getWidth();
			characters.addCharacter(tc);
		}
		return characters;
	}

}
