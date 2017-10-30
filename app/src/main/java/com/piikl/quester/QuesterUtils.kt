package com.piikl.quester

import android.view.View

fun setVisibility(visibility: Int, vararg views: View) {
    views.forEach { it.visibility = visibility }
}