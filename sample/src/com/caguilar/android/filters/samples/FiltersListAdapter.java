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

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FiltersListAdapter extends BaseAdapter {
	/*-------------------------- Fields --------------------------*/

	private ArrayList<FilterObject> list = new ArrayList<FilterObject>();
	private int layout;

	/*-------------------------- Public --------------------------*/

	public FiltersListAdapter(int inLayout,ArrayList<FilterObject> inList) {
		layout = inLayout;
        list = inList;
	}

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LinearLayout linear;
        TextView modName;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(layout, parent, false);
            linear = (LinearLayout) v.findViewById(R.id.listchild);

            linear.setOrientation(LinearLayout.VERTICAL);
            modName  = (TextView) linear.findViewById(R.id.actname);
            modName.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
            modName.setPadding((int)(12.0f*context.getResources().getDisplayMetrics().density),0,0,0);
        }else{
            linear = (LinearLayout)convertView;
            modName  = (TextView) linear.findViewById(R.id.actname);
        }
        modName.setText(list.get(position).getName());
        return linear;
    }
}