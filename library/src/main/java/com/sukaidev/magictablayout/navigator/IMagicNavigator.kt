package com.sukaidev.magictablayout.navigator

import  com.sukaidev.magictablayout.MagicTabLayout

/**
 * Create by sukaidev at 20/05/2021.
 * 抽象导航器
 *
 * @author sukaidev
 */
interface IMagicNavigator {

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)

    fun onPageSelected(position: Int)

    fun onPageScrollStateChanged(state: Int)

    /**
     * 当Navigator与[MagicTabLayout]绑定时调用
     */
    fun onAttachToTabLayout()

    /**
     * 当Navigator从[MagicTabLayout]上移除时调用
     */
    fun onDetachFromTabLayout()

    /**
     * ViewPager内容改变时需要先调用此方法
     * 自定义的Navigator应当遵守此约定
     */
    fun notifyDataSetChanged()
}