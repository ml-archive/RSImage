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

const static float3 luminanceWeighting = {0.2125, 0.7154, 0.0721};
float intensityValue;
float3 filterColor;

void root(const uchar4 *v_in, uchar4 *v_out,const void *userData, uint32_t x, uint32_t y) {
    float4 apixel = rsUnpackColor8888(*v_in);
    float3 color = apixel.rgb;
    float luminance = dot(color, luminanceWeighting);
    float3 desat = {luminance,luminance,luminance};
    float3 outputColor = {
        (desat.r < 0.5f ? (2.0f * desat.r * filterColor.r) : (1.0f - 2.0f * (1.0 - desat.r) * (1.0f - filterColor.r))),
        (desat.g < 0.5f ? (2.0f * desat.g * filterColor.g) : (1.0f - 2.0f * (1.0 - desat.g) * (1.0f - filterColor.g))),
        (desat.b < 0.5f ? (2.0f * desat.b * filterColor.b) : (1.0f - 2.0f * (1.0 - desat.b) * (1.0f - filterColor.b))),
    };
    float3 newColor = mix(color, outputColor, intensityValue);
    newColor = clamp(newColor,0.0f,1.0f);
    *v_out = rsPackColorTo8888(newColor.r,newColor.g,newColor.b,apixel.a);
}

void filter(rs_script script,rs_allocation inAllocation,rs_allocation outAllocation){
    rsForEach(script, inAllocation, outAllocation, 0, 0);
}