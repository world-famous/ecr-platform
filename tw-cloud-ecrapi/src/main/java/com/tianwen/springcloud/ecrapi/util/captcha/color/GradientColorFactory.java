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
package com.tianwen.springcloud.ecrapi.util.captcha.color;

import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class GradientColorFactory implements ColorFactory {

	private Color start;
	private Color step;
	
	public GradientColorFactory() {
		start = new Color(192, 192, 0);
		step = new Color(192, 128, 128);
	}
	
	@Override
	public Color getColor(int index) {
		return new Color((start.getRed() + step.getRed() * index) % 256, 
				(start.getGreen() + step.getGreen() * index) % 256,  
				(start.getBlue() + step.getBlue() * index) % 256);
	}

	public void setStart(Color start) {
		this.start = start;
	}

	public void setStep(Color step) {
		this.step = step;
	}

}
