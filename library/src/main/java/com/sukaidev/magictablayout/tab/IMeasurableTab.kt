package com.sukaidev.magictablayout.tab

/**
 * Create by sukaidev at 20/05/2021.
 * 精确内容区域的Tab
 * 用于绘制indicator时可以保证长度与内容区域一致
 * @author sukaidev
 */
interface IMeasurableTab : IMagicTab {

    /**
     * Tab绘制区域左边界
     */
    fun getContentLeft(): Int

    /**
     * Tab绘制区域上边界
     */
    fun getContentTop(): Int

    /**
     * Tab绘制区域右边界
     */
    fun getContentRight(): Int

    /**
     * Tab绘制区域下边界
     */
    fun getContentBottom(): Int
}