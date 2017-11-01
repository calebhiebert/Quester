package com.piikl.quester

import android.view.Menu
import android.view.View

fun setVisibility(visibility: Int, vararg views: View) {
    views.forEach { it.visibility = visibility }
}

fun setMenuVisibility(visible: Boolean, menu: Menu, vararg menuItemIds: Int) {
    menuItemIds.forEach {
        menu.findItem(it).isVisible = visible
    }
}