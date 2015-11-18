package com.example.xyzreader.utils;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;
import android.util.Log;

/**
 * Animation utilities for object transitions
 *
 * Created by kyleparker on 11/17/2015.
 */
public class AnimationUtils {
    /**
     * Code based on example from "Android RecyclerView Animation Example: Material Design Tutorials"
     * https://www.youtube.com/watch?v=e8ifO1m9g_Q
     *
     * @param holder
     * @param scrollDown
     */
    public static void animate(RecyclerView.ViewHolder holder, boolean scrollDown) {
//        holder.itemView.setTranslationY(scrollDown ? offset : -offset);
//        holder.itemView.setAlpha(0.85f);
//        // then animate back to natural position
//        holder.itemView.animate()
//                .translationY(0f)
//                .alpha(1f)
//                .setInterpolator(interpolator)
//                .setDuration(1000L)
//                .start();
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.itemView, "translationY", scrollDown ? 300 : -300, 0);
        animator.setDuration(300);
        animator.start();
    }
}
