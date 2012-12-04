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
import android.renderscript.Matrix4f;
import android.renderscript.RenderScript;

/**
 * Created with IntelliJ IDEA.
 * User: cesaraguilar
 * Date: 11/18/12
 * Time: 11:17 AM
 */
public class ColorMatrixFilter extends ScriptC_colormatrixfilter {
    public ColorMatrixFilter(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        set_intensityValue(1.0f);
        Matrix4f colorMatrix = new Matrix4f();
        colorMatrix.set(0,0,1.0f);
        colorMatrix.set(1,0,0.0f);
        colorMatrix.set(2,0,0.0f);
        colorMatrix.set(3,0,0.0f);

        colorMatrix.set(0,1,0.0f);
        colorMatrix.set(1,1,1.0f);
        colorMatrix.set(2,1,0.0f);
        colorMatrix.set(3,1,0.0f);

        colorMatrix.set(0,2,0.0f);
        colorMatrix.set(1,2,0.0f);
        colorMatrix.set(2,2,1.0f);
        colorMatrix.set(3,2,0.0f);

        colorMatrix.set(0,3,0.0f);
        colorMatrix.set(1,3,0.0f);
        colorMatrix.set(2,3,0.0f);
        colorMatrix.set(3,3,1.0f);

        set_colorMatrix(colorMatrix);
    }
}
