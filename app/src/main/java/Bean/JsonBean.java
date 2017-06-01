package Bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/5/20.
 */

public class JsonBean implements Serializable
{

    private static final long serialVersionUID = 8496795825511496593L;

    @SerializedName("realtime")
    public realtime realTime;

    @SerializedName("life")
    public life lifeInfo;

    public List<Item> weather;//七天天气预报

    @SerializedName("pm25")
    public pm25 PM25;

    @Override
    public String toString()
    {
        return "JsonBean{" +
                "realTime=" + realTime +
                ", lifeInfo=" + lifeInfo +
                ", weather=" + weather +
                ", PM25=" + PM25 +
                '}';
    }

    public static class realtime implements Serializable
    {
        private static final long serialVersionUID = 1896706133328782584L;
        @SerializedName("wind")
        public wind windInfo;

        @Override
        public String toString()
        {
            return "realtime{" +
                    "windInfo=" + windInfo +
                    ", time='" + time + '\'' +
                    ", weatherInfo=" + weatherInfo +
                    ", date='" + date + '\'' +
                    ", city_name='" + city_name + '\'' +
                    ", week='" + week + '\'' +
                    ", moon='" + moon + '\'' +
                    '}';
        }

        public String time;

        @SerializedName("weather")
        public weather weatherInfo;

        public String date;

        public String city_name;

        public String week;

        public String moon;

        public static class wind implements Serializable
        {
            private static final long serialVersionUID = 349935476262268005L;

            @Override
            public String toString()
            {
                return "wind{" +
                        "direct='" + direct + '\'' +
                        ", power='" + power + '\'' +
                        '}';
            }

            public String direct;
            public String power;
        }

        public static class  weather implements Serializable
        {
            private static final long serialVersionUID = 103025311918539131L;
            public String humidity;

            @Override
            public String toString()
            {
                return "weather{" +
                        "humidity='" + humidity + '\'' +
                        ", info='" + info + '\'' +
                        ", temperature='" + temperature + '\'' +
                        '}';
            }

            public String info;
            public String temperature;
        }
    }

    public static class life implements Serializable
    {
        private static final long serialVersionUID = 2134152496216484352L;
        @SerializedName("info")
        public info suggesstInfo;

        @Override
        public String toString()
        {
            return "life{" +
                    "suggesstInfo=" + suggesstInfo +
                    '}';
        }

        public static class info implements Serializable
        {
            private static final long serialVersionUID = 4509544168289757346L;

            @Override
            public String toString()
            {
                return "info{" +
                        "kongtiao=" + kongtiao +
                        ", yundong=" + yundong +
                        ", ganmao=" + ganmao +
                        ", xiche=" + xiche +
                        ", wuran=" + wuran +
                        ", chuanyi=" + chuanyi +
                        ", ziwaixian=" + ziwaixian +
                        '}';
            }

            public List<String> kongtiao;
            public List<String> yundong;
            public List<String> ganmao;
            public List<String> xiche;
            public List<String> wuran;
            public List<String> chuanyi;
            public List<String> ziwaixian;
        }
    }

    public static class Item implements Serializable
    {
        private static final long serialVersionUID = 70305982358535793L;
        public String date;
        public String week;
        public String nongli;

        @Override
        public String toString()
        {
            return "Item{" +
                    "date='" + date + '\'' +
                    ", week='" + week + '\'' +
                    ", nongli='" + nongli + '\'' +
                    ", weatherInfo=" + weatherInfo +
                    '}';
        }

        @SerializedName("info")
        public info weatherInfo;

        public static class info implements Serializable
        {
            private static final long serialVersionUID = 5181543468995402629L;
            public List<String> dawn;

            @Override
            public String toString()
            {
                return "info{" +
                        "dawn=" + dawn +
                        ", day=" + day +
                        ", night=" + night +
                        '}';
            }

            public List<String> day;
            public List<String> night;
        }
    }

    public static class pm25 implements Serializable {

        private static final long serialVersionUID = 2570269960269165089L;
        @SerializedName("pm25")
        public pm25Info info;

        @Override
        public String toString()
        {
            return "pm25{" +
                    "info=" + info +
                    ", dateTime='" + dateTime + '\'' +
                    ", cityName='" + cityName + '\'' +
                    '}';
        }

        public static class pm25Info implements Serializable{
            private static final long serialVersionUID = 4230747197207370204L;
            public String curPm;
            public String pm25;

            @Override
            public String toString()
            {
                return "pm25Info{" +
                        "curPm='" + curPm + '\'' +
                        ", pm25='" + pm25 + '\'' +
                        ", pm10='" + pm10 + '\'' +
                        ", level='" + level + '\'' +
                        ", quality='" + quality + '\'' +
                        ", des='" + des + '\'' +
                        '}';
            }

            public String pm10;
            public String level;
            public String quality;
            public String des;

        }

        public String dateTime;
        public String cityName;
    }
}
