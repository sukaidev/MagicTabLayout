package com.sukaidev.magictablayout.navigator

/**
 * Create by sukaidev at 20/05/2021.
 * @author sukaidev
 */
interface OnNavigatorScrollListener {

    fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean)

    fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean)

    fun onSelected(index: Int, totalCount: Int)

    fun onUnselected(index: Int, totalCount: Int)
}