package mario.android.tvseriesmvp.model.show;

/**
 * Created by Mario on 14.9.2016.
 */
public class Schedule
{
    private String time;

    private String[] days;

    public String getTime ()
    {
        return time;
    }

    public void setTime (String time)
    {
        this.time = time;
    }

    public String[] getDays ()
    {
        return days;
    }

    public void setDays (String[] days)
    {
        this.days = days;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [time = "+time+", days = "+days+"]";
    }
}