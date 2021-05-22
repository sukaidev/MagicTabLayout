package com.sukaidev.magictablayout.tab

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.sukaidev.magictablayout.ext.dp

/**
 * Create by sukaidev at 22/05/2021.
 *
 * @author sukaidev
 */
class ScalableIconTab @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IMeasurableTab {

    private var padding = 10.dp

    private var isAlignBaseLine = false

    var maxScaleWidth = 1.2f
    var maxScaleHeight = 1.2f

    var imageWidth = 20.dp
    var imageHeight = 24.dp

    private val imageView = AppCompatImageView(context)

    init {
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        val lp = LayoutParams(24.dp, 30.dp)
        lp.gravity = Gravity.CENTER
        addView(imageView, lp)

        setPadding(padding, 0, padding, 0)
    }

    fun setIcon(drawable: Drawable) {
        imageView.setImageDrawable(drawable)
    }

    fun setIcon(@DrawableRes resId: Int) {
        imageView.setImageResource(resId)
    }

    fun setAlignBaseLineMode(isAlignBaseLine: Boolean) {
        this.isAlignBaseLine = isAlignBaseLine
        if (isAlignBaseLine) {
            setPadding(padding, 0, padding, padding)
            return
        }
        setPadding(padding, 0, padding, 0)
    }

    override fun onTabSelected(index: Int, totalCount: Int) {
    }

    override fun onTabUnselected(index: Int, totalCount: Int) {
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        val currentWidth = (enterPercent * (maxScaleWidth - 1) + 1) * imageWidth
        val currentHeight = (enterPercent * (maxScaleHeight - 1) + 1) * imageHeight
        setImageWidthAndHeight(currentWidth.toInt(), currentHeight.toInt())
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        val currentWidth = ((1 - leavePercent) * (maxScaleWidth - 1) + 1) * imageWidth
        val currentHeight = ((1 - leavePercent) * (maxScaleHeight - 1) + 1) * imageHeight
        setImageWidthAndHeight(currentWidth.toInt(), currentHeight.toInt())
    }

    private fun setImageWidthAndHeight(width: Int, height: Int) {
        val lp = imageView.layoutParams
        lp.width = width
        lp.height = height
        imageView.layoutParams = lp
    }

    override fun getContentLeft() = imageView.left + left

    override fun getContentTop() = imageView.top + top

    override fun getContentRight() = imageView.right + left

    override fun getContentBottom() = imageView.bottom + top
}