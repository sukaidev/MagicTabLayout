package com.sukaidev.magictablayout.indicator

/**
 * Create by sukaidev at 21/05/2021.
 * 存储指示器的位置信息
 * @author sukaidev
 */
data class IndicatorPosition(var left: Int = 0,
                             var top: Int = 0,
                             var right: Int = 0,
                             var bottom: Int = 0,
                             var contentLeft: Int = 0,
                             var contentTop: Int = 0,
                             var contentRight: Int = 0,
                             var contentBottom: Int = 0) {

    fun width() = right - left

    fun height() = top - bottom

    fun contentWidth() = contentRight - contentLeft

    fun contentHeight() = contentBottom - contentTop

    fun horizontalCenter() = left + width() / 2

    fun verticalCenter() = top + height() / 2
}