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


public class WobbleImageOp extends AbstractTransformImageOp {

	private double xWavelength;
	private double yWavelength;
	private double xAmplitude;
	private double yAmplitude;
	private double xRandom;
	private double yRandom;
	private double xScale;
	private double yScale;

	public WobbleImageOp() {
		xWavelength = 15;
		yWavelength = 15;
		xAmplitude = 4.0;
		yAmplitude = 3.0;
		xScale = 1.0;
		yScale = 1.0;
		xRandom = 3 * Math.random();
		yRandom = 10 * Math.random();
	}

	public double getxWavelength() {
		return xWavelength;
	}

	public void setxWavelength(double xWavelength) {
		this.xWavelength = xWavelength;
	}

	public double getyWavelength() {
		return yWavelength;
	}

	public void setyWavelength(double yWavelength) {
		this.yWavelength = yWavelength;
	}

	public double getxAmplitude() {
		return xAmplitude;
	}

	public void setxAmplitude(double xAmplitude) {
		this.xAmplitude = xAmplitude;
	}

	public double getyAmplitude() {
		return yAmplitude;
	}

	public void setyAmplitude(double yAmplitude) {
		this.yAmplitude = yAmplitude;
	}

	public double getxScale() {
		return xScale;
	}

	public void setxScale(double xScale) {
		this.xScale = xScale;
	}

	public double getyScale() {
		return yScale;
	}

	public void setyScale(double yScale) {
		this.yScale = yScale;
	}

	@Override
	protected void transform(int x, int y, double[] t) {
		double tx = Math.cos((double) (xScale * x + y) / xWavelength + xRandom);
		double ty = Math.sin((double) (yScale * y + x) / yWavelength + yRandom);
		t[0] = x + xAmplitude * tx;
		t[1] = y + yAmplitude * ty;

	}

}
