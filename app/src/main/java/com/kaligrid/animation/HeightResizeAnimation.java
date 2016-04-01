package com.kaligrid.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class HeightResizeAnimation extends Animation {

    private final View view;
    private final int startHeight;
    private final int endHeight;

    public HeightResizeAnimation(View view, int startHeight, int endHeight, AnimationListener animationListener) {
        this.view = view;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.setAnimationListener(animationListener);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        view.getLayoutParams().height = (int) (startHeight + (((endHeight - startHeight) * interpolatedTime)));
        view.requestLayout();
    }
}