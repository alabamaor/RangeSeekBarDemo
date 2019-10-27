package com.alabamaor.rangeseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;


public class RangeSeekBar extends View {

    private static final int DEFAULT_MARGIN = 100;
    private static final int DEFAULT_LINE_SIZE = 20;
    private static final int DEFAULT_INDICATOR_SIZE = 40;
    private static final float INDICATOR_SPACE = 0;


    private int digitsAfterPoint;

    float lowRange;
    float highRange;

    Context context;

    Canvas canvas;
    int screenWidth;
    int screenHeight;
    Paint indicatorRightPaint;
    Paint indicatorLeftPaint;
    Paint linePaint;
    Paint linePaintRight;
    Paint linePaintLeft;
    Paint paint;


    int marginLeft;
    int marginRight;
    int marginTop;
    int marginBottom;


    boolean isSingleIndicator;


    String rightIndicatorText;
    String leftIndicatorText;

    int indicatorRightRadius;
    int indicatorLeftRadius;

    float indicatorRightPositionX;
    float indicatorLeftPositionX;

    int lineSize;
    float linePositionStartX;
    float linePositionStartY;
    float linePositionEndX;
    float linePositionEndY;


    private boolean pressLeftIndicator;
    private boolean pressRightIndicator;
    private boolean isRightFirst;
    private float calcedRangeMovement;


    public RangeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomSeekBar,
                0, 0);

        try {
            isSingleIndicator = a.getBoolean(R.styleable.CustomSeekBar_setIndicator, true);

            leftIndicatorText = a.getString(R.styleable.CustomSeekBar_setIndicatorTextLeft);
            rightIndicatorText = a.getString(R.styleable.CustomSeekBar_setIndicatorTextRight);

            indicatorLeftRadius = a.getInt(R.styleable.CustomSeekBar_setIndicatorLeftRadius, DEFAULT_INDICATOR_SIZE);
            indicatorRightRadius = a.getInt(R.styleable.CustomSeekBar_setIndicatorRightRadius, DEFAULT_INDICATOR_SIZE);

            lineSize = a.getInt(R.styleable.CustomSeekBar_setLineSize, DEFAULT_LINE_SIZE);

            marginBottom = a.getInt(R.styleable.CustomSeekBar_setMarginBottom, DEFAULT_MARGIN);
            marginTop = a.getInt(R.styleable.CustomSeekBar_setMarginTop, DEFAULT_MARGIN);
            marginRight = a.getInt(R.styleable.CustomSeekBar_setMarginRight, DEFAULT_MARGIN);
            marginLeft = a.getInt(R.styleable.CustomSeekBar_setMarginLeft, DEFAULT_MARGIN);


            lowRange = a.getFloat(R.styleable.CustomSeekBar_setLowerRange, 0);
            highRange = a.getFloat(R.styleable.CustomSeekBar_setHighRange, 100);
            digitsAfterPoint = a.getInt(R.styleable.CustomSeekBar_setDigitsAfterPoint, 1);

//            indicatorRightPositionX = a.getFloat(R.styleable.CustomSeekBar_indicatorRightPositionX, 100);
//            indicatorRightPositionY = a.getFloat(R.styleable.CustomSeekBar_indicatorRightPositionY, 100);
//            indicatorLeftPositionX = a.getFloat(R.styleable.CustomSeekBar_indicatorLeftPositionX, 100);
//            indicatorLeftPositionY = a.getFloat(R.styleable.CustomSeekBar_indicatorLeftPositionY, 100);
        } finally {
            a.recycle();
        }
        init();
    }


    private void init(){

        pressLeftIndicator = false;
        pressRightIndicator = false;



        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);

        indicatorRightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorRightPaint.setColor(Color.GREEN);

        indicatorLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorLeftPaint.setColor(Color.BLUE);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.RED);

        linePaintRight = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaintRight.setColor(Color.YELLOW);
        linePaintLeft = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaintLeft.setColor(Color.GRAY);


    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        screenHeight = MeasureSpec.getSize(heightMeasureSpec);


        int indicatorBiggerSize = isBigger(indicatorLeftRadius, indicatorRightRadius);
        int indicatorOrLineBiggerSize = isBigger(indicatorBiggerSize, lineSize);
        int measureHeight = indicatorOrLineBiggerSize + marginBottom+marginTop;

        setMeasuredDimension(widthMeasureSpec, measureHeight);

        indicatorLeftPositionX = marginLeft + 100;
        indicatorRightPositionX = screenWidth - marginRight - 100;


        linePositionStartX = marginLeft;
        linePositionStartY = (measureHeight/2) - (lineSize/2);
        linePositionEndX = screenWidth - marginRight;
        linePositionEndY = (measureHeight/2) + (lineSize/2);

        calcedRangeMovement = calcRangeMovement();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.canvas = canvas;
//        canvas.drawColor(context.getResources().getColor(R.color.colorAccent));
//
//        RectF rect = new RectF(10, canvas.getHeight()/3 + 10,
//                canvas.getWidth()-10, canvas.getHeight()/3 - 10);
//
        RectF rect = new RectF(linePositionStartX, linePositionStartY,
                linePositionEndX, linePositionEndY);


        RectF rectRight = new RectF(linePositionStartX, linePositionStartY,
                indicatorRightPositionX, linePositionEndY);

        RectF rectLeft = new RectF(linePositionStartX, linePositionStartY,
                indicatorLeftPositionX, linePositionEndY);

        canvas.drawRoundRect(rect, 30, 30, linePaint);
        canvas.drawRoundRect(rectRight, 30, 30, linePaintRight);
        canvas.drawRoundRect(rectLeft, 30, 30, linePaintLeft);


            canvas.drawCircle(indicatorLeftPositionX, rect.centerY(), indicatorLeftRadius, indicatorLeftPaint);
            canvas.drawCircle(indicatorRightPositionX, rect.centerY(), indicatorRightRadius, indicatorRightPaint);



        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
//
//
//        RectF textRect = new RectF(rect.centerX()-150 , rect.centerY() - 140,
//                rect.centerX() , rect.centerY()- 50);
//
//        canvas.drawRoundRect(textRect, 10, 10, paint);
//
//
//        canvas.drawText(rightIndicatorText, (rect.centerX()-130) , rect.centerY() - 100, textPaint);
//        canvas.drawText(leftIndicatorText, (rect.centerX() +30) , rect.centerY() - 100, textPaint);
//
        canvas.drawText(leftIndicatorText, (rect.centerX() -130) , rect.centerY() + 100, textPaint);
        canvas.drawText(rightIndicatorText, (rect.centerX()+30) , rect.centerY() + 100, textPaint);

    }
//
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                if (x >= indicatorLeftPositionX - INDICATOR_SPACE && x < indicatorLeftPositionX+indicatorLeftRadius + INDICATOR_SPACE) {
                    pressLeftIndicator = true;
                    isRightFirst = false;
                }
                 else if (x >= indicatorRightPositionX - INDICATOR_SPACE && x < indicatorRightPositionX+indicatorRightRadius + INDICATOR_SPACE) {
                    pressRightIndicator = true;
                    isRightFirst = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (pressLeftIndicator)
                moveIndicatorLeft(x,  indicatorLeftRadius);
                else if (pressRightIndicator)
                moveIndicatorRight(x, indicatorLeftRadius);
                break;
            case MotionEvent.ACTION_UP:
                if (pressLeftIndicator)
                    pressLeftIndicator = false;
                if (pressRightIndicator)
                    pressRightIndicator = false;
                break;
        }

        return true;
    }

    public int isBigger(int a, int b){
        return a>b ? a:b;
    }


    public void moveIndicatorLeft(float leftX, int radius)
    {

        if (leftX > indicatorRightPositionX-indicatorRightRadius/3){
        indicatorLeftPositionX = indicatorRightPositionX-indicatorRightRadius/3;
    }
        else if (leftX < linePositionStartX){
            indicatorLeftPositionX = marginLeft;
        }
        else if (leftX > linePositionEndX){
            indicatorLeftPositionX = screenWidth - marginRight;
        }
        else{
            indicatorLeftPositionX = leftX;
        }
        indicatorLeftRadius = radius;

        leftIndicatorText  = String.valueOf(getFormatDigitsAfterPoint().format(calcRangeMovement(indicatorLeftPositionX)));
        invalidate();
    }
    public void moveIndicatorRight(float leftX, int radius)
    {

        if (leftX < indicatorLeftPositionX + indicatorLeftRadius/3){
        indicatorRightPositionX = indicatorLeftPositionX + indicatorLeftRadius/3;
    }
        else if (leftX < linePositionStartX){
            indicatorRightPositionX = marginLeft;
        }
        else if (leftX > linePositionEndX){
            indicatorRightPositionX = screenWidth - marginRight;
        }
        else{
            indicatorRightPositionX = leftX;
        }
        indicatorRightRadius = radius;

        rightIndicatorText = String.valueOf(getFormatDigitsAfterPoint().format(calcRangeMovement(indicatorRightPositionX)));
        invalidate();
    }

    public float calcRangeMovement(){

        float calcRange = highRange - lowRange;
        float calcLine = linePositionEndX - linePositionStartX;
        return calcLine / calcRange;
    }

    public float calcRangeMovement(float position){

        return (position-marginRight) / calcedRangeMovement;
    }

    public DecimalFormat getFormatDigitsAfterPoint(){
        switch (digitsAfterPoint){

            case 0:
                return new DecimalFormat("##");
            case 1:
                return new DecimalFormat("##.#");
            case 3:
                return new DecimalFormat("##.###");
            case 4:
                return new DecimalFormat("##.####");
                default:
                return new DecimalFormat("##.##");

        }

    }
}
