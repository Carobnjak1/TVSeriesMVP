package mario.android.tvseriesmvp.model.show;

/**
 * Created by Mario on 14.9.2016.
 */
public class TVShow
{
    private String score;

    private Show show;

    public String getScore ()
    {
        return score;
    }

    public void setScore (String score)
    {
        this.score = score;
    }

    public Show getShow ()
    {
        return show;
    }

    public void setShow (Show show)
    {
        this.show = show;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [score = "+score+", show = "+show+"]";
    }
}