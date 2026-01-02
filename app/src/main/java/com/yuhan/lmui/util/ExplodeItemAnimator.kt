package com.yuhan.lmui.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

class ExplodeItemAnimator : DefaultItemAnimator() {

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        val view = holder.itemView
        
        // Random direction for shards to fly
        val randomX = (Random.nextFloat() - 0.5f) * 1500f
        val randomY = (Random.nextFloat() - 0.5f) * 1500f
        val randomRotation = (Random.nextFloat() - 0.5f) * 1440f

        // Step 1: Anticipation - Shake and slightly scale up
        view.animate()
            .scaleX(1.15f)
            .scaleY(1.15f)
            .rotation(Random.nextFloat() * 10f - 5f)
            .setDuration(250)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                // Step 2: The "Pixel" Explode effect
                // We simulate shattering by moving, rotating, and scaling to zero
                val pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, randomX)
                val pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, randomY)
                val pvhRot = PropertyValuesHolder.ofFloat(View.ROTATION, randomRotation)
                val pvhScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)
                val pvhScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f)
                val pvhAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f)

                val animator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhRot, pvhScaleX, pvhScaleY, pvhAlpha)
                animator.duration = 1000 // Slower for "pixel by pixel" feel
                animator.interpolator = AccelerateInterpolator()
                
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        dispatchRemoveStarting(holder)
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        animator.removeAllListeners()
                        // Reset view state for ViewHolder recycling
                        view.alpha = 1f
                        view.scaleX = 1f
                        view.scaleY = 1f
                        view.translationX = 0f
                        view.translationY = 0f
                        view.rotation = 0f
                        dispatchRemoveFinished(holder)
                    }
                })
                animator.start()
            }
            .start()

        return true
    }
}