package com.sukaidev.magictablayout.ext

import android.content.res.Resources
import android.util.TypedValue
import com.sukaidev.magictablayout.indicator.IndicatorPosition

val Int.dp
    get() = toFloat().dp.toInt()

val Float.dp
    get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
    )

val Double.dp
    get() = toFloat().dp.toDouble()

fun getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels

fun getScreenHeight() = Resources.getSystem().displayMetrics.heightPixels

fun argbEvaluate(fraction: Float, startValue: Int, endValue: Int): Int {
    val startA = startValue shr 24 and 0xff
    val startR = startValue shr 16 and 0xff
    val startG = startValue shr 8 and 0xff
    val startB = startValue and 0xff

    val endA = endValue shr 24 and 0xff
    val endR = endValue shr 16 and 0xff
    val endG = endValue shr 8 and 0xff
    val endB = endValue and 0xff

    val currentA = startA + (fraction * (endA - startA)).toInt() shl 24
    val currentR = startR + (fraction * (endR - startR)).toInt() shl 16
    val currentG = startG + (fraction * (endG - startG)).toInt() shl 8
    val currentB = startB + (fraction * (endB - startB)).toInt()
    return currentA or currentR or currentG or currentB
}

/**
 * 计算锚点位置
 */
fun List<IndicatorPosition>.getImitativeIndicatorInfo(index: Int): IndicatorPosition {
    // 越界后，返回假的PositionData
    return if (index >= 0 && index <= size - 1) {
        this[index]
    } else {
        val result = IndicatorPosition()
        val referenceData: IndicatorPosition
        val offset: Int
        if (index < 0) {
            offset = index
            referenceData = this[0]
        } else {
            offset = index - size + 1
            referenceData = this[size - 1]
        }
        result.left = referenceData.left + offset * referenceData.width()
        result.top = referenceData.top
        result.right = referenceData.right + offset * referenceData.width()
        result.bottom = referenceData.bottom
        result.contentLeft = referenceData.contentLeft + offset * referenceData.width()
        result.contentTop = referenceData.contentTop
        result.contentRight = referenceData.contentRight + offset * referenceData.width()
        result.contentBottom = referenceData.contentBottom
        result
    }
}