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


public abstract class AbstractConvolveImageOp extends AbstractImageOp {

	private float[][] matrix; 
	
	protected AbstractConvolveImageOp(float[][] matrix) {
		this.matrix = matrix;
	}

	@Override
	protected void filter(int[] inPixels, int[] outPixels, int width, int height) {
//		long time1 = System.currentTimeMillis();
		int matrixWidth = matrix[0].length;
		int matrixHeight = matrix.length;
		int mattrixLeft = - matrixWidth / 2; 
		int matrixTop = - matrixHeight / 2;
		for (int y = 0; y < height; y++) {
			int ytop = y + matrixTop;
			int ybottom = y + matrixTop + matrixHeight; 
			for (int x = 0; x < width; x++) {
				float[] sum = {0.5f, 0.5f, 0.5f, 0.5f};
				int xleft = x + mattrixLeft;
				int xright = x + mattrixLeft + matrixWidth;
				int matrixY = 0;
				for (int my = ytop; my < ybottom; my ++, matrixY++) {
					int matrixX = 0;
					for (int mx = xleft; mx < xright; mx ++, matrixX ++) {
						int pixel = getPixel(inPixels, mx, my, width, height, EDGE_ZERO);
						float m = matrix[matrixY][matrixX];
						sum[0] += m * ((pixel >> 24) & 0xff);
						sum[1] += m * ((pixel >> 16) & 0xff);
						sum[2] += m * ((pixel >> 8) & 0xff);
						sum[3] += m * (pixel & 0xff);
					}
				}
				outPixels[x + y * width] = (limitByte((int)sum[0]) << 24) | (limitByte((int)sum[1]) << 16) | (limitByte((int)sum[2]) << 8) | (limitByte((int)sum[3]));				
			}
		}
		
	}

	
}
