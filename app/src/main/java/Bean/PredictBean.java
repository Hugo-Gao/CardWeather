package Bean;

/**
 * Created by Administrator on 2017/5/18.
 */

public class PredictBean
{
    private String date;
    private String weekDay;
    private String describe;
    private String temp;

    public String getDate()
    {
        return date;
    }

    public String getWeekDay()
    {
        return weekDay;
    }

    public String getDescribe()
    {
        return describe;
    }

    public String getTemp()
    {
        return temp;
    }

    @Override

    public String toString()
    {
        return "PredictBean{" +
                "date='" + date + '\'' +
                ", weekDay='" + weekDay + '\'' +
                ", describe='" + describe + '\'' +
                ", Temp=" + temp +
                '}';
    }

    public PredictBean(Builder builder)
    {
        this.date = builder.date;
        this.weekDay = builder.weekDay;
        this.describe = builder.describe;
        this.temp=builder.temp;
    }

    public static class Builder
    {
        private String date;
        private String weekDay;
        private String describe;
        private String temp;


        public Builder()
        {
        }


        public Builder date(String date)
        {
            this.date = date;
            return this;
        }


        public Builder weekDay(String weekDay)
        {
            this.weekDay = weekDay;
            return this;

        }


        public Builder describe(String describe)
        {
            this.describe = describe;
            return this;

        }


        public Builder Temp(String Temp)
        {
            this.temp = Temp;
            return this;

        }



        public PredictBean build()
        {
            return new PredictBean(this);
        }
    }

}
