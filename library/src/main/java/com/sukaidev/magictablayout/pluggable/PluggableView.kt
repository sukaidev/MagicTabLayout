package com.sukaidev.magictablayout.pluggable

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Created by sukaidev on 2021/09/10.
 * @author sukaidev
 */
abstract class PluggableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    abstract fun onTabSelected(index: Int, totalCount: Int)

    abstract fun onTabUnselected(index: Int, totalCount: Int)

    /**
     * 滑入当前Tab
     * @param enterPercent 当Tab滑入选中状态时的百分比
     * @param leftToRight 是否从左至右滑入
     */
    abstract fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean)

    /**
     * 滑出当前Tab
     * @param leavePercent 离开的百分比, 0.0f - 1.0f
     * @param leftToRight  是否从左至右滑出
     */
    abstract fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean)
}