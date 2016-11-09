package mario.android.tvseriesmvp.model.show;

import java.io.Serializable;

/**
 * Created by Mario on 14.9.2016.
 */
public class Rating implements Serializable
{
    private String average;

    public String getAverage ()
    {
        return average;
    }

    public void setAverage (String average)
    {
        this.average = average;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [average = "+average+"]";
    }
}
