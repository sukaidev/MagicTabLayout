package com.sukaidev.magictablayout.tab

import android.annotation.ColorInt
import android.annotation.ColorRes
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.sukaidev.magictablayout.ext.dp

/**
 * Create by sukaidev at 20/05/2021.
 * 默认TabView
 * @author sukaidev
 */
class CommonTitleTab @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), IMeasurableTab {

    private var padding = 10.dp

    private var selectedTextSize = 18f.dp
    private var unselectedTextSize = 18f.dp
    private var sizeUnit = TypedValue.COMPLEX_UNIT_PX

    private var selectTextColor = Color.BLACK
    private var unSelectTextColor = Color.BLACK

    init {
        gravity = Gravity.CENTER
        setPadding(padding, 0, padding, 0)
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.END
        setTextSize(sizeUnit, unselectedTextSize)
    }

    private fun setSelectTextColor(@ColorInt color: Int) {
        selectTextColor = color
    }

    private fun setSelectTextColorRes(@ColorRes color: Int) {
        selectTextColor = ContextCompat.getColor(context, color)
    }

    private fun setSelectTextSize(unit: Int, size: Float) {
        sizeUnit = unit
        selectedTextSize = size
    }

    private fun setUnselectTextSize(unit: Int, size: Float) {
        sizeUnit = unit
        unselectedTextSize = size
    }

    private fun setUnselectTextColor(@ColorInt color: Int) {
        selectTextColor = color
    }

    private fun setUnselectTextColorRes(@ColorRes color: Int) {
        selectTextColor = ContextCompat.getColor(context, color)
    }

    override fun getContentLeft(): Int {
        val bound = Rect()
        var longestString = ""
        if (text.toString().contains("\n")) {
            val brokenStrings = text.toString().split("\\n".toRegex()).toTypedArray()
            for (each in brokenStrings) {
                if (each.length > longestString.length) longestString = each
            }
        } else {
            longestString = text.toString()
        }
        paint.getTextBounds(longestString, 0, longestString.length, bound)
        val contentWidth = bound.width()
        return left + width / 2 - contentWidth / 2
    }

    override fun onTabSelected(index: Int, totalCount: Int) {
        setTextColor(selectTextColor)
    }

    override fun onTabUnselected(index: Int, totalCount: Int) {
        setTextColor(unSelectTextColor)
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        if (selectedTextSize == unselectedTextSize) return
        val currentSize = (selectedTextSize - unselectedTextSize) * enterPercent + unselectedTextSize
        setTextSize(sizeUnit, currentSize)
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        if (selectedTextSize == unselectedTextSize) return
        val currentSize = (selectedTextSize - unselectedTextSize) * (1 - leavePercent) + unselectedTextSize
        setTextSize(sizeUnit, currentSize)
    }

    override fun getContentTop(): Int {
        val metrics = paint.fontMetrics
        val contentHeight = metrics.bottom - metrics.top
        return (height / 2 - contentHeight / 2).toInt()
    }

    override fun getContentRight(): Int {
        val bound = Rect()
        var longestString = ""
        if (text.toString().contains("\n")) {
            val brokenStrings = text.toString().split("\\n".toRegex()).toTypedArray()
            for (each in brokenStrings) {
                if (each.length > longestString.length) longestString = each
            }
        } else {
            longestString = text.toString()
        }
        paint.getTextBounds(longestString, 0, longestString.length, bound)
        val contentWidth = bound.width()
        return left + width / 2 + contentWidth / 2
    }

    override fun getContentBottom(): Int {
        val metrics = paint.fontMetrics
        val contentHeight = metrics.bottom - metrics.top
        return (height / 2 + contentHeight / 2).toInt()
    }
}