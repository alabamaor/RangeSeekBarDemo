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


public class RangeSeekBar extends View {

    private static final int DEFAULT_MARGIN = 100;
    private static final int DEFAULT_LINE_SIZE = 20;
    private static final int DEFAULT_INDICATOR_SIZE = 40;
    private static final float INDICATOR_SPACE = 40;
    Context context;

    Canvas canvas;
    int screenWidth;
    int screenHeight;
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

        canvas.drawRoundRect(rect, 30, 30, paint);

        canvas.drawCircle(indicatorLeftPositionX, rect.centerY(), indicatorLeftRadius, paint);
        canvas.drawCircle(indicatorRightPositionX, rect.centerY(), indicatorRightRadius, paint);


//        Paint textPaint = new Paint();
//        textPaint.setColor(Color.WHITE);
//        textPaint.setStyle(Paint.Style.FILL);
//
//        textPaint.setColor(Color.BLACK);
//        textPaint.setTextSize(24);
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
//        canvas.drawText(rightIndicatorText, (rect.centerX()-130) , rect.centerY() + 100, textPaint);
//        canvas.drawText(leftIndicatorText, (rect.centerX() +30) , rect.centerY() + 100, textPaint);

    }
//
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
//        event.getAction() == MotionEvent.ACTION_DOWN ||


        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                if (x >= indicatorLeftPositionX - INDICATOR_SPACE && x < indicatorLeftPositionX+indicatorLeftRadius + INDICATOR_SPACE) {
                    pressLeftIndicator = true;
                }
                if (x >= indicatorRightPositionX - INDICATOR_SPACE && x < indicatorRightPositionX+indicatorRightRadius + INDICATOR_SPACE) {
                    pressRightIndicator = true;
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

        if (leftX > indicatorRightPositionX){
        indicatorLeftPositionX = indicatorRightPositionX;
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

        invalidate();
    }
    public void moveIndicatorRight(float leftX, int radius)
    {

        if (leftX < indicatorLeftPositionX){
        indicatorRightPositionX = indicatorLeftPositionX;
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

        invalidate();
    }

}
