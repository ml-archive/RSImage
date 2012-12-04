/*
   Copyright 2012 Cesar Aguilar

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.caguilar.android.filters.scripts;

import android.content.res.Resources;
import android.renderscript.RenderScript;

/**
 * Created with IntelliJ IDEA.
 * User: cesaraguilar
 * Date: 11/18/12
 * Time: 11:17 AM
 */
public class HalftoneFilter extends ScriptC_halftonefilter {
    float mWidth = 0.0f;

    public HalftoneFilter(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
    }

    @Override
    public void set_fractionalWidthOfAPixel(float newValue) {
        float singlePixelSpacing;
        if (mWidth != 0.0f)
        {
            singlePixelSpacing = 1.0f / mWidth;
        }
        else
        {
            singlePixelSpacing = 1.0f / 2048.0f;
        }

        if (newValue < singlePixelSpacing)
        {
            newValue = singlePixelSpacing;
        }
        super.set_fractionalWidthOfAPixel(newValue);
    }

    public void setInputSize(int width, int height) {
        mWidth = (float)width;
        float mHeight = (float)height;
        set_aspectRatio(mWidth/mHeight);
    }
}
