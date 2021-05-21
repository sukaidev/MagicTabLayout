package com.sukaidev.magictablayout.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import com.sukaidev.magictablayout.ext.argbEvaluate
import com.sukaidev.magictablayout.ext.dp
import com.sukaidev.magictablayout.ext.getImitativeIndicatorInfo
import java.util.*
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

    // 指示器相对于底部的偏移量
    // 可以通过设置这个值来使得指示器位于tab的顶部
    var yOffset = 0f
    var xOffset = 0f
    var radius = 1.5f.dp
    var indicatorWidth = 3.dp
    var indicatorHeight = 10.dp

    // 插值器，用于控制动画效果
    var startInterpolator = LinearInterpolator()
    var endInterpolator = LinearInterpolator()

    private var colors: List<Int> = mutableListOf()
    private var indicatorPosition: List<IndicatorPosition> = mutableListOf()

    private val indicatorRect = RectF()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (indicatorPosition.isEmpty()) return

        // 计算颜色
        if (colors.isNotEmpty()) {
            val currentColor = colors[abs(position) % colors.size]
            val nextColor = colors[abs(position + 1) % colors.size]
            val color = argbEvaluate(positionOffset, currentColor, nextColor)
            paint.color = color
        }

        // 计算锚点位置
        val current: IndicatorPosition = indicatorPosition.getImitativeIndicatorInfo(position)
        val next: IndicatorPosition = indicatorPosition.getImitativeIndicatorInfo(position + 1)

        val leftX: Float
        val nextLeftX: Float
        val rightX: Float
        val nextRightX: Float
        if (mode == MODE_MATCH_EDGE) {
            leftX = current.left + xOffset
            nextLeftX = next.left + xOffset
            rightX = current.right - xOffset
            nextRightX = next.right - xOffset
        } else if (mode == MODE_WRAP_CONTENT) {
            leftX = current.contentLeft + xOffset
            nextLeftX = next.contentLeft + xOffset
            rightX = current.contentRight - xOffset
            nextRightX = next.contentRight - xOffset
        } else {    // MODE_EXACTLY
            leftX = current.left + (current.width() - indicatorWidth) / 2f
            nextLeftX = next.left + (next.width() - indicatorWidth) / 2f
            rightX = current.left + (current.width() + indicatorWidth) / 2f
            nextRightX = next.left + (next.width() + indicatorWidth) / 2f
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
        // 直线宽度 == title宽度 - 2 * xOffset
        const val MODE_MATCH_EDGE = 0

        // 直线宽度 == title内容宽度 - 2 * xOffset
        const val MODE_WRAP_CONTENT = 1

        // 直线宽度 == lineWidth
        const val MODE_EXACTLY = 2
    }
}