package Bean;

/**
 * Created by Administrator on 2017/5/23.
 */

public class TempBean
{
    @Override
    public String toString()
    {
        return "TempBean{" +
                "lowTemp=" + lowTemp +
                ", highTemp=" + highTemp +
                '}';
    }

    private int lowTemp;
    private int highTemp;
    public TempBean(int lowTemp, int highTemp)
    {
        this.lowTemp = lowTemp;
        this.highTemp = highTemp;
    }
    public int getLowTemp()
    {
        return lowTemp;
    }

    public void setLowTemp(int lowTemp)
    {
        this.lowTemp = lowTemp;
    }

    public int getHighTemp()
    {
        return highTemp;
    }

    public void setHighTemp(int highTemp)
    {
        this.highTemp = highTemp;
    }

}
