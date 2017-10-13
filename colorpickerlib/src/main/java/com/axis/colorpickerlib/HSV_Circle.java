package com.axis.colorpickerlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by DDA_Admin on 5/5/16.
 */
public class HSV_Circle extends View {

    int offset=(int) getResources().getDimension(R.dimen.offset);
    int CPT=(int) getResources().getDimension(R.dimen.ColorPreviewSetTop);
    int CPB=(int) getResources().getDimension(R.dimen.ColorPreviewSetBottom);
    int CPL=(int) getResources().getDimension(R.dimen.ColorPreviewSetLeft);
    int CPR=(int) getResources().getDimension(R.dimen.ColorPreviewSetRight);
    int rr=(int) getResources().getDimension(R.dimen.ColorPreviewRadious);

    int CDT=(int) getResources().getDimension(R.dimen.ColorDarknessSetTop);
    int CDB=(int) getResources().getDimension(R.dimen.ColorDarknessSetBottom);

    int VPS =(int)getResources().getDimension(R.dimen.ValuePointerSpace);
    int VPW =(int)getResources().getDimension(R.dimen.ValuePointerWidth);
    int VPH =(int)getResources().getDimension(R.dimen.ValuePointerHeight);
    int r2 =(int)getResources().getDimension(R.dimen.ValuePointerRadious);

    float valuePoiterX;


    ColorCallBack callBack;
    TouchCallback touchCallback;
    float pointerRadiusFacter=0.09f;
    String hexColor="#ffffff";

    float computedH=0;
    float computedS=0;
    float computedV=0;

    float facter;



    /**
     * Customizable display parameters (in percents)
     */
    private final int paramOuterPadding = 2;
    private final int paramInnerPadding = 5;
    private final int paramValueSliderWidth = 10;
    private final int paramArrowPointerSize = 4;

    private Paint colorWheelPaint;
    private Paint valueSliderPaint;
    private Paint valueSliderPaintStroke;

    private Paint colorViewPaint;
    private Paint colorViewPaintStroke;

    private Paint colorPointerPaint;
    private RectF colorPointerCoords;

    private Paint valuePointerPaint;
    private Paint valuePointerArrowPaint;

    private RectF outerWheelRect;
    private RectF innerWheelRect;

    private Path colorViewPath;
    private Path valueSliderPath;

    private Bitmap colorWheelBitmap;

    private int valueSliderWidth;
    private int innerPadding;
    private int outerPadding;

    private int arrowPointerSize;
    private int outerWheelRadius;
    private int innerWheelRadius;
    private int colorWheelRadius;


    /** Currently selected color */
    private float[] colorHSV = new float[] { 0f, 0f, 1f };

    public HSV_Circle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public HSV_Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HSV_Circle(Context context) {
        super(context);
        init();
    }

    private void init() {

        colorPointerPaint = new Paint();
        colorPointerPaint.setStyle(Paint.Style.STROKE);
        colorPointerPaint.setStrokeWidth(4);
        colorPointerPaint.setARGB(128, 0, 0, 0);
        colorPointerPaint.setAntiAlias(true);

        valuePointerPaint = new Paint();
        valuePointerPaint.setAntiAlias(true);

        valuePointerArrowPaint = new Paint();

        colorWheelPaint = new Paint();
        colorWheelPaint.setAntiAlias(true);
        colorWheelPaint.setDither(true);

        valueSliderPaint = new Paint();
        valueSliderPaint.setAntiAlias(true);
        valueSliderPaint.setDither(true);

        valueSliderPaintStroke = new Paint();
        valueSliderPaintStroke.setAntiAlias(true);

        colorViewPaint = new Paint();
        colorViewPaint.setAntiAlias(true);

        colorViewPaintStroke = new Paint();
        colorViewPaintStroke.setStyle(Paint.Style.STROKE);
        colorViewPaintStroke.setStrokeWidth(1);
        colorViewPaintStroke.setAntiAlias(true);

        colorViewPath = new Path();
        valueSliderPath = new Path();


        outerWheelRect = new RectF();
        innerWheelRect = new RectF();

        colorPointerCoords = new RectF();

    }
    public void setrecentColor(String hash){
        Color.colorToHSV(Color.parseColor(hash), colorHSV);
        hexColor = String.format("#%06X", (0xFFFFFF & Color.HSVToColor(colorHSV)));
        callBack.setColor(hexColor);

        invalidate();
    }

    public void setHashColor(String hash){
        Color.colorToHSV(Color.parseColor(hash), colorHSV);
        hexColor = String.format("#%06X", (0xFFFFFF & Color.HSVToColor(colorHSV)));
        callBack.setColor(hexColor);
        invalidate();

    }


    public  void setColoronTouch(){


        hexColor = String.format("#%06X", (0xFFFFFF & Color.HSVToColor(colorHSV)));
        callBack.setColor(hexColor);//set selected colr to main activity
        invalidate();

    }

    /*public  void setDarkness(float facter){
        darkFacter=facter;
        callBack.setColor(hexColor);//set selected colr to main activity
        colorHSV[2] =  darkFacter;
        hexColor = String.format("#%06X", (0xFFFFFF & Color.HSVToColor(colorHSV)));
        invalidate();
    }*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(widthSize, heightSize);
        setMeasuredDimension(widthSize, heightSize);
    }


    @Override
    protected void onDraw(Canvas canvas) {



        int centerX = (getWidth() / 2)-offset;
        int centerY = getHeight() / 2;

        // drawing color wheel

        canvas.drawBitmap(colorWheelBitmap, centerX - colorWheelRadius, centerY - colorWheelRadius, null);

        // drawing color view

        colorViewPaint.setColor(Color.parseColor(hexColor));
        canvas.drawRoundRect(new RectF(centerX + colorWheelRadius + CPL, centerY - CPT, centerX + colorWheelRadius + CPR, centerY - CPB), rr, rr, colorViewPaint);

        // drawing color view border
        colorViewPaintStroke.setColor( Color.parseColor("#8c000000"));
        canvas.drawRoundRect(new RectF(centerX + colorWheelRadius + CPL, centerY - CPT, centerX + colorWheelRadius + CPR, centerY - CPB), rr, rr, colorViewPaintStroke);

        // drawing value slider

        float[] hsv = new float[] { colorHSV[0], colorHSV[1], 1f };

        //set gradient=============================
        Shader shader = new LinearGradient(centerX+colorWheelRadius+CPL, centerY, centerX+colorWheelRadius+CPR, centerY, Color.HSVToColor(hsv), Color.BLACK, Shader.TileMode.CLAMP);
        valueSliderPaint.setShader(shader);

        canvas.drawRoundRect(new RectF(centerX+colorWheelRadius+CPL, centerY+CDT, centerX+colorWheelRadius+CPR, centerY+CDB), rr, rr, valueSliderPaint);


        // drawing color wheel pointer

        float hueAngle = (float) Math.toRadians(colorHSV[0]);
        int colorPointX = (int) (-Math.cos(hueAngle) * colorHSV[1] * colorWheelRadius) + centerX;
        int colorPointY = (int) (-Math.sin(hueAngle) * colorHSV[1] * colorWheelRadius) + centerY;

        float pointerRadius = pointerRadiusFacter * colorWheelRadius;
        int pointerX = (int) (colorPointX - pointerRadius / 2);
        int pointerY = (int) (colorPointY - pointerRadius / 2);

        //pointer==================================
        colorPointerCoords.set(pointerX, pointerY, pointerX + pointerRadius, pointerY + pointerRadius);
        canvas.drawOval(colorPointerCoords, colorPointerPaint);

        // drawing value pointer

        valuePointerPaint.setColor(Color.WHITE);
        //canvas.drawLine(centerX+colorWheelRadius+CPL+valuePoiterX+20, centerY+CDT+20, centerX+colorWheelRadius+CPL+valuePoiterX-20, centerY+CDB-20, valuePointerPaint);
        canvas.drawRoundRect(new RectF(centerX+colorWheelRadius+CPL+valuePoiterX+VPS, centerY+CDT+VPH, centerX+colorWheelRadius+CPL+valuePoiterX+VPW, centerY+CDB-VPH), r2, r2, valuePointerPaint);

    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {

        int centerX = width / 2;
        int centerY = height / 2;

        innerPadding = (int) (paramInnerPadding * width / 100);
        outerPadding = (int) (paramOuterPadding * width / 100);
        arrowPointerSize = (int) (paramArrowPointerSize * width / 100);
        valueSliderWidth = (int) (paramValueSliderWidth * width / 100);

        outerWheelRadius = width / 2 - outerPadding - arrowPointerSize;
        innerWheelRadius = outerWheelRadius - valueSliderWidth;
        colorWheelRadius = innerWheelRadius - innerPadding;

        outerWheelRect.set(centerX - outerWheelRadius, centerY - outerWheelRadius, centerX + outerWheelRadius, centerY + outerWheelRadius);
        innerWheelRect.set(centerX - innerWheelRadius, centerY - innerWheelRadius, centerX + innerWheelRadius, centerY + innerWheelRadius);

        colorWheelBitmap = createColorWheelBitmap(colorWheelRadius * 2, colorWheelRadius * 2);

    }

    private Bitmap createColorWheelBitmap(int width, int height) {

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int colorCount = 12;
        int colorAngleStep = 360 / 12;
        int colors[] = new int[colorCount + 1];
        float hsv[] = new float[] { 0f, 1f, 1f };
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = (i * colorAngleStep + 180) % 360;
            colors[i] = Color.HSVToColor(hsv);
        }
        colors[colorCount] = colors[0];

        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, null);
        RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, colorWheelRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);

        colorWheelPaint.setShader(composeShader);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width / 2, height / 2, colorWheelRadius, colorWheelPaint);

        return bitmap;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(touchCallback!=null) {
            touchCallback.OnTouchEvent(event);
        }
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:


                int x = (int) event.getX()+offset;
                int y = (int) event.getY();
                int cx = x - getWidth() / 2;
                int cy = y - (getHeight() / 2);
                double d = Math.sqrt(cx * cx + cy * cy);

                int width=CPR-CPL-VPS-VPW;
                float touchX=x-((getWidth() / 2)+colorWheelRadius+CPL);

                float touch=touchX/width;
                facter=1-touch;




                if (d <= colorWheelRadius) {


                    colorHSV[0] = (float) (Math.toDegrees(Math.atan2(cy, cx)) + 180f);
                    colorHSV[1] = Math.max(0f, Math.min(1f, (float) (d / colorWheelRadius)));
                    if(facter!=1f) {
                        colorHSV[2] = 1f;
                        valuePoiterX=0;
                    }

                    invalidate();
                    setColoronTouch();

                }

                else if (x >= (getWidth() / 2)+colorWheelRadius+CPL && x <= ( getWidth() / 2 )+colorWheelRadius+CPR -VPS-VPW && y>=(getHeight()/2)) {
                    valuePoiterX=touchX;

                    colorHSV[2]=facter;


                    invalidate();
                    setColoronTouch();
                }

                // callBack.setColor(finalColor);
            case MotionEvent.ACTION_UP:


                return true;
        }
        return super.onTouchEvent(event);
    }


    public void setCallBack(ColorCallBack call) {
        this.callBack = call;
    }
    public void setTouchCallback(TouchCallback touchCallback) {
        this.touchCallback = touchCallback;
    }

/*    public void rgb2hsv (float r,float g, float b) {


        r=r/255; g=g/255; b=b/255;
        float minRGB = Math.min(r,Math.min(g,b));
        float maxRGB = Math.max(r,Math.max(g,b));

        // Black-gray-white
        if (minRGB==maxRGB) {
            computedV = minRGB;

        }

        // Colors other than black-gray-white:
        float d = (r==minRGB) ? g-b : ((b==minRGB) ? r-g : b-r);
        float h = (r==minRGB) ? 3 : ((b==minRGB) ? 1 : 5);
        computedH = 60*(h - d/(maxRGB - minRGB));
        computedS = (maxRGB - minRGB)/maxRGB;
        computedV = maxRGB;
    }*/


}
