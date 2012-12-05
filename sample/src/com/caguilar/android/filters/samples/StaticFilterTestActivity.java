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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;

public class StaticFilterTestActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    Bitmap originalBitmap;
    Bitmap filteredBitmap;
    ImageView imageView;
    RenderScript mRS;
    Allocation mInAllocation;
    Allocation mOutAllocation;
    String mCurrentEffect;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentEffect = getIntent().getStringExtra("filter");
        setTitle(mCurrentEffect);
        setContentView(R.layout.filtersample);
        imageView = (ImageView)findViewById(R.id.imageView);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDensity = DisplayMetrics.DENSITY_XHIGH;
        opts.inScaled = false;
        opts.inTargetDensity = DisplayMetrics.DENSITY_XHIGH;
        originalBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.gatesmall,opts);
        filteredBitmap = Bitmap.createBitmap(originalBitmap.getWidth(),originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        imageView.setImageBitmap(originalBitmap);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


        //RENDERSCRIPT ALLOCATION
        mRS = RenderScript.create(this);
        mInAllocation = Allocation.createFromBitmap(mRS, originalBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        mOutAllocation = Allocation.createTyped(mRS, mInAllocation.getType());
        mOutAllocation.copyFrom(originalBitmap);
        mOutAllocation.copyTo(filteredBitmap);

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
        float value = newI/getIntent().getFloatExtra("divisionValue", 1.0f);

        FilterSystem.applyFilter(mInAllocation,mOutAllocation,value,mCurrentEffect,mRS,getResources(),originalBitmap);

        mOutAllocation.copyTo(filteredBitmap);
        imageView.setImageBitmap(filteredBitmap);
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
