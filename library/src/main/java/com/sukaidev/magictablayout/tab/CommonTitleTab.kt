package com.sukaidev.magictablayout.tab

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
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

    /** 是否为对齐BaseLine模式 */
    private var isAlignBaseLine = false

    private var selectedTextSize = 0f
    private var unselectedTextSize = 0f
    private var sizeUnit = 0

    private var selectTextColor = 0
    private var unSelectTextColor = 0

    private var textBoldMode = SELECT

    init {
        gravity = Gravity.CENTER
        setPadding(padding, 0, padding, 0)
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.END
        setTextSize(sizeUnit, unselectedTextSize)
    }

    fun setAlignBaseLineMode(isAlignBaseLine: Boolean) {
        this.isAlignBaseLine = isAlignBaseLine
        if (isAlignBaseLine) {
            post {
                gravity = Gravity.BOTTOM
                val paddingBottom = (height - unselectedTextSize - 10) / 2
                setPadding(padding, 0, padding, paddingBottom.toInt())
            }
            return
        }
        gravity = Gravity.CENTER
        setPadding(padding, 0, padding, 0)
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
        paint.isFakeBoldText = textBoldMode == SELECT || textBoldMode == BOTH
    }

    override fun onTabUnselected(index: Int, totalCount: Int) {
        setTextColor(unSelectTextColor)
        paint.isFakeBoldText = textBoldMode == UNSELECT
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
        return ((height - contentHeight) / 2).toInt()
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

    companion object {
        const val SELECT = 0
        const val UNSELECT = 1
        const val BOTH = 2
    }

    class Builder(private val context: Context) {
        private var title = ""
        private var selectedTextSize = 18f.dp
        private var unselectedTextSize = 18f.dp
        private var sizeUnit = TypedValue.COMPLEX_UNIT_PX

        private var selectTextColor = Color.BLACK
        private var unSelectTextColor = Color.BLACK

        private var textBoldMode = SELECT

        private var isAlignBaseLine = false

        fun setTitle(title: String) = apply {
            this.title = title
        }

        fun setSelectTextColor(@ColorInt color: Int) = apply {
            selectTextColor = color
        }

        fun setSelectTextColorRes(@ColorRes color: Int) = apply {
            selectTextColor = ContextCompat.getColor(context, color)
        }

        fun setSelectTextSize(unit: Int, size: Float) = apply {
            sizeUnit = unit
            selectedTextSize = size
        }

        fun setUnselectTextSize(unit: Int, size: Float) = apply {
            sizeUnit = unit
            unselectedTextSize = size
        }

        fun setUnselectTextColor(@ColorInt color: Int) = apply {
            unSelectTextColor = color
        }

        fun setUnselectTextColorRes(@ColorRes color: Int) = apply {
            unSelectTextColor = ContextCompat.getColor(context, color)
        }

        fun setAlignBaseLineMode(alignBaseLine: Boolean) = apply {
            isAlignBaseLine = alignBaseLine
        }

        fun setTextBoldMode(mode: Int) = apply {
            textBoldMode = mode
        }

        fun build(): CommonTitleTab {
            val tab = CommonTitleTab(context)
            tab.text = title
            tab.textBoldMode = textBoldMode
            tab.selectedTextSize = selectedTextSize
            tab.selectTextColor = selectTextColor
            tab.unselectedTextSize = unselectedTextSize
            tab.unSelectTextColor = unSelectTextColor
            tab.setAlignBaseLineMode(isAlignBaseLine)
            return tab
        }
    }
}