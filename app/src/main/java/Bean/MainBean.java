package Bean;

import android.widget.ImageView;

/**
 * Created by Administrator on 2017/5/18.
 */

public class MainBean
{
    private int temp;
    private ImageView pic;
    private String date;
    private String time;
    private int pmValue;

    public int getTemp()
    {
        return temp;
    }

    public ImageView getPic()
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

    public int getPmValue()
    {
        return pmValue;
    }

    public int getHumidity()
    {
        return humidity;
    }

    public int getWindSpeed()
    {
        return windSpeed;
    }

    private int humidity;
    private int windSpeed;

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
        this.pmValue =builder. pmValue;
        this.humidity = builder.humidity;
        this.windSpeed = builder.windSpeed;
    }

    public static class Builder{
        private int temp;
        private ImageView pic;
        private String date;
        private String time;
        private int pmValue;
        private int humidity;
        private int windSpeed;

        public Builder(){
        }
        public MainBean build(){
            return new MainBean(this);
        }
        public Builder temp(int val){
            this.temp=val;
            return this;
        }
        public Builder pic(ImageView val){
            this.pic=val;
            return this;
        }
        public Builder date(String val){
            this.date=val;
            return this;
        }
        public Builder time(String val){
            this.time=val;
            return this;
        }
        public Builder pmValue(int val){
            this.pmValue=val;
            return this;
        }
        public Builder humidity(int val){
            this.humidity=val;
            return this;
        }
        public Builder windSpeed(int val){
            this.windSpeed=val;
            return this;
        }

    }
}
