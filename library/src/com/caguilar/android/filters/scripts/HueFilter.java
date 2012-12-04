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
 * Date: 11/19/12
 * Time: 5:03 PM
 */
public class HueFilter extends ScriptC_huefilter {
    public HueFilter(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
    }

    public void setHue(float newHue) {
        newHue =(float) ((newHue%360.0f) * Math.PI/180.0f);
        super.set_hueAdjust(newHue);
    }
}
