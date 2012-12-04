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
public class SelectiveGaussianBlurFilter extends GaussianBlurFilter {

    ScriptC_selectivefilter mScript;
    public SelectiveGaussianBlurFilter(RenderScript rs) {
        super(rs);
        mScript = new ScriptC_selectivefilter(rs);
        init();
    }

    public SelectiveGaussianBlurFilter(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        mScript = new ScriptC_selectivefilter(rs, resources, R.raw.selectivefilter);
        init();
    }

    public void init(){

        setBlurSize(2.0f);
        mScript.set_aspectRatio(0.75f);
        mScript.set_excludeBlurSize(30.0f/320.0f);
        mScript.set_excludeCircleRadius(60.0f/320.0f);
        mScript.set_excludeCirclePoint(new Float2(0.5f, 0.5f));
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

    public void set_excludeCirclePoint(Float2 ratio){
        mScript.set_excludeCirclePoint(ratio);
    }
    public void set_excludeCircleRadius(float ratio){
        mScript.set_excludeCircleRadius(ratio);
    }
    public void set_excludeBlurSize(float ratio){
        mScript.set_excludeBlurSize(ratio);
    }
    public void set_aspectRatio(float ratio){
        mScript.set_aspectRatio(ratio);
    }
}
