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
import android.renderscript.Float2;
import android.renderscript.RenderScript;
import android.renderscript.Script;

/**
 * Created with IntelliJ IDEA.
 * User: cesaraguilar
 * Date: 12/4/12
 * Time: 3:02 PM
 */
public class GaussianBlurFilter extends ScriptC_convolutionseperablefilter {
    float blurSize = 2.0f;
    float kernel[];
    Allocation kernelAllocation;

    public GaussianBlurFilter(RenderScript rs) {
        super(rs);
        kernel = makeGaussiannKernel(4);
        kernelAllocation = Allocation.createSized(rs, Element.F32(rs),9);
        kernelAllocation.copyFrom(kernel);
    }

    public GaussianBlurFilter(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        kernel = makeGaussiannKernel(4);
        kernelAllocation = Allocation.createSized(rs, Element.F32(rs),9);
        kernelAllocation.copyFrom(kernel);
    }

    @Override
    public void invoke_filter(Script script,Allocation mInAllocation, Allocation mOutAllocation) {
        set_inTexture(mInAllocation);
        set_matrixLenght(9);
        set_XYOffset(new Float2(1 * blurSize, 0));
        set_matrixTexture(kernelAllocation);
        super.invoke_filter(script, mInAllocation, mOutAllocation);
        set_inTexture(mOutAllocation);
        set_XYOffset(new Float2(0, 1 * blurSize));
        super.invoke_filter(script,mInAllocation, mOutAllocation);
    }

    public void setBlurSize(float value) {
        blurSize = value;
    }

    public  float[] makeGaussiannKernel(int radius){
        int r = (int)Math.ceil(radius);
        int rows = r*2+1;
        float[] matrix = new float[rows];
        float sigma = ((float)radius)/3.0f;
        float sigma22 = 2.0f*sigma*sigma;
        float sigmaPi2 = 2.0f* (float)(Math.PI*sigma);
        float sqrtSigmaPi2 = (float)Math.sqrt(sigmaPi2);
        float radius2 = radius*radius;
        float total = 0.0f;
        int index = 0;
        for (int row = -r; row <= r; row++) {
            float distance = row*row;
            if (distance > radius2)
                matrix[index] = 0.0f;
            else
                matrix[index] = (float)Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
            total += matrix[index];
            index++;
        }
        for (int i = 0; i < rows; i++)
            matrix[i] /= total;

        return matrix;
    }
}
