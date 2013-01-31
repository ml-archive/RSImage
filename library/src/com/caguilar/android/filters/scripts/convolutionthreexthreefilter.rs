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

float* convolutionKernel;
float  kernelSum;
uint32_t imageWidth;
uint32_t imageHeight;

static uchar4 convolve_3x3k( const uchar4* neighborhood, const float* kernel, float sum );

void root(const uchar4 *v_in, uchar4 *v_out,const void *userData, uint32_t x, uint32_t y) {
    float4 apixel = rsUnpackColor8888(*v_in);

    if( x > 0 && x < imageWidth-1 && y > 0 && y < imageHeight-1 )
    {
        uchar4 neighborhood[9] = { *(uchar4* )rsGetElementAt(inTexture, x-1, y-1),
                                 *(uchar4* )rsGetElementAt(inTexture, x, y-1),
                                 *(uchar4* )rsGetElementAt(inTexture, x+1, y-1),
                                 *(uchar4* )rsGetElementAt(inTexture, x-1, y),
                                 *(uchar4* )rsGetElementAt(inTexture, x, y),
                                 *(uchar4* )rsGetElementAt(inTexture, x+1, y),
                                 *(uchar4* )rsGetElementAt(inTexture, x-1, y+1),
                                 *(uchar4* )rsGetElementAt(inTexture, x, y+1),
                                 *(uchar4* )rsGetElementAt(inTexture, x+1, y+1)
                               };
        
        *v_out = convolve_3x3k( neighborhood, convolutionKernel, kernelSum );
       
    }
    else
        *v_out = rsPackColorTo8888(apixel);
}

void filter(rs_script script,rs_allocation inAllocation,rs_allocation outAllocation){
    rsForEach(script, inAllocation, outAllocation, 0, 0);
}

static uchar4 convolve_3x3k( const uchar4* neighborhood, const float* kernel, float sum )
{
    float4 total = {0.0f,0.0f,0.0f,0.0f};

    float alpha = rsUnpackColor8888(*(neighborhood+4)).a;

    float4 s = { sum, sum, sum, 1};
    for(int i = 0; i < 9; i++ )
    {
        float4 c = rsUnpackColor8888(*(neighborhood+i));
        float4 k = {*(kernel+i),*(kernel+i),*(kernel+i),1};

        total.rgb += (*(kernel+i)*c).rgb;
        //total.rgb += (k*c).rgb;

    }

    float4 newValue = total;

    newValue = clamp(newValue,0.0f,1.0f);

    return rsPackColorTo8888( newValue.r, newValue.g, newValue.b, alpha );
}
