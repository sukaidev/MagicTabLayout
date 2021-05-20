package com.sukaidev.magictablayout.tab

/**
 * Create by sukaidev at 20/05/2021.
 * 抽象的TabView
 * @author sukaidev
 */
interface IMagicTab {

  fun onSelected()

  fun onUnSelected()

  fun onEnter()

  fun onLeave()
}