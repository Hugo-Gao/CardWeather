package tool;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

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
}
