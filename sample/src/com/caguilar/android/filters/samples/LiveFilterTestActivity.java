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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.io.IOException;

public class LiveFilterTestActivity extends Activity implements SeekBar.OnSeekBarChangeListener, TextureView.SurfaceTextureListener, SurfaceTexture.OnFrameAvailableListener {

    TextureView mTextureView;
    private Camera mCamera;

    public int LAYOUT = R.layout.livefiltersample;
    RenderScript mRS;
    Resources resources;
    Allocation mInAllocation;
    Allocation mOutAllocation;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        mTextureView = (TextureView)findViewById(R.id.imageView);
        mTextureView.setSurfaceTextureListener(this);


        if(findViewById(R.id.valueBar)!=null){
            SeekBar bar = (SeekBar)findViewById(R.id.valueBar);
            bar.setOnSeekBarChangeListener(this);
            bar.setMax((int)getIntent().getFloatExtra("maxValue",0.0f));
            bar.setProgress((int)getIntent().getFloatExtra("defaultValue",100.0f));
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open();

        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                previewSize.width, previewSize.height);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,1);
        mTextureView.setLayoutParams(params);
        surface.setOnFrameAvailableListener(this);

        try {
            mCamera.setPreviewTexture(surface);
        } catch (IOException t) {
        }

        mCamera.startPreview();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, the Camera does all the work for us
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Called whenever a new frame is available and displayed in the TextureView
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//        doChange(seekBar);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

//    public void doChange(SeekBar seekBar){
//        String effectName = getIntent().getStringExtra("filter");
//        ScriptC mScript;
//
//        float newI = seekBar.getProgress();
//        newI = newI- getIntent().getFloatExtra("minusValue", 0.0f);
//        float value = newI/getIntent().getFloatExtra("divisionValue", 1.0f);
//
//        if(effectName.equalsIgnoreCase("basic")){
//            mScript = new ScriptC_basicfilter(mRS, resources, R.raw.basicfilter);
//            ((ScriptC_basicfilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if(effectName.equalsIgnoreCase("saturation")){
//            mScript = new ScriptC_saturationfilter(mRS, resources, R.raw.saturationfilter);
//            ((ScriptC_saturationfilter)mScript).set_saturationValue(value);
//            ((ScriptC_saturationfilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if(effectName.equalsIgnoreCase("contrast")){
//            mScript = new ScriptC_contrastfilter(mRS, resources, R.raw.contrastfilter);
//            ((ScriptC_contrastfilter)mScript).set_contrastValue(value);
//            ((ScriptC_contrastfilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if(effectName.equalsIgnoreCase("brightness")){
//            mScript = new ScriptC_brightnessfilter(mRS, resources, R.raw.brightnessfilter);
//            ((ScriptC_brightnessfilter)mScript).set_brightnessValue(value);
//            ((ScriptC_brightnessfilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if (effectName.equalsIgnoreCase("sepia")) {
//            mScript = new SepiaFilter(mRS, resources, R.raw.colormatrixfilter);
//            ((SepiaFilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if (effectName.equalsIgnoreCase("grayscale")) {
//            mScript = new ScriptC_grayscalefilter(mRS, resources, R.raw.grayscalefilter);
//            ((ScriptC_grayscalefilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if (effectName.equalsIgnoreCase("halftone")) {
//            mScript = new HalftoneFilter(mRS, resources, R.raw.halftonefilter);
//            ((HalftoneFilter)mScript).setInputSize(filteredBitmap.getWidth(), filteredBitmap.getHeight());
//            ((HalftoneFilter)mScript).set_fractionalWidthOfAPixel(value);
//            ((HalftoneFilter)mScript).set_inTexture(mInAllocation);
//            ((HalftoneFilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if (effectName.equalsIgnoreCase("colorinvert")) {
//            mScript = new ScriptC_invertcolorfilter(mRS, resources, R.raw.invertcolorfilter);
//            ((ScriptC_invertcolorfilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if (effectName.equalsIgnoreCase("hue")) {
//            mScript = new HueFilter(mRS, resources, R.raw.huefilter);
//            ((HueFilter)mScript).setHue(value);
//            ((HueFilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if (effectName.equalsIgnoreCase("monochrome")) {
//            mScript = new ScriptC_monochromefilter(mRS, resources, R.raw.monochromefilter);
//            ((ScriptC_monochromefilter)mScript).set_intensityValue(value);
//            ((ScriptC_monochromefilter)mScript).set_filterColor(new Float3(0.0f, 0.0f, 1.0f));
//            ((ScriptC_monochromefilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if (effectName.equalsIgnoreCase("exposure")) {
//            mScript = new ScriptC_exposurefilter(mRS, resources, R.raw.exposurefilter);
//            ((ScriptC_exposurefilter)mScript).set_exposureValue(value);
//            ((ScriptC_exposurefilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        } else if (effectName.equalsIgnoreCase("gamma")) {
//            mScript = new ScriptC_gammafilter(mRS, resources, R.raw.gammafilter);
//            ((ScriptC_gammafilter)mScript).set_gammaValue(value);
//            ((ScriptC_gammafilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        } else if(effectName.equalsIgnoreCase("rgb")){
//            mScript = new ScriptC_rgbfilter(mRS, resources, R.raw.rgbfilter);
//            ((ScriptC_rgbfilter)mScript).set_redValue(1.0f);
//            ((ScriptC_rgbfilter)mScript).set_greenValue(value);
//            ((ScriptC_rgbfilter)mScript).set_blueValue(1.0f);
//            ((ScriptC_rgbfilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if (effectName.equalsIgnoreCase("opacity")) {
//            mScript = new ScriptC_opacityfilter(mRS, resources, R.raw.opacityfilter);
//            ((ScriptC_opacityfilter)mScript).set_opacityValue(value);
//            ((ScriptC_opacityfilter) mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if (effectName.equalsIgnoreCase("luminancethreshold")) {
//            mScript = new ScriptC_luminancethresholdfilter(mRS, resources, R.raw.luminancethresholdfilter);
//            ((ScriptC_luminancethresholdfilter)mScript).set_thresholdValue(value);
//            ((ScriptC_luminancethresholdfilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }else if (effectName.equalsIgnoreCase("tonecurve")) {
//            mScript = new ToneCurveFilter(mRS, resources, R.raw.tonecurvefilter);
//            ArrayList<PointF> blueCurvePF = new ArrayList<PointF>();
//            blueCurvePF.add(new PointF(0.0f, 0.0f));
//            blueCurvePF.add(new PointF(0.5f, value));
//            blueCurvePF.add(new PointF(1.0f, 0.75f));
//            ((ToneCurveFilter)mScript).setBlueControlPoints(blueCurvePF);
//            ((ToneCurveFilter)mScript).updateToneCurveTexture();
//            ((ToneCurveFilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        } else if (effectName.equalsIgnoreCase("gaussianblur")) {
//            mScript = new GaussianBlurFilter(mRS, resources, R.raw.convolutionseperablefilter);
//            ((GaussianBlurFilter)mScript).setBlurSize(value);
//            ((GaussianBlurFilter)mScript).set_imageHeight(originalBitmap.getHeight());
//            ((GaussianBlurFilter)mScript).set_imageWidth(originalBitmap.getWidth());
//            ((GaussianBlurFilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        } else if (effectName.equalsIgnoreCase("gaussianselectiveblur")) {
//            mScript = new SelectiveGaussianBlurFilter(mRS, resources, R.raw.convolutionseperablefilter);
//            ((SelectiveGaussianBlurFilter)mScript).set_imageHeight(originalBitmap.getHeight());
//            ((SelectiveGaussianBlurFilter)mScript).set_imageWidth(originalBitmap.getWidth());
//            ((SelectiveGaussianBlurFilter)mScript).set_excludeCircleRadius(value);
//            ((SelectiveGaussianBlurFilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        } else if (effectName.equalsIgnoreCase("tiltshift")) {
//            mScript = new TiltShiftGaussianBlurFilter(mRS, resources, R.raw.convolutionseperablefilter);
//            ((TiltShiftGaussianBlurFilter)mScript).set_imageHeight(originalBitmap.getHeight());
//            ((TiltShiftGaussianBlurFilter)mScript).set_imageWidth(originalBitmap.getWidth());
//            ((TiltShiftGaussianBlurFilter)mScript).set_topFocusLevel(value-0.1f);
//            ((TiltShiftGaussianBlurFilter)mScript).set_bottomFocusLevel(value+0.1f);
//            ((TiltShiftGaussianBlurFilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        } else if (effectName.equalsIgnoreCase("tiltshiftvertical")) {
//            mScript = new TiltShiftGaussianBlurFilter(mRS, resources, R.raw.convolutionseperablefilter);
//            ((TiltShiftGaussianBlurFilter)mScript).set_imageHeight(originalBitmap.getHeight());
//            ((TiltShiftGaussianBlurFilter)mScript).set_imageWidth(originalBitmap.getWidth());
//            ((TiltShiftGaussianBlurFilter)mScript).set_topFocusLevel(value-0.1f);
//            ((TiltShiftGaussianBlurFilter)mScript).set_bottomFocusLevel(value+0.1f);
//            ((TiltShiftGaussianBlurFilter)mScript).set_direction(0);
//            ((TiltShiftGaussianBlurFilter)mScript).forEach_root(mInAllocation, mOutAllocation);
//        }
//        mOutAllocation.copyTo(filteredBitmap);
//        imageView.setImageBitmap(filteredBitmap);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,1,"Values").setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==1){
            SeekBar bar = (SeekBar)findViewById(R.id.valueBar);
            float newI = bar.getProgress();
            newI = newI- Float.parseFloat(getIntent().getStringExtra("minusValue"));
            float value = newI/Float.parseFloat(getIntent().getStringExtra("divisionValue"));
            makeDialog(value+"","Value");
        }
        return true;
    }

    public AlertDialog makeDialog(String message, String title) {
        if (isFinishing())
            return null;

        AlertDialog.Builder builder = new AlertDialog.Builder(
                getParent() != null ? getParent() : this);
        builder.setMessage(message)
                .setCancelable(true)
                .setTitle(title)
                .setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }
}
