package com.example.watchme.utils

import android.widget.ImageView
import coil.load
import coil.request.CachePolicy
import com.example.watchme.R

fun ImageView.loadMoviePoster(
    posterUrl: String?,
    placeholderRes: Int = android.R.drawable.ic_menu_report_image,
    errorRes: Int = android.R.drawable.ic_menu_report_image
) {
    if (posterUrl.isNullOrBlank() || posterUrl.equals("N/A", ignoreCase = true)) {
        setImageResource(errorRes)
        return
    }

    load(posterUrl) {
        crossfade(true)
        placeholder(placeholderRes)
        error(errorRes)
        diskCachePolicy(CachePolicy.ENABLED)
        memoryCachePolicy(CachePolicy.ENABLED)
    }
}
