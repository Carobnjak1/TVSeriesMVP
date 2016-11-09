package mario.android.tvseriesmvp.model.episodes;

import mario.android.tvseriesmvp.model.show.Self;

/**
 * Created by Mario on 17.10.2016.
 */
public class _links
{
    private Self self;

    public Self getSelf ()
    {
        return self;
    }

    public void setSelf (Self self)
    {
        this.self = self;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [self = "+self+"]";
    }
}
