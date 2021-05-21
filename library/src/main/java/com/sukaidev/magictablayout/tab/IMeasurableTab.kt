package com.sukaidev.magictablayout.tab

/**
 * Create by sukaidev at 20/05/2021.
 * @author sukaidev
 */
interface IMeasurableTab : IMagicTab {
    fun getContentLeft(): Int

    fun getContentTop(): Int

    fun getContentRight(): Int

    fun getContentBottom(): Int
}