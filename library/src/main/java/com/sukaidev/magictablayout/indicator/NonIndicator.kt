package com.sukaidev.magictablayout.indicator

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Create by sukaidev at 22/05/2021.
 * 空指示器.
 * 当tabLayout的showIndicator为false
 * 或者 indicatorWidht 为0时，默认使用此指示器
 * @author sukaidev
 */
class NonIndicator @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IMagicIndicator {

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) = Unit

    override fun onPageScrollStateChanged(state: Int) = Unit

    override fun onIndicatorPositionProvide(dataList: List<IndicatorPosition>) = Unit
}