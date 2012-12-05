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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class FiltersActivity extends Activity {
    protected ArrayList<FilterObject> list;
    boolean staticView = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);
        list = loadFilters();
        ListView elv = (ListView)findViewById(android.R.id.list);
        FiltersListAdapter myAdapter = new FiltersListAdapter(R.layout.listchild,list);
        elv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                FilterObject child = (FilterObject)list.get(position);
                Intent i;
                if(staticView){
                    i = new Intent(FiltersActivity.this, StaticFilterTestActivity.class);
                }else{
                    i = new Intent(FiltersActivity.this, LiveFilterTestActivity.class);
                }

                i.putExtra("filter",child.getValue());
                i.putExtra("defaultValue",child.getDefaultValue());
                i.putExtra("minusValue",child.getMinusValue());
                i.putExtra("maxValue",child.getMaxValue());
                i.putExtra("divisionValue",child.getDivisorValue());
                startActivity(i);
            }
        });
        elv.setAdapter(myAdapter);
    }

    public ArrayList<FilterObject> loadFilters(){
        return new ArrayList<FilterObject>(){{
            add(new FilterObject("Basic","basic",50,100,0,1));
            add(new FilterObject("Saturation","saturation",100,200,0,100));
            add(new FilterObject("Contrast","contrast",200,400,100,100));
            add(new FilterObject("Brightness","brightness",100,200,100,100));
            add(new FilterObject("Exposure","exposure",1000,2000,1000,100));
            add(new FilterObject("RGB","rgb",100,200,0,100));
            add(new FilterObject("Hue","hue",90,360,0,1));
            add(new FilterObject("Monochrome","monochrome",50,100,0,100));
            add(new FilterObject("Gamma","gamma",100,300,0,100));
            add(new FilterObject("Sepia","sepia",50,100,0,1));
            add(new FilterObject("Gray Scale","grayscale",50,100,0,1));
            add(new FilterObject("Invert Color","invertcolor",50,100,0,1));
            add(new FilterObject("Luminance Threshold","luminancethreshold",50,100,0,100));
            add(new FilterObject("Halftone","halftone",100,500,0,10000));
            add(new FilterObject("Tone Curve","tonecurve",50000,100000,0,100000));
            add(new FilterObject("Gaussian Blur","gaussianblur",50000,400000,0,100000));
            add(new FilterObject("Selective Gaussian Blur","gaussianselectiveblur",18750,75000,0,100000));
            add(new FilterObject("TiltShift","tiltshift",30000,60000,-20000,100000));
            add(new FilterObject("TiltShift Vertical","tiltshiftvertical",30000,60000,-20000,100000));
            add(new FilterObject("Opacity","opacity",100,100,0,100));
        }};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(staticView){
            menu.add(0,0,0,"Live").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }else{
            menu.add(0,0,0,"Static").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        staticView = !staticView;
        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }
}