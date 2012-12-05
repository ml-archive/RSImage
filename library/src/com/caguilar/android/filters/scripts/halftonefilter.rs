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

float fractionalWidthOfAPixel;
rs_allocation inTexture;
float aspectRatio = 0.75f;
const static float3 W = {0.2125, 0.7154, 0.0721};

void root(const uchar4 *v_in, uchar4 *v_out,const void *userData, uint32_t x, uint32_t y) {

    float2 sampleDivisor = {fractionalWidthOfAPixel, fractionalWidthOfAPixel / aspectRatio};
    float xpos = x;
    float ypos = y;
    float2 textureCoordinate = {xpos,ypos};
    float2 samplePos = textureCoordinate - fmod(textureCoordinate, sampleDivisor) + 0.5f * sampleDivisor;
    float2 textureCoordinateToUse = {textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5f - 0.5f * aspectRatio)};
    float2 adjustedSamplePos = {samplePos.x, (samplePos.y * aspectRatio + 0.5f - 0.5f * aspectRatio)};
    float distanceFromSamplePoint = distance(adjustedSamplePos, textureCoordinateToUse);

    const uchar4 *element = rsGetElementAt(inTexture, samplePos.x, samplePos.y);
    float3 sampledColor = rsUnpackColor8888(*element).rgb;
    float dotScaling = 1.0f - dot(sampledColor, W);

    float checkForPresenceWithinDot = 1.0f - step(distanceFromSamplePoint, (fractionalWidthOfAPixel * 0.5f) * dotScaling);

    float3 color = {checkForPresenceWithinDot,checkForPresenceWithinDot,checkForPresenceWithinDot};
    *v_out = rsPackColorTo8888(color);
}

void filter(rs_script script,rs_allocation inAllocation,rs_allocation outAllocation){
    rsForEach(script, inAllocation, outAllocation, 0, 0);
}