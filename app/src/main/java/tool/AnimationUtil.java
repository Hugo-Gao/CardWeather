package tool;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/5/17.
 */

public class AnimationUtil
{
    public static void FABRotate(final View fab)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(fab, "rotation", fab.getRotation(), fab.getRotation() + 135F);
        ObjectAnimator animator2;
        ObjectAnimator animator3;
        if (fab.getScaleX() > 1F || fab.getScaleY() > 1F)
        {
            animator2 = ObjectAnimator.ofFloat(fab, "scaleX", 1.25F, 1.0F);
            animator3 = ObjectAnimator.ofFloat(fab, "scaleY", 1.25F, 1.0F);
        } else
        {
            animator2 = ObjectAnimator.ofFloat(fab, "scaleX", 1.0F, 1.25F);
            animator3 = ObjectAnimator.ofFloat(fab, "scaleY", 1.0F, 1.25F);
        }
        animator.setDuration(300);
        animator2.setDuration(300);
        animator3.setDuration(300);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, animator2, animator3);
        animatorSet.start();
    }

    public static void FABRotateToNormal(View fab)
    {
        ObjectAnimator animator2;
        ObjectAnimator animator3;
        animator2 = ObjectAnimator.ofFloat(fab, "scaleX", 1.25F, 1.0F);
        animator3 = ObjectAnimator.ofFloat(fab, "scaleY", 1.25F, 1.0F);
        animator2.setDuration(300);
        animator3.setDuration(300);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator2, animator3);
        animatorSet.start();
    }

    public static void CardFlyUp(View card)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(card, "translationZ", 200F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(card, "scaleX", 1.03F);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(card, "scaleY", 1.03F);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator, animator2, animator3);
        set.setDuration(500);
        set.start();
    }

    public static void CardDropDown(View card)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(card, "translationZ", 0F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(card, "scaleX", 1.01F);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(card, "scaleY", 1F);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator, animator2, animator3);
        set.setDuration(500);
        set.start();
    }

    public static void ColorChangeAnimation(View view, int colorFrom, int ColorTo)
    {
        final LinearLayout linearLayout = (LinearLayout) view;
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, ColorTo);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                linearLayout.setBackgroundColor((int) animation.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.start();
    }

    public static void ConfirmAndBigger(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "scaleX", 1F, 1.5F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleY", 1F, 1.5F);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "scaleX", 1.5F, 1F);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(view, "scaleY", 1.5F, 1F);
        animator.setDuration(300);
        animator.start();
        animator2.setDuration(300);
        animator2.start();
        animator3.setDuration(300);
        animator3.start();
        animator4.setDuration(300);
        animator4.start();

    }


}
