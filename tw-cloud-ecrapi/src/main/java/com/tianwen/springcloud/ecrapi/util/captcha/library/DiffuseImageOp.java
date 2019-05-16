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

import java.util.Random;

public class DiffuseImageOp extends AbstractTransformImageOp {

	double[] tx;
	double[] ty;
	double amount;

	public DiffuseImageOp() {
		amount = 1.6;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	protected synchronized void init() {
		tx = new double[256];
		ty = new double[256];
		for (int i = 0; i < 256; i++) {
			double angle = 2 * Math.PI * i / 256;
			tx[i] = amount * Math.sin(angle);
			ty[i] = amount * Math.cos(angle);
		}
	}

	@Override
	protected void transform(int x, int y, double[] t) {
		Random r = new Random();
		int angle = (int) (r.nextFloat() * 255);
		t[0] = x + tx[angle];
		t[1] = y + ty[angle];
	}

}
