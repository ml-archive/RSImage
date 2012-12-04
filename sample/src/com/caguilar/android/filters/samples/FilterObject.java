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


public class FilterObject {
    private String value;
    private float defaultValue;
    private float minusValue;
    private float maxValue;
    private float divisorValue;
    private String name;

    public FilterObject(String name, String value, int defaultValue, int maxValue, int minusValue, int divisorValue) {
        this.value = value;
        this.name = name;
        this.defaultValue = defaultValue;
        this.maxValue = maxValue;
        this.minusValue = minusValue;
        this.divisorValue = divisorValue;

    }

    public String getValue() {
        return value;
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    public float getMinusValue() {
        return minusValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public float getDivisorValue() {
        return divisorValue;
    }

    public String getName() {
        return name;
    }
}
