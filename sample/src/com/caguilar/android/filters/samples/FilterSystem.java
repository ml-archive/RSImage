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

package com.caguilar.android.filters.samples;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.renderscript.Allocation;
import android.renderscript.Float3;
import android.renderscript.RenderScript;
import android.renderscript.ScriptC;
import com.caguilar.android.filters.scripts.GaussianBlurFilter;
import com.caguilar.android.filters.scripts.HalftoneFilter;
import com.caguilar.android.filters.scripts.HueFilter;
import com.caguilar.android.filters.scripts.ScriptC_basicfilter;
import com.caguilar.android.filters.scripts.ScriptC_brightnessfilter;
import com.caguilar.android.filters.scripts.ScriptC_contrastfilter;
import com.caguilar.android.filters.scripts.ScriptC_exposurefilter;
import com.caguilar.android.filters.scripts.ScriptC_gammafilter;
import com.caguilar.android.filters.scripts.ScriptC_grayscalefilter;
import com.caguilar.android.filters.scripts.ScriptC_invertcolorfilter;
import com.caguilar.android.filters.scripts.ScriptC_luminancethresholdfilter;
import com.caguilar.android.filters.scripts.ScriptC_monochromefilter;
import com.caguilar.android.filters.scripts.ScriptC_opacityfilter;
import com.caguilar.android.filters.scripts.ScriptC_rgbfilter;
import com.caguilar.android.filters.scripts.ScriptC_saturationfilter;
import com.caguilar.android.filters.scripts.SelectiveGaussianBlurFilter;
import com.caguilar.android.filters.scripts.SepiaFilter;
import com.caguilar.android.filters.scripts.TiltShiftGaussianBlurFilter;
import com.caguilar.android.filters.scripts.ToneCurveFilter;

import java.util.ArrayList;

public class FilterSystem {

    public static void applyFilter(Allocation mInAllocation, Allocation mOutAllocation, float value, String effectName,
                                   RenderScript mRS, Resources resources, Bitmap originalBitmap){
        ScriptC mScript;
        if(effectName.equalsIgnoreCase("basic")){
            mScript = new ScriptC_basicfilter(mRS, resources, R.raw.basicfilter);
            ((ScriptC_basicfilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if(effectName.equalsIgnoreCase("saturation")){
            mScript = new ScriptC_saturationfilter(mRS, resources, R.raw.saturationfilter);
            ((ScriptC_saturationfilter)mScript).set_saturationValue(value);
            ((ScriptC_saturationfilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if(effectName.equalsIgnoreCase("contrast")){
            mScript = new ScriptC_contrastfilter(mRS, resources, R.raw.contrastfilter);
            ((ScriptC_contrastfilter)mScript).set_contrastValue(value);
            ((ScriptC_contrastfilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if(effectName.equalsIgnoreCase("brightness")){
            mScript = new ScriptC_brightnessfilter(mRS, resources, R.raw.brightnessfilter);
            ((ScriptC_brightnessfilter)mScript).set_brightnessValue(value);
            ((ScriptC_brightnessfilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if (effectName.equalsIgnoreCase("sepia")) {
            mScript = new SepiaFilter(mRS, resources, R.raw.colormatrixfilter);
            ((SepiaFilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if (effectName.equalsIgnoreCase("grayscale")) {
            mScript = new ScriptC_grayscalefilter(mRS, resources, R.raw.grayscalefilter);
            ((ScriptC_grayscalefilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if (effectName.equalsIgnoreCase("halftone")) {
            mScript = new HalftoneFilter(mRS, resources, R.raw.halftonefilter);
            ((HalftoneFilter)mScript).setInputSize(originalBitmap.getWidth(), originalBitmap.getHeight());
            ((HalftoneFilter)mScript).set_fractionalWidthOfAPixel(value);
            ((HalftoneFilter)mScript).set_inTexture(mInAllocation);
            ((HalftoneFilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if (effectName.equalsIgnoreCase("invertcolor")) {
            mScript = new ScriptC_invertcolorfilter(mRS, resources, R.raw.invertcolorfilter);
            ((ScriptC_invertcolorfilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if (effectName.equalsIgnoreCase("hue")) {
            mScript = new HueFilter(mRS, resources, R.raw.huefilter);
            ((HueFilter)mScript).setHue(value);
            ((HueFilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if (effectName.equalsIgnoreCase("monochrome")) {
            mScript = new ScriptC_monochromefilter(mRS, resources, R.raw.monochromefilter);
            ((ScriptC_monochromefilter)mScript).set_intensityValue(value);
            ((ScriptC_monochromefilter)mScript).set_filterColor(new Float3(0.0f, 0.0f, 1.0f));
            ((ScriptC_monochromefilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if (effectName.equalsIgnoreCase("exposure")) {
            mScript = new ScriptC_exposurefilter(mRS, resources, R.raw.exposurefilter);
            ((ScriptC_exposurefilter)mScript).set_exposureValue(value);
            ((ScriptC_exposurefilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        } else if (effectName.equalsIgnoreCase("gamma")) {
            mScript = new ScriptC_gammafilter(mRS, resources, R.raw.gammafilter);
            ((ScriptC_gammafilter)mScript).set_gammaValue(value);
            ((ScriptC_gammafilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        } else if(effectName.equalsIgnoreCase("rgb")){
            mScript = new ScriptC_rgbfilter(mRS, resources, R.raw.rgbfilter);
            ((ScriptC_rgbfilter)mScript).set_redValue(1.0f);
            ((ScriptC_rgbfilter)mScript).set_greenValue(value);
            ((ScriptC_rgbfilter)mScript).set_blueValue(1.0f);
            ((ScriptC_rgbfilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if (effectName.equalsIgnoreCase("opacity")) {
            mScript = new ScriptC_opacityfilter(mRS, resources, R.raw.opacityfilter);
            ((ScriptC_opacityfilter)mScript).set_opacityValue(value);
            ((ScriptC_opacityfilter) mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if (effectName.equalsIgnoreCase("luminancethreshold")) {
            mScript = new ScriptC_luminancethresholdfilter(mRS, resources, R.raw.luminancethresholdfilter);
            ((ScriptC_luminancethresholdfilter)mScript).set_thresholdValue(value);
            ((ScriptC_luminancethresholdfilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }else if (effectName.equalsIgnoreCase("tonecurve")) {
            mScript = new ToneCurveFilter(mRS, resources, R.raw.tonecurvefilter);
            ArrayList<PointF> blueCurvePF = new ArrayList<PointF>();
            blueCurvePF.add(new PointF(0.0f, 0.0f));
            blueCurvePF.add(new PointF(0.5f, value));
            blueCurvePF.add(new PointF(1.0f, 0.75f));
            ((ToneCurveFilter)mScript).setBlueControlPoints(blueCurvePF);
            ((ToneCurveFilter)mScript).updateToneCurveTexture();
            ((ToneCurveFilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        } else if (effectName.equalsIgnoreCase("gaussianblur")) {
            mScript = new GaussianBlurFilter(mRS, resources, R.raw.convolutionseperablefilter);
            ((GaussianBlurFilter)mScript).setBlurSize(value);
            ((GaussianBlurFilter)mScript).set_imageHeight(originalBitmap.getHeight());
            ((GaussianBlurFilter)mScript).set_imageWidth(originalBitmap.getWidth());
            ((GaussianBlurFilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        } else if (effectName.equalsIgnoreCase("gaussianselectiveblur")) {
            mScript = new SelectiveGaussianBlurFilter(mRS, resources, R.raw.convolutionseperablefilter);
            ((SelectiveGaussianBlurFilter)mScript).set_imageHeight(originalBitmap.getHeight());
            ((SelectiveGaussianBlurFilter)mScript).set_imageWidth(originalBitmap.getWidth());
            ((SelectiveGaussianBlurFilter)mScript).set_excludeCircleRadius(value);
            ((SelectiveGaussianBlurFilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        } else if (effectName.equalsIgnoreCase("tiltshift")) {
            mScript = new TiltShiftGaussianBlurFilter(mRS, resources, R.raw.convolutionseperablefilter);
            ((TiltShiftGaussianBlurFilter)mScript).set_imageHeight(originalBitmap.getHeight());
            ((TiltShiftGaussianBlurFilter)mScript).set_imageWidth(originalBitmap.getWidth());
            ((TiltShiftGaussianBlurFilter)mScript).set_topFocusLevel(value-0.1f);
            ((TiltShiftGaussianBlurFilter)mScript).set_bottomFocusLevel(value+0.1f);
            ((TiltShiftGaussianBlurFilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        } else if (effectName.equalsIgnoreCase("tiltshiftvertical")) {
            mScript = new TiltShiftGaussianBlurFilter(mRS, resources, R.raw.convolutionseperablefilter);
            ((TiltShiftGaussianBlurFilter)mScript).set_imageHeight(originalBitmap.getHeight());
            ((TiltShiftGaussianBlurFilter)mScript).set_imageWidth(originalBitmap.getWidth());
            ((TiltShiftGaussianBlurFilter)mScript).set_topFocusLevel(value-0.1f);
            ((TiltShiftGaussianBlurFilter)mScript).set_bottomFocusLevel(value+0.1f);
            ((TiltShiftGaussianBlurFilter)mScript).set_direction(0);
            ((TiltShiftGaussianBlurFilter)mScript).invoke_filter(mScript,mInAllocation, mOutAllocation);
        }
    }
}
