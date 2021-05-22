package com.sukaidev.magictablayout.tab

/**
 * Create by sukaidev at 20/05/2021.
 * 抽象的TabView
 * @author sukaidev
 */
interface IMagicTab {

    fun onTabSelected(index: Int, totalCount: Int)

    fun onTabUnselected(index: Int, totalCount: Int)

    /**
     * 滑入当前Tab
     * @param enterPercent 当Tab滑入选中状态时的百分比
     * @param leftToRight 是否从左至右滑入
     */
    fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean)

    /**
     * 滑出当前Tab
     * @param leavePercent 离开的百分比, 0.0f - 1.0f
     * @param leftToRight  是否从左至右滑出
     */
    fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean)
}