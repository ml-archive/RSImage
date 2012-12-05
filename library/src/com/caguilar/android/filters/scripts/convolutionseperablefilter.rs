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

rs_allocation inTexture;
rs_allocation matrixTexture;
float2 XYOffset;
int matrixLenght;
int imageWidth;
int imageHeight;


void root(const uchar4 *v_in, uchar4 *v_out,const void *userData, uint32_t x, uint32_t y) {
    float4 apixel = rsUnpackColor8888(*v_in);

    float3 newColor = {0,0,0};
    uchar4 *element;
    float *matrixValue;

    float center = floor(matrixLenght/2.0f);
    float2 currentPosition = {x,y};
    float2 moveBy = {0,0};
    float2 newPosition = {0,0};
    float i=-center;
    float3 color = {0,0,0};

    for(i; i<=center; i++){
        moveBy = XYOffset*i;
        newPosition = currentPosition+moveBy;
        if((newPosition.x > -1 && newPosition.y >-1) && (newPosition.x < imageWidth && newPosition.y < imageHeight)){
            element = (uchar4 *)rsGetElementAt(inTexture, newPosition.x, newPosition.y);
            matrixValue = (float*)rsGetElementAt(matrixTexture,i+center);
            color = rsUnpackColor8888(*element).rgb;
            newColor += color*(*matrixValue);
        }
    }

    newColor = clamp(newColor,0.0f,1.0f);
    *v_out = rsPackColorTo8888(newColor.r,newColor.g,newColor.b,apixel.a);
}

void filter(rs_script script,rs_allocation inAllocation,rs_allocation outAllocation){
    rsForEach(script, inAllocation, outAllocation, 0, 0);
}