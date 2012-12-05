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

float saturationValue;
const static float3 luminanceWeighting = {0.2125, 0.7154, 0.0721};

void root(const uchar4 *v_in, uchar4 *v_out,const void *userData, uint32_t x, uint32_t y) {
    float4 apixel = rsUnpackColor8888(*v_in);
    float3 pixel = apixel.rgb;
    float luminance = dot(pixel, luminanceWeighting);
    float3 greyScaleColor = {luminance,luminance,luminance};
    float3 color = mix(greyScaleColor, pixel, saturationValue);
    color = clamp(color,0.0f,1.0f);
    *v_out = rsPackColorTo8888(color.r,color.g,color.b,apixel.a);
}

void filter(rs_script script,rs_allocation inAllocation,rs_allocation outAllocation){
    rsForEach(script, inAllocation, outAllocation, 0, 0);
}