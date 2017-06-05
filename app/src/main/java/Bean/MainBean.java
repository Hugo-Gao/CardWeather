package Bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/18.
 */
public class MainBean implements Serializable
{

    private static final long serialVersionUID = -6618662127173639129L;
    private String temp;//当前温度
    private Bitmap pic;//图片
    private String date;//日期
    private String time;//更新时间
    private String pmValue;//pm2.5
    private String humidity;//湿度
    private String windSpeed;//风速
    private String weatherInfo;//对天气的描述：多云

    public void setPmValue(String pmValue)
    {
        this.pmValue = pmValue;
    }

    public String getWeatherInfo()
    {
        return weatherInfo;
    }


    public void setPic(Bitmap pic)
    {
        this.pic = pic;
    }

    public String getTemp()
    {

        return temp;
    }

    public Bitmap getPic()
    {
        return pic;
    }

    public String getDate()
    {
        return date;
    }

    public String getTime()
    {
        return time;
    }

    public String getPmValue()
    {
        return pmValue;
    }

    public String getHumidity()
    {
        return humidity;
    }

    public String getWindSpeed()
    {
        return windSpeed;
    }


    @Override
    public String toString()
    {
        return "MainBean{" +
                "temp=" + temp +
                ", pic=" + pic +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", pmValue=" + pmValue +
                ", humidity=" + humidity +
                ", windSpeed=" + windSpeed +
                '}';
    }

    public MainBean(Builder builder)
    {
        this.temp = builder.temp;
        this.pic = builder.pic;
        this.date = builder.date;
        this.time = builder.time;
        this.pmValue = builder.pmValue;
        this.humidity = builder.humidity;
        this.windSpeed = builder.windSpeed;
        this.weatherInfo = builder.weatherInfo;
    }

    public static class Builder
    {
        private String temp;
        private Bitmap pic;
        private String date;
        private String time;
        private String pmValue;
        private String humidity;
        private String windSpeed;
        private String weatherInfo;//对天气的描述：多云

        public Builder()
        {
        }

        public MainBean build()
        {
            return new MainBean(this);
        }

        public Builder temp(String val)
        {
            this.temp = val;
            return this;
        }

        public Builder weatherInfo(String val)
        {
            this.weatherInfo = val;
            return this;
        }

        public Builder pic(Bitmap val)
        {
            this.pic = val;
            return this;
        }

        public Builder date(String val)
        {
            this.date = val;
            return this;
        }

        public Builder time(String val)
        {
            this.time = val;
            return this;
        }

        public Builder pmValue(String val)
        {
            this.pmValue = val;
            return this;
        }

        public Builder humidity(String val)
        {
            this.humidity = val;
            return this;
        }

        public Builder windSpeed(String val)
        {
            this.windSpeed = val;
            return this;
        }

    }



    }
