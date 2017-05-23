package tool;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/5/22.
 */

public class RecyScrollListener extends RecyclerView.OnScrollListener
{
    private View view;

    public RecyScrollListener(View view)
    {
        this.view = view;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState)
    {
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy)
    {
        if(dy>=30)
        {
            AnimationUtil.CardFlyUp(view);
        }
        else if(dy<=-30)
        {
            AnimationUtil.CardDropDown(view);
        }
    }
}
