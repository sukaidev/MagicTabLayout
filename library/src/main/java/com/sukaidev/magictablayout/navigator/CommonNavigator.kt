package com.sukaidev.magictablayout.navigator

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE
import com.sukaidev.magictablayout.indicator.IMagicIndicator
import com.sukaidev.magictablayout.indicator.IndicatorPosition
import com.sukaidev.magictablayout.tab.IMagicTab
import com.sukaidev.magictablayout.tab.IMeasurableTab

/**
 * Create by sukaidev at 20/05/2021.
 * 默认导航器
 * @author sukaidev
 */
class CommonNavigator @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseNavigator(context, attrs, defStyleAttr), OnNavigatorScrollListener {

    override var mode: Int = MODE_SCROLLABLE
        set(value) {
            field = value
            if (tabContainer?.childCount ?: 0 > 0) initTabsAndIndicators()
        }

    private var tabContainer: LinearLayoutCompat? = null
    private var indicatorContainer: LinearLayoutCompat? = null


    override var indicator: IMagicIndicator? = null
        get() {
            if (field == null) {
                field = adapter?.getIndicator(context)
            }
            return field
        }


    override var adapter: BaseNavigatorAdapter? = null
        set(value) {
            if (value == field) return
            field?.unregisterDataSetObserver(observer)

            field = value

            if (value != null) {
                value.registerDataSetObserver(observer)
                navigatorHelper.setTotalCount(value.getCount())
                // adapter改变时，应该重新init
                if (tabContainer != null) {
                    value.notifyDataSetChanged()
                }
            } else {
                navigatorHelper.setTotalCount(0)
                initViews()
            }
        }

    private val navigatorHelper = NavigatorHelper().apply {
        navigatorScrollListener = this@CommonNavigator
    }

    /** 是否启动中心点滚动 */
    var enablePivotScroll = false

    /** 滚动中心点 0.0f - 1.0f */
    var scrollPivotX = 0.5f

    /** 是否平滑滚动，适用于!mFollowTouch && MODE_SCROLLABLE */
    var isSmoothScrollEnable = true

    /** 是否手指跟随滚动 */
    var isFollowTouch = true

    private val leftPadding = 0
    private val rightPadding = 0

    /** 指示器是否在title上层，默认为下层 */
    var isIndicatorOnTop = false

    /** 跨多页切换时，中间页是否显示 "掠过" 效果 */
    var isSkimOverEnable = false
        set(value) {
            field = value
            navigatorHelper.skimOver = field
        }

    /** IndicatorPosition准备好时，是否重新选中当前页 */
    var reselectWhenLayout = true

    private val indicatorPositions = mutableListOf<IndicatorPosition>()

    override fun initViews() {
        removeAllViews()
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        isHorizontalScrollBarEnabled = false

        val container = FrameLayout(context)
        container.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)

        indicatorContainer = LinearLayoutCompat(context)
        indicatorContainer?.orientation = LinearLayoutCompat.HORIZONTAL
        indicatorContainer?.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        tabContainer = LinearLayoutCompat(context)
        tabContainer?.orientation = LinearLayoutCompat.HORIZONTAL
        tabContainer?.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        tabContainer?.setPadding(leftPadding, 0, rightPadding, 0)

        container.addView(indicatorContainer)
        container.addView(tabContainer)
        if (isIndicatorOnTop) bringChildToFront(indicatorContainer)

        addView(container)

        initTabsAndIndicators()
    }

    /**
     * 初始化title和indicator
     */
    private fun initTabsAndIndicators() {
        tabContainer?.removeAllViews()
        for (i in 0 until navigatorHelper.totalCount) {
            val tab = adapter?.getTabView(context, i)
            if (tab is View) {
                val lp = when (mode) {
                    MODE_FIXED -> {
                        val lp = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT)
                        lp.weight = adapter?.getTitleWeight(context, i) ?: 1f
                        lp
                    }
                    else -> LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
                }
                tabContainer?.addView(tab, lp)
            }
        }
        if (indicator is View) {
            val lp = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            indicatorContainer?.addView(indicator as View, lp)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        adapter ?: return
        prepareIndicatorPositions()
        indicator?.onIndicatorPositionProvide(indicatorPositions)
        if (reselectWhenLayout && navigatorHelper.scrollState == SCROLL_STATE_IDLE) {
            onPageSelected(navigatorHelper.currentIndex)
            onPageScrolled(navigatorHelper.currentIndex, 0f, 0)
        }
    }

    /**
     * 收集指示器的位置信息
     */
    private fun prepareIndicatorPositions() {
        indicatorPositions.clear()
        for (i in 0 until navigatorHelper.totalCount) {
            val data = IndicatorPosition()
            val tab = tabContainer?.getChildAt(i) ?: return
            data.left = tab.left
            data.top = tab.top
            data.right = tab.right
            data.bottom = tab.bottom
            if (tab is IMeasurableTab) {
                data.contentLeft = tab.getContentLeft()
                data.contentTop = tab.getContentTop()
                data.contentRight = tab.getContentRight()
                data.contentBottom = tab.getContentBottom()
            } else {
                data.contentLeft = data.left
                data.contentTop = data.top
                data.contentRight = data.right
                data.contentBottom = data.bottom
            }
            indicatorPositions.add(data)
        }
    }

    override fun onDataSetChanged() {
        initViews()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        adapter ?: return
        navigatorHelper.onPageScrolled(position, positionOffset, positionOffsetPixels)
        indicator?.onPageScrolled(position, positionOffset, positionOffsetPixels)

        // 手指跟随滚动
        if (indicatorPositions.size > 0 && position >= 0 && position < indicatorPositions.size) {
            if (isFollowTouch) {
                val currentPosition = (indicatorPositions.size - 1).coerceAtMost(position)
                val nextPosition = (indicatorPositions.size - 1).coerceAtMost(position + 1)
                val current = indicatorPositions[currentPosition]
                val next = indicatorPositions[nextPosition]
                val scrollTo = current.horizontalCenter() - width * scrollPivotX
                val nextScrollTo = next.horizontalCenter() - width * scrollPivotX
                scrollTo((scrollTo + (nextScrollTo - scrollTo) * positionOffset).toInt(), 0)
            } else if (!enablePivotScroll) {
                // TODO 实现待选中项完全显示出来
            }
        }
    }

    override fun onPageSelected(position: Int) {
        adapter ?: return
        navigatorHelper.onPageSelected(position)
        indicator?.onPageSelected(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
        adapter ?: return
        navigatorHelper.onPageScrollStateChanged(state)
        indicator?.onPageScrollStateChanged(state)
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        if (tabContainer == null || tabContainer?.childCount == 0) return
        val tab = tabContainer?.getChildAt(index)
        (tab as? IMagicTab)?.onEnter(index, totalCount, enterPercent, leftToRight)
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        if (tabContainer == null || tabContainer?.childCount == 0) return
        val tab = tabContainer?.getChildAt(index)
        (tab as? IMagicTab)?.onLeave(index, totalCount, leavePercent, leftToRight)
    }

    override fun onSelected(index: Int, totalCount: Int) {
        if (tabContainer == null || tabContainer?.childCount == 0) return

        val tab = tabContainer?.getChildAt(index)
        (tab as? IMagicTab)?.onTabSelected(index, totalCount)

        if (mode != MODE_FIXED && !isFollowTouch && indicatorPositions.isNotEmpty()) {
            val currentIndex = (indicatorPositions.size - 1).coerceAtMost(index)
            val current: IndicatorPosition = indicatorPositions[currentIndex]
            if (enablePivotScroll) {
                val scrollTo: Float = current.horizontalCenter() - width * scrollPivotX
                if (isSmoothScrollEnable) {
                    smoothScrollTo(scrollTo.toInt(), 0)
                } else {
                    scrollTo(scrollTo.toInt(), 0)
                }
            } else {
                // 如果当前项被部分遮挡，则滚动显示完全
                if (scrollX > current.left) {
                    if (isSmoothScrollEnable) {
                        smoothScrollTo(current.left, 0)
                    } else {
                        scrollTo(current.left, 0)
                    }
                } else if (scrollX + width < current.right) {
                    if (isSmoothScrollEnable) {
                        smoothScrollTo(current.right - width, 0)
                    } else {
                        scrollTo(current.right - width, 0)
                    }
                }
            }
        }
    }

    override fun onUnselected(index: Int, totalCount: Int) {
        if (tabContainer == null || tabContainer?.childCount == 0) return
        val tab = tabContainer?.getChildAt(index)
        (tab as? IMagicTab)?.onTabUnselected(index, totalCount)
    }

    fun getTabView(index: Int): IMagicTab? {
        if (tabContainer == null || tabContainer?.childCount == 0) return null
        return tabContainer?.getChildAt(index) as? IMagicTab
    }

    fun getTabContainer() = tabContainer
}