package MyView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gaoyunfan.cardweather.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Bean.JsonBean;
import Bean.TempBean;

/**
 * Created by Administrator on 2017/5/26.
 */

public class ChartView extends View
{
    private List<String> xLable;//X轴坐标值
    private List<String> yLable;//X轴坐标值
    private List<TempBean> tempList;
    private final String TAG = "Chart";
    private String title = "七日温度折线图";
    private String[] mMaxDataLineColors;
    private String[] mMinDataLineColors;
    private float[][] mMaxDataCoords = new float[7][2];
    private float[][] mMinDataCoords=new float[7][2];
    private Paint mMaxDataLinePaint;  //数据画笔
    private Paint mMinDataLinePaint;
    private Paint mScaleLinePaint; //坐标画笔
    private Paint mScaleValuePaint;//图表刻度值画笔
    private Paint mBackColorPaint;//背景色块画笔
    private float yScale;
    private float xScale;
    private float startPointX;
    private float startPointY;
    private float xLength;
    private float yLength;
    private float chartLineStrokeWidth;
    private float coordTextSize;
    private float dataLineStrodeWidth;
    private boolean isClick;
    private int clickIndex = -1;
    private final int ClickMax=0;
    private final int ClickMin=1;
    private int nowClick;
    Rect bounds = new Rect();
    private PopupWindow mPopWin;

    public ChartView(Context context, @Nullable AttributeSet attrs, JsonBean jsonBean, List<TempBean> tempList)
    {
        super(context, attrs);
        this.tempList = tempList;
        for (JsonBean.Item item : jsonBean.weather)
        {
            xLable.add(item.date);
        }
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * 初始化画笔及数据刻度值
     */
    private void init()
    {
        this.setBackgroundColor(Color.WHITE);
        if (xLable == null)
        {
            Log.d(TAG, "X坐标数据有误");
            return;
        }
        if (tempList == null)
        {
            Log.d(TAG, "温度数据为空");
            return;
        }
        yLable = createYLabel();
        mMaxDataLineColors = new String[]{"#fbbc14", "#fbaa0c", "#fbaa0c", "#fb8505", "#ff6b02", "#ff5400", "#ff5400"};
        mMinDataLineColors=  new String[]{"#90caf9","#90caf9", "#64b5f6", "#2196f3", "#1e88e5", "#1e88e5","#1976d2"};
        mMaxDataLinePaint = new Paint();
        mMinDataLinePaint = new Paint();
        mScaleLinePaint = new Paint();
        mScaleValuePaint = new Paint();
        mBackColorPaint = new Paint();

        mMaxDataLinePaint.setAntiAlias(true);
        mMinDataLinePaint.setAntiAlias(true);
        mScaleLinePaint.setAntiAlias(true);
        mScaleValuePaint.setAntiAlias(true);
        mBackColorPaint.setAntiAlias(true);
    }

    private List<String> createYLabel()
    {
        int[] maxDataInt = new int[7];
        int[] minDataInt =new int[7];
        for (int i = 0; i < tempList.size(); i++)
        {
            maxDataInt[i] = tempList.get(i).getHighTemp();
            minDataInt[i]=tempList.get(i).getLowTemp();
        }
        Arrays.sort(maxDataInt);
        Arrays.sort(minDataInt);
        int middle = (minDataInt[0] + maxDataInt[6]) / 2;
        double scaleValue = Math.abs(maxDataInt[6] - minDataInt[0]) / 5.0;//将y轴分为6份
        if(scaleValue==0)
        {
            scaleValue++;
        }
        if((scaleValue-(int)scaleValue)>0.5&&(scaleValue-(int)scaleValue)<1.0)
        {
            scaleValue=((int)scaleValue)+1;
        }
        int scale = (int) scaleValue;
        List<String> yText = new ArrayList<>();
        while(true)
        {
            if (maxDataInt[6] - (middle + 2 * scale) >= 2 || (middle - 3 * scale) - minDataInt[0] > 2)
            {
                scale++;
            }else
            {
                break;
            }
        }
        yText.add((middle - 3 * scale) + "");
        yText.add((middle - 2 * scale) + "");
        yText.add((middle -  scale) + "");
        yText.add(middle + "");
        yText.add((middle +  scale) + "");
        yText.add((middle + 2 * scale) + "");
        return yText;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initParams();
    }

    /**
     * 初始化尺寸及画笔属性等参数
     */
    private void initParams()
    {
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        yScale = height / 8.5f;
        xScale = width / 7.5f;
        startPointX = xScale / 2;
        startPointY = yScale / 2;
        xLength = 6.5f * xScale;
        yLength = 6.0f * yScale;


        chartLineStrokeWidth = xScale / 50;
        coordTextSize = xScale / 4.0f;
        dataLineStrodeWidth = xScale / 15;

        //设置画笔数据
        mBackColorPaint.setColor(0x11DEDE68);
        mScaleLinePaint.setStrokeWidth(chartLineStrokeWidth);
        mScaleLinePaint.setColor(0xFFDEDCD8);
        mScaleValuePaint.setColor(0xFF999999);
        mScaleValuePaint.setTextSize(coordTextSize);
        mMaxDataLinePaint.setStrokeWidth(dataLineStrodeWidth);
        mMaxDataLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mMaxDataLinePaint.setTextSize(1.5f * coordTextSize);
        mMinDataLinePaint.setStrokeWidth(dataLineStrodeWidth);
        mMinDataLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mMinDataLinePaint.setTextSize(1.5f * coordTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawBackColor(canvas);//绘制背景色块
        drawYAxisAndXScaleValue(canvas);//绘制y轴和x刻度值
        drawXAxisAndYScaleValue(canvas);//绘制x轴和刻度值
        drawDataLines(canvas);//绘制数据连线
        drawDataPoints(canvas);//绘制数据点
        drawTitle(canvas);//绘制标题

    }


    private void drawBackColor(Canvas canvas)
    {
        for (int i = 0; i < 7; i++)
        {
            if (i != 0 && i % 2 == 0)
            {
                canvas.drawRect(startPointX + (i - 1) * xScale,
                        startPointY + 0.5f * yScale,
                        startPointX + i * xScale,
                        yLength + startPointY +   yScale,
                        mBackColorPaint);
            }
        }
    }

    private void drawYAxisAndXScaleValue(Canvas canvas)
    {
        for (int i = 0; i < 7; i++)
        {
            canvas.drawLine(startPointX + i * xScale,
                    startPointY + 0.5f * yScale,
                    startPointX + i * xScale,
                    startPointY + yLength + 1.0f * yScale,
                    mScaleLinePaint
            );
            mScaleValuePaint.getTextBounds(xLable.get(i), 0, xLable.get(i).length(), bounds);//测量字符宽度
            if (i == 0)
            {
                canvas.drawText(xLable.get(i),
                        startPointX,
                        startPointY + yLength + bounds.height() + 1.1f * yScale,//不解
                        mScaleValuePaint);

            } else
            {
                canvas.drawText(xLable.get(i),
                        startPointX + i * xScale - bounds.width() / 2,
                        startPointY + yLength + bounds.height() + 1.1f * yScale,
                        mScaleValuePaint);
            }

        }
    }

    private void drawXAxisAndYScaleValue(Canvas canvas)
    {
        for (int i = 0; i < 7; i++)
        {
            if (i < 6)
            {
                mScaleValuePaint.getTextBounds(yLable.get(5 - i), 0, yLable.get(5 - i).length(), bounds);
                canvas.drawText(yLable.get(5 - i),//y轴上的坐标值
                        startPointX + xScale / 15,
                        startPointY + yScale * (i + 1.0f) + bounds.height() / 2,
                        mScaleValuePaint);
                canvas.drawLine(startPointX + bounds.width() + 2 * xScale / 15,
                        startPointY + (i + 0.5f) * yScale + 0.5f * yScale,
                        startPointX + xLength,
                        startPointY + (i + 1.0f) * yScale,
                        mScaleLinePaint);
            } else
            {
                canvas.drawLine(startPointX,
                        startPointY + (i + 0.5f) * yScale + 0.5f * yScale,
                        startPointX + xLength,
                        startPointY + (i + 1.0f) * yScale,
                        mScaleLinePaint);//画X轴
            }
        }
    }

    private void getDataRoords()
    {
        float originalPointX = startPointX;
        float originalPointY = startPointY + yLength - yScale;
        for (int i = 0; i < tempList.size(); i++)
        {
            mMaxDataCoords[i][0] = originalPointX + i * xScale;
            int dataMaxY = tempList.get(i).getHighTemp();
            int oriMaxY = Integer.parseInt(yLable.get(0));
            mMaxDataCoords[i][1] = originalPointY + yScale - (yScale * (dataMaxY - oriMaxY) / (Float.parseFloat(yLable.get(1)) - Float.parseFloat(yLable.get(0))));
            //准备最低温度的数据
            mMinDataCoords[i][0] = originalPointX + i * xScale;
            int dataMinY = tempList.get(i).getLowTemp();
            int oriMinY = Integer.parseInt(yLable.get(0));
            mMinDataCoords[i][1] = originalPointY +yScale- (yScale * (dataMinY - oriMinY) / (Float.parseFloat(yLable.get(1)) - Float.parseFloat(yLable.get(0))));
        }
    }

    private void drawDataLines(Canvas canvas)
    {
        getDataRoords();
        for (int i = 0; i < 6; i++)
        {
            mMaxDataLinePaint.setColor(Color.parseColor(mMaxDataLineColors[i]));
            canvas.drawLine(mMaxDataCoords[i][0], mMaxDataCoords[i][1], mMaxDataCoords[i + 1][0], mMaxDataCoords[i + 1][1], mMaxDataLinePaint);

            mMinDataLinePaint.setColor(Color.parseColor(mMinDataLineColors[i]));
            canvas.drawLine(mMinDataCoords[i][0], mMinDataCoords[i][1], mMinDataCoords[i + 1][0], mMinDataCoords[i + 1][1], mMinDataLinePaint);

        }
    }

    private void drawDataPoints(Canvas canvas)
    {
        // 点击后，绘制数据点
        if (isClick && clickIndex > -1&&nowClick==ClickMax)
        {
            mMaxDataLinePaint.setColor(Color.parseColor(mMaxDataLineColors[clickIndex]));
            canvas.drawCircle(mMaxDataCoords[clickIndex][0], mMaxDataCoords[clickIndex][1], xScale / 10, mMaxDataLinePaint);
            mMaxDataLinePaint.setColor(Color.WHITE);
            canvas.drawCircle(mMaxDataCoords[clickIndex][0], mMaxDataCoords[clickIndex][1], xScale / 20, mMaxDataLinePaint);
            mMaxDataLinePaint.setColor(Color.parseColor(mMaxDataLineColors[clickIndex]));
        }else if(isClick && clickIndex > -1&&nowClick==ClickMin)
        {
            mMinDataLinePaint.setColor(Color.parseColor(mMinDataLineColors[clickIndex]));
            canvas.drawCircle(mMinDataCoords[clickIndex][0], mMinDataCoords[clickIndex][1], xScale / 10, mMinDataLinePaint);
            mMinDataLinePaint.setColor(Color.WHITE);
            canvas.drawCircle(mMinDataCoords[clickIndex][0], mMinDataCoords[clickIndex][1], xScale / 20, mMinDataLinePaint);
            mMinDataLinePaint.setColor(Color.parseColor(mMinDataLineColors[clickIndex]));

        }
    }

    private void drawTitle(Canvas canvas)
    {
        // 绘制标题文本和线条
        Paint paint;
        if(nowClick==ClickMin)
        {
            paint=mMinDataLinePaint;
        }else
        {
            paint=mMaxDataLinePaint;
        }
        paint.getTextBounds(title, 0, title.length(), bounds);

        canvas.drawText(title, (getWidth() - bounds.width()) / 2, startPointY + yLength + 1.8f*yScale, paint);
        canvas.drawLine((getWidth() - bounds.width()) / 2 - xScale / 15,
                startPointY + yLength + 1.8f*yScale - bounds.height() / 2 + coordTextSize / 4,
                (getWidth() - bounds.width()) / 2 - xScale / 2,
                startPointY + yLength + 1.8f*yScale - bounds.height() / 2 + coordTextSize / 4,
                paint);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float touchX = event.getX();
        float touchY = event.getY();
        for (int i = 0; i < 7; i++)
        {
            float maxDataX = mMaxDataCoords[i][0];
            float maxDataY = mMaxDataCoords[i][1];
            float minDataX = mMinDataCoords[i][0];
            float minDataY = mMinDataCoords[i][1];
            // 控制触摸/点击的范围，在有效范围内才触发
            if (Math.abs(touchX - maxDataX) < xScale / 2 && Math.abs(touchY - maxDataY) < yScale / 2)
            {
                isClick = true;
                nowClick=ClickMax;
                clickIndex = i;
                invalidate();     // 重绘展示数据点小圆圈
                showDetails(i,ClickMax);   // 通过PopupWindow展示详细数据信息
                return true;
            }
            else if (Math.abs(touchX - minDataX) < xScale / 2 && Math.abs(touchY - minDataY) < yScale / 2)
            {
                isClick = true;
                nowClick=ClickMin;
                clickIndex = i;
                invalidate();     // 重绘展示数据点小圆圈
                showDetails(i,ClickMin);   // 通过PopupWindow展示详细数据信息
                return true;
            }
            else
            {
                hideDetails();
            }
            clickIndex = -1;
            nowClick=-1;
            invalidate();
        }
        return super.onTouchEvent(event);
    }

    private void showDetails(int index,int ClickWhat)
    {
        if (mPopWin != null) mPopWin.dismiss();
        TextView tv = new TextView(getContext());
        tv.setTextColor(Color.WHITE);
        if(ClickWhat==ClickMax)
        {
            tv.setBackgroundResource(R.drawable.shape_max_pop_bg);
        }else
        {
            tv.setBackgroundResource(R.drawable.shape_min_pop_bg);
        }
        GradientDrawable myGrad = (GradientDrawable) tv.getBackground();
        if(ClickWhat==ClickMax)
        {
            myGrad.setColor(Color.parseColor(mMaxDataLineColors[index]));
        }else
        {
            myGrad.setColor(Color.parseColor(mMinDataLineColors[index]));
        }
        tv.setPadding(20, 0, 20, 0);
        tv.setGravity(Gravity.CENTER);
        if(ClickWhat==ClickMax)
        {
            tv.setText(tempList.get(index).getHighTemp() + "°");
        }else
        {
            tv.setText(tempList.get(index).getLowTemp() + "°");
        }
        mPopWin = new PopupWindow(tv, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWin.setBackgroundDrawable(new ColorDrawable(0));
        mPopWin.setFocusable(false);
        // 根据坐标点的位置计算弹窗的展示位置
        int xoff;
        int yoff;
        if(ClickWhat==ClickMax)
        {
             xoff = (int) (mMaxDataCoords[index][0] - 0.5f * xScale);
             yoff = -(int) (getHeight() - mMaxDataCoords[index][1] + 0.75f * yScale);
        }else
        {
             xoff = (int) (mMinDataCoords[index][0] - 0.5f * xScale);
             yoff = -(int) (getHeight() - mMinDataCoords[index][1] + 0.75f * yScale);
        }
        mPopWin.showAsDropDown(this, xoff, yoff);
        mPopWin.update();
    }

    private void hideDetails()
    {
        if (mPopWin != null) mPopWin.dismiss();
    }

    public void setxLabel(JsonBean jsonBean)
    {
        if (xLable != null)
        {
            xLable.clear();
        } else
        {
            xLable = new ArrayList<>();
        }
        for (JsonBean.Item item : jsonBean.weather)
        {
            String date = item.date.substring(5);
            xLable.add(date);
        }
    }

    public void setData(List<TempBean> data)
    {
        this.tempList = data;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void fresh()
    {
        init();
        postInvalidate();
    }
}
