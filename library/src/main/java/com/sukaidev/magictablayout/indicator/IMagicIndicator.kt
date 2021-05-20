package com.sukaidev.magictablayout.indicator

/**
 * Create by sukaidev at 20/05/2021.
 * 抽象指示器
 *
 * @author sukaidev
 */
interface IMagicIndicator {
  fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)

  fun onPageSelected(position: Int)

  fun onPageScrollStateChanged(state: Int)

  fun onPositionDataProvide(dataList: List<*>?)
}