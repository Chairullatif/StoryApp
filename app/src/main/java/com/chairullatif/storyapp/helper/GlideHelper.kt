package com.chairullatif.storyapp.helper

import android.widget.ImageView
import com.bumptech.glide.Glide

object GlideHelper {
    fun ImageView.loadImage(url: String?) {
        Glide.with(this)
            .load(url)
            .error(android.R.drawable.stat_notify_error)
            .into(this)
    }
}