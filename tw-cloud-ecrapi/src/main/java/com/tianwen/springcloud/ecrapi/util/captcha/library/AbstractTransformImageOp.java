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
package com.tianwen.springcloud.ecrapi.util.captcha.library;


public abstract class AbstractTransformImageOp extends AbstractImageOp {

	protected abstract void transform(int x, int y, double[] t);
	
	protected void init() {
	}

	private boolean initialized;
	
	public AbstractTransformImageOp() {
		setEdgeMode(EDGE_CLAMP);
	}
	
	@Override
	protected void filter(int[] inPixels, int[] outPixels, int width, int height) {
		if (!initialized) {
			init();
			initialized = true;
		}
//		long time1 = System.currentTimeMillis();
		double[]t = new double[2];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				transform(x, y, t);
				int pixel = getPixelBilinear(inPixels, t[0], t[1], width, height, getEdgeMode());
				outPixels[x + y * width] = pixel;
			}
		}
	}
	
}

