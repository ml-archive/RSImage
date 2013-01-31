#pragma version(1)
#pragma rs java_package_name(com.caguilar.android.filters.scripts)


// Scene buffer
rs_allocation inTexture;


// Swirl effect parameters
float radius;
float angle;
float2 center;

void root(const uchar4 *v_in, uchar4 *v_out,const void *userData, uint32_t x, uint32_t y) {
  float2 textureCoordinate = {x,y};
  float2 textureCoordinateToUse = {x,y};
  float dist = distance(center, textureCoordinate);
  if (dist < radius)
  {
    textureCoordinateToUse -= center;
    float percent = (radius - dist) / radius;
    float theta = percent * percent * angle * 8.0;
    float s = sin(theta);
    float c = cos(theta);

    float2 temp = {c, -s};
    float2 temp2 = {s, c};

    float2 textureCoordinateToUseNew = {dot(textureCoordinateToUse,temp),dot(textureCoordinateToUse,temp2)};
    textureCoordinateToUse = textureCoordinateToUseNew;
    textureCoordinateToUse += center;
  }
  uchar4 *element;
  element = (uchar4 *)rsGetElementAt(inTexture, textureCoordinateToUse.x, textureCoordinateToUse.y);
  float4 color = rsUnpackColor8888(*element);
  *v_out = rsPackColorTo8888(color.r,color.g,color.b,color.a);
}


void filter(rs_script script,rs_allocation inAllocation,rs_allocation outAllocation){
    rsForEach(script, inAllocation, outAllocation);
}