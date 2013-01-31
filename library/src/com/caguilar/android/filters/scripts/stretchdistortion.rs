#pragma version(1)
#pragma rs java_package_name(com.caguilar.android.filters.scripts)


static float saturate(float x)
{
  return max(0.0f, min(1.0f, x));
}

static float2 smoothstepIMP(float a, float b, float2 x)
{
    float satx = saturate((x.x - a)/(b - a));
    float saty = saturate((x.y - a)/(b - a));
    float2 t = { satx, saty };
    float2 three = { 3.0, 3.0 };
    float2 two   = { 2.0, 2.0 };
    return t*t*(three - (two*t));
}

rs_allocation inTexture;

float2 center;
float2 dim;

void root(const uchar4 *v_in, uchar4 *v_out,const void *userData, uint32_t x, uint32_t y) {
    float2 textureCoordinate = {x,y};

    float2 m = {2.0, 2.0};
    float2 b = {1.0, 1.0};
    float2 h = {0.5, 0.5};
    float2 normCoord  = ((textureCoordinate / dim) * m) - b;
    float2 normCenter = (( center / dim ) * m) - b;


    normCoord -= normCenter;
    float2 s  = sign(normCoord);

    normCoord = fabs(normCoord);
    normCoord = h * normCoord + h * smoothstepIMP(0.25, 0.5, normCoord) * normCoord;
    normCoord = s * normCoord;

    normCoord += normCenter;

    float2 textureCoordinateToUse = (normCoord / m + h)*dim;

    uchar4 *element;
    element = (uchar4 *)rsGetElementAt(inTexture, textureCoordinateToUse.x, textureCoordinateToUse.y);
    float4 color = rsUnpackColor8888(*element);
    *v_out = rsPackColorTo8888(color.r,color.g,color.b,color.a);

}

void filter(rs_script script,rs_allocation inAllocation,rs_allocation outAllocation){
 rsForEach(script, inAllocation, outAllocation);
}