package com.caguilar.android.filters.scripts;

import android.content.res.Resources;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: cesaraguilar
 * Date: 12/5/12
 * Time: 4:15 PM
 */
public class Yuv2Rgb extends ScriptC_yuv2rgb {
    RenderScript mRS;
    Allocation yuvAllocation;
    public Yuv2Rgb(RenderScript rs) {
        super(rs);
        mRS = rs;
    }

    public Yuv2Rgb(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        mRS = rs;
    }

    public void convert(byte[] mYUVData, int imageWidth, int imageHeight, Allocation mOutAllocation) {
        set_imageHeight(imageHeight);
        set_imageWidth(imageWidth);
        try{
            yuvAllocation.copyFrom(mYUVData);
        }catch (Throwable t){
            yuvAllocation = Allocation.createSized(mRS, Element.I8(mRS), mYUVData.length);
            yuvAllocation.copyFrom(mYUVData);
        }
        set_yuvData(yuvAllocation);
        invoke_convert(this,mOutAllocation,mOutAllocation);
    }
}
