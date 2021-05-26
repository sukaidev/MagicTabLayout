package com.sukaidev.magictablayout.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import com.sukaidev.magictablayout.ext.argbEvaluate
import com.sukaidev.magictablayout.ext.dp
import kotlin.math.abs

/**
 * Create by sukaidev at 20/05/2021.
 * 默认指示器，与原生TabLayout类似
 * @author sukaidev
 */
class CommonIndicator @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IMagicIndicator {

    @Mode
    var mode: Int = MODE_MATCH_EDGE

    /**
     * 指示器相对于x轴和y轴的偏移量
     * 可以通过设置这两个值来指定tab的位置
     */
    var yOffset = 10f
    var xOffset = 0f
    var indicatorWidth = 10.dp
    var indicatorHeight = 3.dp
    var radius = indicatorHeight / 2f

    /** 插值器，用于控制动画效果 */
    var startInterpolator = LinearInterpolator()
    var endInterpolator = LinearInterpolator()

    private var colors: List<Int> = mutableListOf()
    private var indicatorPosition: List<IndicatorPosition>? = null

    private val indicatorRect = RectF()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (indicatorPosition.isNullOrEmpty()) return

        // 计算颜色
        if (colors.isNotEmpty()) {
            val currentColor = colors[abs(position) % colors.size]
            val nextColor = colors[abs(position + 1) % colors.size]
            val color = argbEvaluate(positionOffset, currentColor, nextColor)
            paint.color = color
        }

        // 计算锚点位置
        val current: IndicatorPosition = getImitativeIndicatorInfo(position, indicatorPosition)
                ?: return
        val next: IndicatorPosition = getImitativeIndicatorInfo(position + 1, indicatorPosition)
                ?: return

        val leftX: Float
        val nextLeftX: Float
        val rightX: Float
        val nextRightX: Float
        when (mode) {
            MODE_MATCH_EDGE -> {
                leftX = current.left + xOffset
                nextLeftX = next.left + xOffset
                rightX = current.right - xOffset
                nextRightX = next.right - xOffset
            }
            MODE_WRAP_CONTENT -> {
                leftX = current.contentLeft + xOffset
                nextLeftX = next.contentLeft + xOffset
                rightX = current.contentRight - xOffset
                nextRightX = next.contentRight - xOffset
            }
            // MODE_EXACTLY
            else -> {
                leftX = current.left + (current.width() - indicatorWidth) / 2f
                nextLeftX = next.left + (next.width() - indicatorWidth) / 2f
                rightX = current.left + (current.width() + indicatorWidth) / 2f
                nextRightX = next.left + (next.width() + indicatorWidth) / 2f
            }
        }

        indicatorRect.left = leftX + (nextLeftX - leftX) * startInterpolator.getInterpolation(positionOffset)
        indicatorRect.right = rightX + (nextRightX - rightX) * endInterpolator.getInterpolation(positionOffset)
        indicatorRect.top = height - indicatorHeight - yOffset
        indicatorRect.bottom = height - yOffset

        invalidate()
    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onIndicatorPositionProvide(dataList: List<IndicatorPosition>) {
        indicatorPosition = dataList
    }

    /**
     * 计算锚点位置
     */
    private fun getImitativeIndicatorInfo(index: Int, data: List<IndicatorPosition>?): IndicatorPosition? {
        val size = data?.size ?: return null
        return if (index >= 0 && index <= size - 1) {
            data[index]
        } else {
            val result = IndicatorPosition()
            val referenceData: IndicatorPosition
            val offset: Int
            if (index < 0) {
                offset = index
                referenceData = data[0]
            } else {
                offset = index - size + 1
                referenceData = data[size - 1]
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

    fun getColors(): List<Int> = colors

    fun setColors(@ColorInt vararg colors: Int) {
        this.colors = colors.toList()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(indicatorRect, radius, radius, paint)
    }

    @Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
    @IntDef(value = [MODE_MATCH_EDGE, MODE_WRAP_CONTENT, MODE_EXACTLY])
    annotation class Mode

    companion object {
        /** 指示器宽度 == Tab宽度 - 2 * xOffset */
        const val MODE_MATCH_EDGE = 0

        /** 指示器宽度 == Tab内容宽度 - 2 * xOffset */
        const val MODE_WRAP_CONTENT = 1

        /** 指示器宽度 == lineWidth */
        const val MODE_EXACTLY = 2
    }
}