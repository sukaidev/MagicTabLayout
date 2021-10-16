package com.sukaidev.magictablayout.pluggable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.sukaidev.magictablayout.ext.dp

/**
 * Created by sukaidev on 2021/09/10.
 *
 * 新消息提醒红点
 *
 * @author sukaidev
 */
class RedDotView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PluggableView(context, attrs, defStyleAttr) {

    var enable: Boolean = true
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }

    var horizontalPadding = 4.dp
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }
    var verticalPadding = 10.dp
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }

    var dotRadius = 3.dp
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isDither = true
        style = Paint.Style.FILL
        color = Color.RED
        strokeWidth = 2F.dp
    }

    init {
        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension((dotRadius + horizontalPadding) * 2, (dotRadius + verticalPadding) * 2)
    }

    override fun onTabSelected(index: Int, totalCount: Int) {
    }

    override fun onTabUnselected(index: Int, totalCount: Int) {
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
    }

    override fun onDraw(canvas: Canvas) {
        if (!enable) return
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        canvas.drawCircle(
            (dotRadius + horizontalPadding).toFloat(),
            (dotRadius + verticalPadding).toFloat(),
            dotRadius.toFloat(),
            paint
        )
    }
}