package com.sukaidev.magictablayout.ext

import android.content.res.Resources
import android.util.TypedValue

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