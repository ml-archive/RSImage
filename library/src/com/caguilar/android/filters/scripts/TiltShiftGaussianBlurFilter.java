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
import android.renderscript.Allocation;
import android.renderscript.Float2;
import android.renderscript.RenderScript;
import com.caguilar.android.filters.R;

/**
 * Created with IntelliJ IDEA.
 * User: cesaraguilar
 * Date: 12/4/12
 * Time: 3:02 PM
 */
public class TiltShiftGaussianBlurFilter extends GaussianBlurFilter {

    ScriptC_tiltshiftfilter mScript;
    public TiltShiftGaussianBlurFilter(RenderScript rs) {
        super(rs);
        mScript = new ScriptC_tiltshiftfilter(rs);
        init();
    }

    public TiltShiftGaussianBlurFilter(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        mScript = new ScriptC_tiltshiftfilter(rs, resources, R.raw.tiltshiftfilter);
        init();
    }

    public void init(){
        setBlurSize(2.0f);
        mScript.set_bottomFocusLevel(0.6f);
        mScript.set_direction(1);
        mScript.set_focusFallOffRate(0.2f);
        mScript.set_topFocusLevel(0.4f);
    }


    public void set_imageWidth(int width){
        super.set_imageWidth(width);
        mScript.set_imageWidth(width);
    }

    public void set_imageHeight(int height){
        super.set_imageHeight(height);
        mScript.set_imageHeight(height);
    }

    @Override
    public void forEach_root(Allocation mInAllocation, Allocation mOutAllocation) {
        super.forEach_root(mInAllocation, mOutAllocation);
        mScript.set_blurTexture(mOutAllocation);
        mScript.set_sharpTexture(mInAllocation);
        mScript.forEach_root(mInAllocation, mOutAllocation);
    }

    public void set_topFocusLevel(float bottomLevel){
        mScript.set_topFocusLevel(bottomLevel);
    }
    public void set_focusFallOffRate(float bottomLevel){
        mScript.set_focusFallOffRate(bottomLevel);
    }
    public void set_direction(int bottomLevel){
        mScript.set_direction(bottomLevel);
    }
    public void set_bottomFocusLevel(float bottomLevel){
        mScript.set_bottomFocusLevel(bottomLevel);
    }
}
