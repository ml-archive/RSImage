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
import android.renderscript.Element;
import android.renderscript.RenderScript;

public class Yuv2Rgb extends ScriptC_yuv2rgb {
    RenderScript mRS;
    Allocation yuvAllocation;
    public Yuv2Rgb(RenderScript rs) {
        super(rs);
        mRS = rs;
    }

    public Yuv2Rgb(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        mRS = rs;
    }

    public void convert(byte[] mYUVData, int imageWidth, int imageHeight, Allocation mOutAllocation) {
        set_imageHeight(imageHeight);
        set_imageWidth(imageWidth);
        try{
            yuvAllocation.copyFrom(mYUVData);
        }catch (Throwable t){
            yuvAllocation = Allocation.createSized(mRS, Element.I8(mRS), mYUVData.length);
            yuvAllocation.copyFrom(mYUVData);
        }
        set_yuvData(yuvAllocation);
        invoke_convert(this,mOutAllocation,mOutAllocation);
    }
}
