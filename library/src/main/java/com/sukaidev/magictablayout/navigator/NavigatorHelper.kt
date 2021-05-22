package com.sukaidev.magictablayout.navigator

import android.util.SparseArray
import android.util.SparseBooleanArray
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE

/**
 * Create by sukaidev at 20/05/2021.
 * 协助[IMagicNavigator]将ViewPager的滑动回调转换成[OnNavigatorScrollListener]中的方法
 * @author sukaidev
 */
class NavigatorHelper {
    private val unselectItems = SparseBooleanArray()
    private val leavePercents = SparseArray<Float>()

    /** tab总数 */
    var totalCount = 0
        private set

    /** 滑动状态 */
    var scrollState = 0
        private set

    /** 当前focus的tab pos */
    var currentIndex = 0
        private set

    private var lastIndex = 0
    private var lastPositionOffsetSum = 0f

    var skimOver = false
    var navigatorScrollListener: OnNavigatorScrollListener? = null

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val currentPositionOffsetSum = position + positionOffset
        var leftToRight = false
        if (lastPositionOffsetSum <= currentPositionOffsetSum) {
            leftToRight = true
        }
        if (scrollState != SCROLL_STATE_IDLE) {
            if (currentPositionOffsetSum == lastPositionOffsetSum) return
            var nextPosition = position + 1
            var normalDispatch = true
            if (positionOffset == 0f) {
                if (leftToRight) {
                    nextPosition = position - 1
                    normalDispatch = false
                }
            }
            for (i in 0 until totalCount) {
                if (i == position || i == nextPosition) {
                    continue
                }
                val leavePercent = leavePercents[i, 0.0f]
                if (leavePercent != 1.0f) {
                    dispatchOnLeave(i, 1.0f, leftToRight, true)
                }
            }
            if (normalDispatch) {
                if (leftToRight) {
                    dispatchOnLeave(position, positionOffset, leftToRight = true, force = false)
                    dispatchOnEnter(nextPosition, positionOffset, leftToRight = true, force = false)
                } else {
                    dispatchOnLeave(nextPosition, 1.0f - positionOffset, leftToRight = false, force = false)
                    dispatchOnEnter(position, 1.0f - positionOffset, leftToRight = false, force = false)
                }
            } else {
                dispatchOnLeave(nextPosition, 1.0f - positionOffset, leftToRight = true, force = false)
                dispatchOnEnter(position, 1.0f - positionOffset, leftToRight = true, force = false)
            }
        } else {
            for (i in 0 until totalCount) {
                if (i == currentIndex) {
                    continue
                }
                val deselected = unselectItems[i]
                if (!deselected) {
                    dispatchOnDeselected(i)
                }
                val leavePercent = leavePercents[i, 0f]
                if (leavePercent != 1f) {
                    dispatchOnLeave(i, 1f, leftToRight = false, force = true)
                }
            }
            dispatchOnEnter(currentIndex, 1f, leftToRight = false, force = true)
            dispatchOnSelected(currentIndex)
        }
        lastPositionOffsetSum = currentPositionOffsetSum
    }

    private fun dispatchOnEnter(index: Int, enterPercent: Float, leftToRight: Boolean, force: Boolean) {
        if (skimOver || index == currentIndex || scrollState == SCROLL_STATE_DRAGGING || force) {
            if (navigatorScrollListener != null) {
                navigatorScrollListener!!.onEnter(index, totalCount, enterPercent, leftToRight)
            }
            leavePercents.put(index, 1f - enterPercent)
        }
    }

    private fun dispatchOnLeave(index: Int, leavePercent: Float, leftToRight: Boolean, force: Boolean) {
        if (skimOver || index == lastIndex || scrollState == SCROLL_STATE_DRAGGING || (index == currentIndex - 1 || index == currentIndex + 1) && leavePercents[index, 0f] != 1f || force) {
            navigatorScrollListener?.onLeave(index, totalCount, leavePercent, leftToRight)
            leavePercents.put(index, leavePercent)
        }
    }

    private fun dispatchOnSelected(index: Int) {
        navigatorScrollListener?.onSelected(index, totalCount)
        unselectItems.put(index, false)
    }

    private fun dispatchOnDeselected(index: Int) {
        navigatorScrollListener?.onUnselected(index, totalCount)
        unselectItems.put(index, true)
    }

    fun onPageSelected(position: Int) {
        lastIndex = currentIndex
        currentIndex = position
        dispatchOnSelected(currentIndex)
        for (i in 0 until totalCount) {
            if (i == currentIndex) {
                continue
            }
            val deselected = unselectItems[i]
            if (!deselected) {
                dispatchOnDeselected(i)
            }
        }
    }

    fun onPageScrollStateChanged(state: Int) {
        scrollState = state
    }

    fun setTotalCount(totalCount: Int) {
        this.totalCount = totalCount
        unselectItems.clear()
        leavePercents.clear()
    }
}