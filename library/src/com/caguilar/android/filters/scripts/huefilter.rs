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

 float hueAdjust;
 const static  float4  kRGBToYPrime =  {0.299, 0.587, 0.114, 0.0};
 const static  float4  kRGBToI     = {0.595716, -0.274453, -0.321263, 0.0};
 const static  float4  kRGBToQ     = {0.211456, -0.522591, 0.31135, 0.0};

 const static  float4  kYIQToR   = {1.0, 0.9563, 0.6210, 0.0};
 const static  float4  kYIQToG   = {1.0, -0.2721, -0.6474, 0.0};
 const static  float4  kYIQToB   = {1.0, -1.1070, 1.7046, 0.0};

void root(const uchar4 *v_in, uchar4 *v_out,const void *userData, uint32_t x, uint32_t y) {
    float4 pixel = rsUnpackColor8888(*v_in);

    float YPrime  = dot (pixel, kRGBToYPrime);
    float I      = dot (pixel, kRGBToI);
    float Q      = dot (pixel, kRGBToQ);

    float   hue     = atan2 (Q, I);
    float   chroma  = sqrt (I * I + Q * Q);

    hue += (-hueAdjust);

    Q = chroma * sin (hue);
    I = chroma * cos (hue);

    float4 yIQ   = {YPrime, I, Q, 0.0};
    pixel.r = dot (yIQ, kYIQToR);
    pixel.g = dot (yIQ, kYIQToG);
    pixel.b = dot (yIQ, kYIQToB);

    pixel = clamp(pixel,0.0f,1.0f);

    *v_out = rsPackColorTo8888(pixel);
}

void filter(rs_script script,rs_allocation inAllocation,rs_allocation outAllocation){
    rsForEach(script, inAllocation, outAllocation, 0, 0);
}