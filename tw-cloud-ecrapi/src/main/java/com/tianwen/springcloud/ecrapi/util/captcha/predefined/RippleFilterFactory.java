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
package com.tianwen.springcloud.ecrapi.util.captcha.predefined;

import com.tianwen.springcloud.ecrapi.util.captcha.filter.AbstractFilterFactory;
import com.tianwen.springcloud.ecrapi.util.captcha.library.RippleImageOp;

import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.List;


public class RippleFilterFactory extends AbstractFilterFactory {

	protected List<BufferedImageOp> filters;
	protected RippleImageOp ripple;

	public RippleFilterFactory() {
		ripple = new RippleImageOp();
	}

	protected List<BufferedImageOp> getPreRippleFilters() {
		return new ArrayList<BufferedImageOp>();
	}

	protected List<BufferedImageOp> getPostRippleFilters() {
		return new ArrayList<BufferedImageOp>();
		
	}
	
	@Override
	public List<BufferedImageOp> getFilters() {
		if (filters == null) {
			filters = new ArrayList<BufferedImageOp>();
			filters.addAll(getPreRippleFilters());
			filters.add(ripple);
			filters.addAll(getPostRippleFilters());
		}
		return filters;
	}

	
}
