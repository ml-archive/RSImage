package com.caguilar.android.filters.scripts;

import android.content.res.Resources;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Script;

/**
 * User: Leonard Collins
 * Date: 1/23/13
 * Description:
 */
public class EmbossFilter extends ScriptC_convolutionfilter {
    float _intensity = 2.0f;
    float kernel[];
    Allocation kernelAllocation;

    public EmbossFilter(RenderScript rs) {
        super(rs);
        kernelAllocation = Allocation.createSized(rs, Element.F32(rs),9);
    }

    public EmbossFilter(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        kernelAllocation = Allocation.createSized(rs, Element.F32(rs),9);
        kernel = makeKernel();
    }

    public EmbossFilter(RenderScript rs, Resources resources, int id, float value) {
        super(rs, resources, id);
        _intensity = value;
        kernelAllocation = Allocation.createSized(rs, Element.F32(rs),9);
        kernel = makeKernel();
    }

    @Override
    public void invoke_filter(Script script,Allocation mInAllocation, Allocation mOutAllocation) {
        set_inTexture(mInAllocation);
        set_matrixTexture(kernelAllocation);
        super.invoke_filter(script, mInAllocation, mOutAllocation);
        set_inTexture(mOutAllocation);
        super.invoke_filter(script,mInAllocation, mOutAllocation);
    }

    public void setIntensity(float value) {
        _intensity = value;
        makeKernel();
    }

    public  float[] makeKernel(){

        float[] matrix = new float[9];

        matrix[0] = _intensity * (-2.0f);
        matrix[1] = -_intensity;
        matrix[2] = 0.0f;

        matrix[3] = -_intensity;
        matrix[4] = 1.0f;
        matrix[5] = _intensity;

        matrix[6] = 0.0f;
        matrix[7] = _intensity;
        matrix[8] = _intensity * 2.0f;

        int total = 0;
        for( int i = 0; i < 9; i++ )
            total += matrix[i];
        set_kernelSum(total);

        kernelAllocation.copyFrom(matrix);
        bind_convolutionKernel(kernelAllocation);

        return matrix;
    }
}
