/*
 * Copyright (C) 2012 Cesar Aguilar
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.caguilar.android.filters.samples;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import com.caguilar.android.filters.scripts.Yuv2Rgb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Tests for manual verification of the CDD-required camera output formats
 * for preview callbacks
 */
public class LiveFilterTestActivity extends Activity
        implements TextureView.SurfaceTextureListener, Camera.PreviewCallback, SeekBar.OnSeekBarChangeListener {
    private TextureView mPreviewView;
    private SurfaceTexture mPreviewTexture;
    private int mPreviewTexWidth;
    private int mPreviewTexHeight;
    private ImageView mFormatView;
    private Camera mCamera;
    private Camera.Size mPreviewSize;
    private Bitmap mCallbackBitmap;
    private Bitmap mPreCallbackBitmap;

    private static final int STATE_OFF = 0;
    private static final int STATE_PREVIEW = 1;
    private static final int STATE_NO_CALLBACKS = 2;
    private int mState = STATE_OFF;
    private boolean mProcessInProgress = false;
    private RenderScript mRS;
//    private RsYuv mFilterYuv;
    boolean FRONT;

    Allocation mInAllocation;
    Allocation mOutAllocation;
    Yuv2Rgb mScript;
    float mCurrentValue;
    String mCurrentEffect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentEffect = getIntent().getStringExtra("filter");
        setTitle(mCurrentEffect);
        setContentView(R.layout.livefiltersample);
        mPreviewView = (TextureView) findViewById(R.id.preview_view);
        mFormatView = (ImageView) findViewById(R.id.format_view);
        mPreviewView.setSurfaceTextureListener(this);
        mRS = RenderScript.create(this);

        if(Camera.getNumberOfCameras()>1){
            findViewById(R.id.flip_button).setVisibility(View.VISIBLE);
            findViewById(R.id.flip_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FRONT){
                        FRONT = false;
                        setUpCamera(0);
                    }else{
                        FRONT = true;
                        setUpCamera(1);
                    }

                }
            });
        }

        if(findViewById(R.id.valueBar)!=null){
            SeekBar bar = (SeekBar)findViewById(R.id.valueBar);
            bar.setOnSeekBarChangeListener(this);
            bar.setMax((int)getIntent().getFloatExtra("maxValue",0.0f));
            bar.setProgress((int)getIntent().getFloatExtra("defaultValue",100.0f));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        doChange(seekBar);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        doChange(seekBar);
    }

    public void doChange(SeekBar seekBar){
        float newI = seekBar.getProgress();
        newI = newI- getIntent().getFloatExtra("minusValue", 0.0f);
        mCurrentValue = newI/getIntent().getFloatExtra("divisionValue", 1.0f);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpCamera(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        shutdownCamera();
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                          int width, int height) {
        mPreviewTexture = surface;
        mPreviewTexWidth = width;
        mPreviewTexHeight = height;
        if (mCamera != null) {
            startPreview();
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }

    private void setUpCamera(int id) {
        shutdownCamera();
        mCamera = Camera.open(id);
        if (mPreviewTexture != null) {
            startPreview();
        }
    }

    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.001;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(h, w);

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private void shutdownCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            mState = STATE_OFF;
        }
    }

    public void setupPreviewSize(){
        Camera.Parameters p = mCamera.getParameters();
        List<Camera.Size> unsortedSizes = p.getSupportedPreviewSizes();
        class SizeCompare implements Comparator<Camera.Size> {
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.width < rhs.width) return -1;
                if (lhs.width > rhs.width) return 1;
                if (lhs.height < rhs.height) return -1;
                if (lhs.height > rhs.height) return 1;
                return 0;
            }
        };
        SizeCompare s = new SizeCompare();
        TreeSet<Camera.Size> sortedResolutions = new TreeSet<Camera.Size>(s);
        sortedResolutions.addAll(unsortedSizes);
        List<Camera.Size> mPreviewSizes = new ArrayList<Camera.Size>(sortedResolutions);
        mPreviewSize = getOptimalPreviewSize(mPreviewSizes, mPreviewTexWidth, mPreviewTexHeight);
    }

    private void startPreview() {
        if (mState != STATE_OFF) {
            // Stop for a while to drain callbacks
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mState = STATE_OFF;
            Handler h = new Handler();
            Runnable mDelayedPreview = new Runnable() {
                public void run() {
                    startPreview();
                }
            };
            h.postDelayed(mDelayedPreview, 300);
            return;
        }
        mState = STATE_PREVIEW;

        setupPreviewSize();

        Matrix transform = new Matrix();
        float widthRatio = mPreviewSize.width / (float)mPreviewTexWidth;
        float heightRatio = mPreviewSize.height / (float)mPreviewTexHeight;

        transform.setScale(1, heightRatio/widthRatio);
        transform.postTranslate(0,
                mPreviewTexHeight * (1 - heightRatio/widthRatio)/2);

        mPreviewView.setTransform(transform);

        Camera.Parameters p = mCamera.getParameters();
        p.setPreviewFormat(ImageFormat.NV21);
        p.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        mCamera.setParameters(p);

        mCamera.setPreviewCallbackWithBuffer(this);
        int expectedBytes = mPreviewSize.width * mPreviewSize.height *
                ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
        for (int i=0; i < 4; i++) {
            mCamera.addCallbackBuffer(new byte[expectedBytes]);
        }
        try {
            mCamera.setPreviewTexture(mPreviewTexture);
            mCamera.startPreview();
        } catch (IOException ioe) {
        }
    }


    private class ProcessPreviewDataTask extends AsyncTask<byte[], Void, Boolean> {
        protected Boolean doInBackground(byte[]... datas) {
            byte[] data = datas[0];
            //mFilterYuv.execute(data, mPreCallbackBitmap);
            try{
                mInAllocation.copyFrom(mPreCallbackBitmap);
            }catch (Throwable e){
                mScript = new Yuv2Rgb(mRS,getResources(),R.raw.yuv2rgb);
                mInAllocation = Allocation.createFromBitmap(mRS, mCallbackBitmap,
                        Allocation.MipmapControl.MIPMAP_NONE,
                        Allocation.USAGE_SCRIPT);
                mOutAllocation = Allocation.createTyped(mRS, mInAllocation.getType());
                mOutAllocation.copyFrom(mPreCallbackBitmap);
            }

            //CONVERT TO RGB
            mScript.convert(data,mPreCallbackBitmap.getWidth(),mPreCallbackBitmap.getHeight(), mOutAllocation);
            mOutAllocation.copyTo(mPreCallbackBitmap);
            mInAllocation.copyFrom(mPreCallbackBitmap);


            FilterSystem.applyFilter(mInAllocation,mOutAllocation,mCurrentValue,mCurrentEffect,mRS,getResources(),mCallbackBitmap);
            mOutAllocation.copyTo(mCallbackBitmap);
            if(mCamera!=null){
                mCamera.addCallbackBuffer(data);
            }
            mProcessInProgress = false;
            return true;
        }

        protected void onPostExecute(Boolean result) {
            mFormatView.invalidate();
        }

    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mProcessInProgress || mState != STATE_PREVIEW) {
            mCamera.addCallbackBuffer(data);
            return;
        }
        if (data == null) {
            return;
        }
        int expectedBytes = mPreviewSize.width * mPreviewSize.height *
                ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
        if (expectedBytes != data.length) {
            mState = STATE_NO_CALLBACKS;
            mCamera.setPreviewCallbackWithBuffer(null);
            return;
        }
        mProcessInProgress = true;
        if (mCallbackBitmap == null ||
                mPreviewSize.width != mCallbackBitmap.getWidth() ||
                mPreviewSize.height != mCallbackBitmap.getHeight() ) {
            mCallbackBitmap =
                    Bitmap.createBitmap(
                            mPreviewSize.width, mPreviewSize.height,
                            Bitmap.Config.ARGB_8888);
            mPreCallbackBitmap =
                    Bitmap.createBitmap(
                            mPreviewSize.width, mPreviewSize.height,
                            Bitmap.Config.ARGB_8888);
            //mFilterYuv = new RsYuv(mRS, getResources(), mPreviewSize.width, mPreviewSize.height);
            mFormatView.setImageBitmap(mCallbackBitmap);
        }
        mFormatView.invalidate();
        mCamera.addCallbackBuffer(data);
        mProcessInProgress = true;
        new ProcessPreviewDataTask().execute(data);
    }

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
}