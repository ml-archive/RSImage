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

package com.caguilar.android.filters.scripts;

import android.content.res.Resources;
import android.graphics.PointF;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: cesaraguilar
 * Date: 11/20/12
 * Time: 8:22 PM
 */
public class ToneCurveFilter extends ScriptC_tonecurvefilter {

    RenderScript mRS;
    ArrayList<PointF> _redControlPoints;
    ArrayList<PointF> _greenControlPoints;
    ArrayList<PointF> _blueControlPoints;
    ArrayList<PointF> _rgbCompositeControlPoints;

    ArrayList<Float> _redCurve;
    ArrayList<Float> _greenCurve;
    ArrayList<Float> _blueCurve;
    ArrayList<Float> _rgbCompositeCurve;

    public ToneCurveFilter(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        mRS = rs;
        ArrayList<PointF> defaultCurve = new ArrayList<PointF>(){{
            add(new PointF(0.0f,0.0f));
            add(new PointF(0.5f,0.5f));
            add(new PointF(1.0f,1.0f));
        }};

        _rgbCompositeControlPoints = new ArrayList<PointF>();
        _rgbCompositeControlPoints.addAll(defaultCurve);
        _rgbCompositeCurve = getPreparedSplineCurve(_rgbCompositeControlPoints);

        _redControlPoints = new ArrayList<PointF>();
        _redControlPoints.addAll(_rgbCompositeControlPoints);
        _redCurve = new ArrayList<Float>();
        _redCurve.addAll(_rgbCompositeCurve);

        _greenControlPoints = new ArrayList<PointF>();
        _greenControlPoints.addAll(_rgbCompositeControlPoints);
        _greenCurve = new ArrayList<Float>();
        _greenCurve.addAll(_rgbCompositeCurve);

        _blueControlPoints = new ArrayList<PointF>();
        _blueControlPoints.addAll(_rgbCompositeControlPoints);
        _blueCurve = new ArrayList<Float>();
        _blueCurve.addAll(_rgbCompositeCurve);

        //updateToneCurveTexture();
    }

    public void setRGBControlPoints(ArrayList<PointF> newValue){

        _redControlPoints.clear();
        _redControlPoints.addAll(newValue);
        _redCurve = getPreparedSplineCurve(_redControlPoints);

        _greenControlPoints.clear();
        _greenControlPoints.addAll(newValue);
        _greenCurve = getPreparedSplineCurve(_greenControlPoints);

        _blueControlPoints.clear();
        _blueControlPoints.addAll(newValue);
        _blueCurve = getPreparedSplineCurve(_blueControlPoints);
        //updateToneCurveTexture();
    }


    public void setRgbCompositeControlPoints(ArrayList<PointF> newValue)
    {
        _rgbCompositeControlPoints.clear();
        _rgbCompositeControlPoints.addAll(newValue);
        _rgbCompositeCurve = getPreparedSplineCurve(_rgbCompositeControlPoints);
        //updateToneCurveTexture();
    }

    public void setRedControlPoints(ArrayList<PointF> newValue)
    {
        _redControlPoints.clear();
        _redControlPoints.addAll(newValue);
        _redCurve = getPreparedSplineCurve(_redControlPoints);
//        updateToneCurveTexture();
    }

    public void setGreenControlPoints(ArrayList<PointF> newValue)
    {
        _greenControlPoints.clear();
        _greenControlPoints.addAll(newValue);
        _greenCurve = getPreparedSplineCurve(_greenControlPoints);
//        updateToneCurveTexture();
    }

    public void setBlueControlPoints(ArrayList<PointF> newValue)
    {
        _blueControlPoints.clear();
        _blueControlPoints.addAll(newValue);
        _blueCurve = getPreparedSplineCurve(_blueControlPoints);
//        updateToneCurveTexture();
    }

    public ArrayList<Float> getPreparedSplineCurve(ArrayList<PointF> points)
    {
        if (points!=null && points.size() > 0)
        {

            Collections.sort(points,new Comparator<PointF>() {
                @Override
                public int compare(PointF pointF, PointF pointF1) {
                    float x1 = pointF.x;
                    float x2 = pointF1.x;
                    return (int)(x1 - x2);
                }
            });

            // Convert from (0, 1) to (0, 255).
            ArrayList<PointF> convertedPoints = new ArrayList<PointF>();
            for (PointF point : points){
                point.x = point.x * 255;
                point.y = point.y * 255;
                convertedPoints.add(point);
            }


            ArrayList<PointF> splinePoints = splineCurve(convertedPoints);
            convertedPoints.clear();
            // If we have a first point like (0.3, 0) we'll be missing some points at the beginning
            // that should be 0.
            PointF firstSplinePoint = splinePoints.get(0);

            if (firstSplinePoint.x > 0) {
                for (int i=(int)firstSplinePoint.x; i >= 0; i--) {
                    PointF newCGPoint = new PointF((float)i, 0.0f);
                    splinePoints.add(0,newCGPoint);
                }
            }

            // Insert points similarly at the end, if necessary.
            PointF lastSplinePoint = splinePoints.get(splinePoints.size()-1);

            if (lastSplinePoint.x < 255) {
                for (int i = ((int)lastSplinePoint.x) + 1; i <= 255; i++) {
                    PointF newCGPoint = new PointF(i, 255);
                    splinePoints.add(newCGPoint);
                }
            }


            // Prepare the spline points.
            ArrayList<Float> preparedSplinePoints = new ArrayList<Float>();
            for (PointF newPoint : splinePoints)
            {
                PointF origPoint = new PointF(newPoint.x, newPoint.x);

                float distance = (float)Math.sqrt(Math.pow((origPoint.x - newPoint.x), 2.0) + Math.pow((origPoint.y - newPoint.y), 2.0));

                if (origPoint.y > newPoint.y)
                {
                    distance = -distance;
                }
                preparedSplinePoints.add(distance);
            }
            splinePoints.clear();
            return preparedSplinePoints;
        }

        return null;
    }

    public ArrayList<PointF> splineCurve(ArrayList<PointF> points)
    {
        ArrayList<Double> sdA = secondDerivative(points);

        // Is [points count] equal to [sdA count]?
//    int n = [points count];
        int n = sdA.size();
        double sd[] = new double[n];

        // From NSMutableArray to sd[n];
        for (int i=0; i<n; i++)
        {
            sd[i] = sdA.get(i);
        }


        ArrayList<PointF> output = new ArrayList<PointF>();

        for(int i=0; i<n-1 ; i++)
        {
            PointF cur = points.get(i);
            PointF next = points.get(i+1);

            for(int x=(int)cur.x;x<(int)next.x;x++)
            {
                double t = (double)(x-cur.x)/(next.x-cur.x);

                double a = 1-t;
                double b = t;
                double h = next.x-cur.x;

                double y= a*cur.y + b*next.y + (h*h/6)*( (a*a*a-a)*sd[i]+ (b*b*b-b)*sd[i+1] );

                if (y > 255.0)
                {
                    y = 255.0;
                }
                else if (y < 0.0)
                {
                    y = 0.0;
                }

                output.add(new PointF((float)x,(float)y));
            }
        }

        // If the last point is (255, 255) it doesn't get added.
        if (output.size() == 255) {
            output.add(points.get(points.size()-1));
    }
        return output;
    }


    public ArrayList<Double> secondDerivative(ArrayList<PointF> points)
    {
        int n = points.size();
        if ((n <= 0) || (n == 1))
        {
            return null;
        }

        double matrix[][] = new double[n][3];
        double result[] = new double[n];
        matrix[0][1]=1;
        // What about matrix[0][1] and matrix[0][0]? Assuming 0 for now (Brad L.)
        matrix[0][0]=0;
        matrix[0][2]=0;

        for(int i=1;i<n-1;i++)
        {
            PointF P1 = points.get(i-1);
            PointF P2 = points.get(i);
            PointF P3 = points.get(i+1);

            matrix[i][0]=(double)(P2.x-P1.x)/6;
            matrix[i][1]=(double)(P3.x-P1.x)/3;
            matrix[i][2]=(double)(P3.x-P2.x)/6;
            result[i]=(double)(P3.y-P2.y)/(P3.x-P2.x) - (double)(P2.y-P1.y)/(P2.x-P1.x);
        }

        // What about result[0] and result[n-1]? Assuming 0 for now (Brad L.)
        result[0] = 0;
        result[n-1] = 0;

        matrix[n-1][1]=1;
        // What about matrix[n-1][0] and matrix[n-1][2]? For now, assuming they are 0 (Brad L.)
        matrix[n-1][0]=0;
        matrix[n-1][2]=0;

        // solving pass1 (up->down)
        for(int i=1;i<n;i++)
        {
            double k = matrix[i][0]/matrix[i-1][1];
            matrix[i][1] -= k*matrix[i-1][2];
            matrix[i][0] = 0;
            result[i] -= k*result[i-1];
        }
        // solving pass2 (down->up)
        for(int i=n-2;i>=0;i--)
        {
            double k = matrix[i][2]/matrix[i+1][1];
            matrix[i][1] -= k*matrix[i+1][0];
            matrix[i][2] = 0;
            result[i] -= k*result[i+1];
        }

        double y2[] = new double[n];
        for(int i=0;i<n;i++) y2[i]=result[i]/matrix[i][1];

        ArrayList<Double> output = new ArrayList<Double>();
        for (int i=0;i<n;i++)
        {
            output.add(y2[i]);
        }

        return output;
    }

    public int[] toneCurveByteArray;
    private Allocation curveAllocation;

    public void updateToneCurveTexture()
    {
        if(toneCurveByteArray==null){
            toneCurveByteArray = new int[256];
            curveAllocation = Allocation.createSized(mRS, Element.I32(mRS),256);
        }

        if ( (_redCurve.size() >= 256) && (_greenCurve.size() >= 256) && (_blueCurve.size() >= 256) && (_rgbCompositeCurve.size() >= 256))
        {
            int r,g,b,a;

            for (int currentCurveIndex = 0; currentCurveIndex < 256; currentCurveIndex++)
            {
                // BGRA for upload to texture
                r = (int)Math.min(Math.max(currentCurveIndex + _blueCurve.get(currentCurveIndex) + _rgbCompositeCurve.get(currentCurveIndex), 0), 255);
                g = (int)Math.min(Math.max(currentCurveIndex + _greenCurve.get(currentCurveIndex) + _rgbCompositeCurve.get(currentCurveIndex), 0), 255);
                b = (int)Math.min(Math.max(currentCurveIndex + _redCurve.get(currentCurveIndex) + _rgbCompositeCurve.get(currentCurveIndex), 0), 255);
                a = 255;

                toneCurveByteArray[currentCurveIndex] = ((a&0x0ff)<<24)|((r&0x0ff)<<16)|((g&0x0ff)<<8)|((b&0x0ff));
            }

            curveAllocation.copyFrom(toneCurveByteArray);
            set_toneTexture(curveAllocation);
        }
    }
}
