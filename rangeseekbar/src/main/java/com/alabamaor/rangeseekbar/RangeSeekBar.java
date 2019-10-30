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

    private float lowRange;
    private float highRange;

    private Context context;

    private Canvas canvas;
    private int screenWidth;
    private int screenHeight;

    private RectF defaultLine;
    private RectF leftLine;
    private RectF rightLine;

    private Paint indicatorRightPaint;
    private Paint indicatorLeftPaint;
    private Paint linePaint;
    private Paint linePaintRight;
    private Paint linePaintLeft;
    private Paint paint;


    private int marginLeft;
    private int marginRight;
    private int marginTop;
    private int marginBottom;


    private boolean isSingleIndicator;


    private String rightIndicatorText;
    private String leftIndicatorText;

    private int indicatorRightRadius;
    private int indicatorLeftRadius;

    private float indicatorRightPositionX;
    private float indicatorLeftPositionX;

    private int lineSize;
    private float linePositionStartX;
    private float linePositionStartY;
    private float linePositionEndX;
    private float linePositionEndY;


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
            setSingleIndicator(a.getBoolean(R.styleable.CustomSeekBar_isSingleIndicator, false));

            setLeftIndicatorText(a.getString(R.styleable.CustomSeekBar_setIndicatorTextLeft));
            setRightIndicatorText(a.getString(R.styleable.CustomSeekBar_setIndicatorTextRight));

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

        defaultLine = new RectF();
        rightLine = new RectF();
        leftLine = new RectF();

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

        indicatorRightPositionX = isSingleIndicator ? linePositionEndX : screenWidth - marginRight - 100;


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

        defaultLine.set(linePositionStartX, linePositionStartY,
                linePositionEndX, linePositionEndY);

        rightLine.set(linePositionStartX, linePositionStartY,
                indicatorRightPositionX, linePositionEndY);

        leftLine.set(linePositionStartX, linePositionStartY,
                indicatorLeftPositionX, linePositionEndY);


        canvas.drawRoundRect(linePositionStartX, linePositionStartY,
                linePositionEndX, linePositionEndY, 30, 30, linePaint);

        canvas.drawRoundRect(rightLine, 30, 30, linePaintRight);

        if (!isSingleIndicator){
            canvas.drawRoundRect(leftLine, 30, 30, linePaintLeft);
        }

        canvas.drawCircle(indicatorLeftPositionX, defaultLine.centerY(), indicatorLeftRadius, indicatorLeftPaint);

        if (!isSingleIndicator) {
            canvas.drawCircle(indicatorRightPositionX, defaultLine.centerY(), indicatorRightRadius, indicatorRightPaint);
        }


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
        canvas.drawText(leftIndicatorText, indicatorLeftPositionX-indicatorLeftRadius/2, leftLine.centerY() + 100, textPaint);
        if (!isSingleIndicator) {
            canvas.drawText(rightIndicatorText, indicatorRightPositionX-indicatorRightRadius/2, defaultLine.centerY() + 100, textPaint);
        }

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
                 else if (x >= linePositionStartX && x<=linePositionEndX &&
                y>= linePositionStartY && y<=linePositionEndY){
                     if (Math.abs(x-indicatorRightPositionX) < Math.abs(x-indicatorLeftPositionX)){
                         moveIndicatorRight(x, indicatorRightRadius);
                         pressRightIndicator = true;
                    }
                     else{
                         moveIndicatorLeft(x, indicatorLeftRadius);
                         pressLeftIndicator = true;
                     }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (pressLeftIndicator)
                moveIndicatorLeft(x,  indicatorLeftRadius);
                else if (pressRightIndicator)
                moveIndicatorRight(x, indicatorRightRadius);
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


















    public int getDigitsAfterPoint() {
        return digitsAfterPoint;
    }

    public void setDigitsAfterPoint(int digitsAfterPoint) {
        this.digitsAfterPoint = digitsAfterPoint;
    }

    public float getLowRange() {
        return lowRange;
    }

    public void setLowRange(float lowRange) {
        this.lowRange = lowRange;
    }

    public float getHighRange() {
        return highRange;
    }

    public void setHighRange(float highRange) {
        this.highRange = highRange;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public Paint getIndicatorRightPaint() {
        return indicatorRightPaint;
    }

    public void setIndicatorRightPaint(Paint indicatorRightPaint) {
        this.indicatorRightPaint = indicatorRightPaint;
    }

    public Paint getIndicatorLeftPaint() {
        return indicatorLeftPaint;
    }

    public void setIndicatorLeftPaint(Paint indicatorLeftPaint) {
        this.indicatorLeftPaint = indicatorLeftPaint;
    }

    public Paint getLinePaint() {
        return linePaint;
    }

    public void setLinePaint(Paint linePaint) {
        this.linePaint = linePaint;
    }

    public Paint getLinePaintRight() {
        return linePaintRight;
    }

    public void setLinePaintRight(Paint linePaintRight) {
        this.linePaintRight = linePaintRight;
    }

    public Paint getLinePaintLeft() {
        return linePaintLeft;
    }

    public void setLinePaintLeft(Paint linePaintLeft) {
        this.linePaintLeft = linePaintLeft;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public boolean isSingleIndicator() {
        return isSingleIndicator;
    }

    public void setSingleIndicator(boolean singleIndicator) {
        isSingleIndicator = singleIndicator;
    }

    public String getRightIndicatorText() {
        return rightIndicatorText;
    }

    public void setRightIndicatorText(String rightIndicatorText) {
        if (rightIndicatorText == null)
            this.rightIndicatorText = " ";
        else
            this.rightIndicatorText = rightIndicatorText;
    }

    public String getLeftIndicatorText() {
        return leftIndicatorText;
    }

    public void setLeftIndicatorText(String leftIndicatorText) {
        if (leftIndicatorText == null)
            this.leftIndicatorText = " ";
        else
            this.leftIndicatorText = leftIndicatorText;
    }

    public int getIndicatorRightRadius() {
        return indicatorRightRadius;
    }

    public void setIndicatorRightRadius(int indicatorRightRadius) {
        this.indicatorRightRadius = indicatorRightRadius;
    }

    public int getIndicatorLeftRadius() {
        return indicatorLeftRadius;
    }

    public void setIndicatorLeftRadius(int indicatorLeftRadius) {
        this.indicatorLeftRadius = indicatorLeftRadius;
    }

    public float getIndicatorRightPositionX() {
        return indicatorRightPositionX;
    }

    public void setIndicatorRightPositionX(float indicatorRightPositionX) {
        this.indicatorRightPositionX = indicatorRightPositionX;
    }

    public float getIndicatorLeftPositionX() {
        return indicatorLeftPositionX;
    }

    public void setIndicatorLeftPositionX(float indicatorLeftPositionX) {
        this.indicatorLeftPositionX = indicatorLeftPositionX;
    }

    public int getLineSize() {
        return lineSize;
    }

    public void setLineSize(int lineSize) {
        this.lineSize = lineSize;
    }

    public float getLinePositionStartX() {
        return linePositionStartX;
    }

    public void setLinePositionStartX(float linePositionStartX) {
        this.linePositionStartX = linePositionStartX;
    }

    public float getLinePositionStartY() {
        return linePositionStartY;
    }

    public void setLinePositionStartY(float linePositionStartY) {
        this.linePositionStartY = linePositionStartY;
    }

    public float getLinePositionEndX() {
        return linePositionEndX;
    }

    public void setLinePositionEndX(float linePositionEndX) {
        this.linePositionEndX = linePositionEndX;
    }

    public float getLinePositionEndY() {
        return linePositionEndY;
    }

    public void setLinePositionEndY(float linePositionEndY) {
        this.linePositionEndY = linePositionEndY;
    }

    public boolean isPressLeftIndicator() {
        return pressLeftIndicator;
    }

    public void setPressLeftIndicator(boolean pressLeftIndicator) {
        this.pressLeftIndicator = pressLeftIndicator;
    }

    public boolean isPressRightIndicator() {
        return pressRightIndicator;
    }

    public void setPressRightIndicator(boolean pressRightIndicator) {
        this.pressRightIndicator = pressRightIndicator;
    }

    public boolean isRightFirst() {
        return isRightFirst;
    }

    public void setRightFirst(boolean rightFirst) {
        isRightFirst = rightFirst;
    }

    public float getCalcedRangeMovement() {
        return calcedRangeMovement;
    }

    public void setCalcedRangeMovement(float calcedRangeMovement) {
        this.calcedRangeMovement = calcedRangeMovement;
    }


}
