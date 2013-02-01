#pragma version(1)
#pragma rs java_package_name(com.caguilar.android.filters.scripts)

rs_allocation inTexture;
 
float2 center;
float2 pixelSize;
float2 dim;

void root(const uchar4 *v_in, uchar4 *v_out,const void *userData, uint32_t x, uint32_t y) {
    float2 textureCoordinate = {x,y};

    float2 m = {2.0, 2.0};
    float2 b = {1.0, 1.0};
    float2 h = {0.5, 0.5};
    float2 normCoord  = ((textureCoordinate / dim) * m) - b;
    float2 normCenter = (( center / dim ) * m) - b;

    normCoord -= normCenter;

    float r = length(normCoord); // to polar coords
    float phi = atan2(normCoord.y,normCoord.x); // to polar coords

    r = r - fmod(r, pixelSize.x) + 0.03;
    phi = phi - fmod(phi, pixelSize.y);


        normCoord.x = r * cos(phi);
        normCoord.y = r * sin(phi);



    normCoord += normCenter;


    float2 textureCoordinateToUse = normCoord  / m + h;

    //rsDebug("PolarPixellate: normCoord", textureCoordinateToUse.x, textureCoordinateToUse.y, phi);

    textureCoordinateToUse *= dim;

    uchar4 *element;

    int xUse = textureCoordinateToUse.x;
    int yUse = textureCoordinateToUse.y;
    float4 color;
    if((xUse>-1 && xUse<dim.x) && (yUse>-1 && yUse<dim.y) ){
        element = (uchar4 *)rsGetElementAt(inTexture, xUse, yUse);
        color = rsUnpackColor8888(*element);
    }else{
        color = rsUnpackColor8888(*v_in);
    }

    *v_out = rsPackColorTo8888(color.r,color.g,color.b,color.a);
}

void filter(rs_script script,rs_allocation inAllocation,rs_allocation outAllocation){
 rsForEach(script, inAllocation, outAllocation);
}