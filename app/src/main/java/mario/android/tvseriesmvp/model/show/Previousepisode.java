package mario.android.tvseriesmvp.model.show;

/**
 * Created by Mario on 14.9.2016.
 */
public class Previousepisode
{
    private String href;

    public String getHref ()
    {
        return href;
    }

    public void setHref (String href)
    {
        this.href = href;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [href = "+href+"]";
    }
}