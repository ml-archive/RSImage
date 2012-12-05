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

rs_allocation toneTexture;

void root(const uchar4 *v_in, uchar4 *v_out,const void *userData, uint32_t x, uint32_t y) {
    float4 apixel = rsUnpackColor8888(*v_in);
    float3 pixel = apixel.rgb;

    uchar4 *element = (uchar4 *)rsGetElementAt(toneTexture, floor(pixel.r*255.0f));
    float Rpixel = rsUnpackColor8888(*element).r;

    element = (uchar4 *)rsGetElementAt(toneTexture, floor(pixel.g*255.0f));
    float Gpixel = rsUnpackColor8888(*element).g;

    element = (uchar4 *)rsGetElementAt(toneTexture, floor(pixel.b*255.0f));
    float Bpixel = rsUnpackColor8888(*element).b;

    *v_out = rsPackColorTo8888(Rpixel,Gpixel,Bpixel,apixel.a);
}

void filter(rs_script script,rs_allocation inAllocation,rs_allocation outAllocation){
    rsForEach(script, inAllocation, outAllocation, 0, 0);
}