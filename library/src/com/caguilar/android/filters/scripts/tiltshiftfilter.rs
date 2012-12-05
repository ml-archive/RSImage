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

#pragma version(1)
#pragma rs java_package_name(com.caguilar.android.filters.scripts)

rs_allocation blurTexture;
rs_allocation sharpTexture;
float topFocusLevel;
float bottomFocusLevel;
float focusFallOffRate;
int direction;

float imageWidth;
float imageHeight;

static float saturate(float x)
{
  return max(0.0f, min(1.0f, x));
}

static float smoothstepIMP(float a, float b, float x)
{
    float t = saturate((x - a)/(b - a));
    return t*t*(3.0 - (2.0*t));
}

void root(const uchar4 *v_in, uchar4 *v_out,const void *userData, uint32_t x, uint32_t y) {
    float4 apixel = rsUnpackColor8888(*v_in);

    uchar4 *element = (uchar4 *)rsGetElementAt(blurTexture, x, y);
    float3 blurColor = rsUnpackColor8888(*element).rgb;

    element = (uchar4 *)rsGetElementAt(sharpTexture, x, y);
    float3 sharpColor = rsUnpackColor8888(*element).rgb;

    float blurIntensity;

    if(direction==1){
        float realTopLevel = topFocusLevel*imageHeight;
        float realBottomFocusLevel = bottomFocusLevel*imageHeight;
        float realFocusFallOffRate = focusFallOffRate*imageHeight;

        blurIntensity = 1.0 - smoothstepIMP(realTopLevel - realFocusFallOffRate, realTopLevel, y);
        blurIntensity += smoothstepIMP(realBottomFocusLevel, realBottomFocusLevel + realFocusFallOffRate, y);
    }else{
        float realTopLevel = topFocusLevel*imageWidth;
        float realBottomFocusLevel = bottomFocusLevel*imageWidth;
        float realFocusFallOffRate = focusFallOffRate*imageWidth;
        blurIntensity = 1.0 - smoothstepIMP(realTopLevel - realFocusFallOffRate, realTopLevel, x);
        blurIntensity += smoothstepIMP(realBottomFocusLevel, realBottomFocusLevel + realFocusFallOffRate, x);
    }


    float3 newColor = mix(sharpColor, blurColor, blurIntensity);
    newColor = clamp(newColor,0.0f,1.0f);
    *v_out = rsPackColorTo8888(newColor.r,newColor.g,newColor.b,apixel.a);
}

void filter(rs_script script,rs_allocation inAllocation,rs_allocation outAllocation){
    rsForEach(script, inAllocation, outAllocation, 0, 0);
}