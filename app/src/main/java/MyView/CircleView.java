package MyView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2017/5/30.
 */

public class CircleView extends View
{
    private Paint innerPaint;
    private Paint outterPaint;
    private Paint valuePaint;
    private Paint titlePaint;
    private Paint subTitlePaint;

    private float MaxValue;
    private float curValue;
    private float centerPointX;
    private float centerPointY;
    private float innerRadius;
    private String title;
    private String subTitle;
    private float strokeWidth;
     Rect bound = new Rect();
    public float getCurValue()
    {
        return curValue;
    }

    public void setCurValue(float curValue)
    {
        this.curValue = curValue;
    }


    public CircleView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        innerPaint = new Paint();
        innerPaint.setAntiAlias(true);
        outterPaint = new Paint();
        outterPaint.setAntiAlias(true);
        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);
        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        subTitlePaint = new Paint();
        subTitlePaint.setAntiAlias(true);
        valuePaint.setTextSize(55f);
        subTitlePaint.setTextSize(30f);
        titlePaint.setTextSize(35f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        centerPointX = width / 2.0f;
        centerPointY = height / 2.0f;
        innerRadius=(width>height?height:width)*0.3f;
        strokeWidth=height/20f;
        innerPaint.setStrokeWidth(3.0f);
        innerPaint.setColor(0xFFDEDCD8);
        innerPaint.setStyle(Paint.Style.STROKE);
        outterPaint.setStrokeWidth(6.0f);
        outterPaint.setStyle(Paint.Style.STROKE);
        subTitlePaint.setColor(Color.GRAY);
        titlePaint.setColor(Color.BLACK);

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawInnerCircle(canvas);
        drawOutterCircle(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas)
    {
        String value;
        if (MaxValue == 12)
        {
            title = "风力级数";
            subTitle = "WindSpeed";
            value = String.valueOf((int) curValue) + "级";

        } else if (MaxValue == 300)
        {

            title = "空气质量指数";
            subTitle = "AQI";
            value = String.valueOf((int) curValue);
        }
        else
        {
            curValue=0;
            title = "空气质量指数";
            subTitle = "AQI";
            value = "暂无数据";
        }
        valuePaint.getTextBounds(value, 0, value.length(), bound);//测量字符宽度
        canvas.drawText(value, centerPointX - (bound.width() / 2.0f), centerPointY - (bound.height() / 10.0f), valuePaint);
        subTitlePaint.getTextBounds(subTitle, 0, subTitle.length(), bound);//测量字符宽度
        canvas.drawText(subTitle, centerPointX - (bound.width() / 2.0f), centerPointY + bound.height()*1.1f, subTitlePaint);
        titlePaint.getTextBounds(title, 0, title.length(), bound);//测量字符宽度
        canvas.drawText(title, centerPointX - (bound.width() / 2.0f), centerPointY + innerRadius+bound.height()*0.3f, titlePaint);

    }

    private void drawOutterCircle(Canvas canvas)
    {
        float sweepAngle=(curValue/MaxValue)*270f;
        if(curValue/MaxValue<0.3f)
        {
            outterPaint.setColor(Color.parseColor("#3F51B5"));
            valuePaint.setColor(Color.parseColor("#3F51B5"));
        } else if (curValue / MaxValue >= 0.3f && curValue / MaxValue <= 0.7f)
        {
            outterPaint.setColor(Color.parseColor("#FFD464"));
            valuePaint.setColor(Color.parseColor("#FFD464"));
        }else
        {
            outterPaint.setColor(Color.parseColor("#F44336"));
            valuePaint.setColor(Color.parseColor("#F44336"));
        }
        canvas.drawArc(new RectF(centerPointX - innerRadius,
                centerPointY - innerRadius,
                centerPointX + innerRadius,
                centerPointY + innerRadius),135f, sweepAngle, false, outterPaint);
        Log.d("cicle", "圆形绘制完成");
    }

    private void drawInnerCircle(Canvas canvas)
    {
        canvas.drawArc(new RectF(centerPointX - innerRadius,
                centerPointY - innerRadius,
                centerPointX + innerRadius,
                centerPointY + innerRadius),135f, 270f, false, innerPaint);

    }

    public void setType(type typer)
    {
        if(typer == type.WindSpeed)
        {
            MaxValue=12;
        }else if(typer==type.AQI)
        {
            MaxValue = 300;
        }
    }

    public  enum type{
        WindSpeed //风力指数
        ,AQI  //污染指数
    }

    public void fresh()
    {
        init();
        postInvalidate();
    }

}
