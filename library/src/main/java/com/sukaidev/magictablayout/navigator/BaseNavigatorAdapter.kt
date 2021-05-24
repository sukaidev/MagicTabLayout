package com.sukaidev.magictablayout.navigator

import android.content.Context
import android.database.DataSetObservable
import android.database.DataSetObserver
import android.view.View
import com.sukaidev.magictablayout.indicator.IMagicIndicator
import com.sukaidev.magictablayout.indicator.NonIndicator
import com.sukaidev.magictablayout.tab.IMagicTab

/**
 * Create by sukaidev at 21/05/2021.
 * 导航适配器
 * @author sukaidev
 */
abstract class BaseNavigatorAdapter(context: Context) {

    var tabCount = 0
        get() = tabs.size
        private set

    private var tabs = mutableListOf<IMagicTab>()

    private var listeners = mutableListOf<OnTabClickListener>()

    var indicator: IMagicIndicator = NonIndicator(context)

    private val dataSetObservable = DataSetObservable()

    /**
     * 当adapter与navigator绑定时调用此方法
     */
    fun onAttachToNavigator() {
        tabs.clear()
        tabs.addAll(setTabViews())

        indicator = setIndicator()
    }

    /**
     * 当adapter与navigator解绑时调用此方法
     */
    fun onDetachFromNavigator() {
        tabs.clear()
    }

    fun addTab(tab: IMagicTab) {
        tabs.add(tab)
        notifyDataSetChanged()
    }

    fun addTab(index: Int, tab: IMagicTab) {
        tabs.add(index, tab)
        notifyDataSetChanged()
    }

    fun onTabClicked(position: Int, tab: IMagicTab) {
        listeners.forEach { it.onTabClick(position, tab) }
    }

    fun addOnTabClickListener(listener: OnTabClickListener) {
        listeners.add(listener)
    }

    fun removeOnTabClickListener(listener: OnTabClickListener) {
        listeners.remove(listener)
    }

    fun removeAllListeners() {
        listeners.clear()
    }

    fun getTab(index: Int) = tabs[index]

    open fun setTabWeight(index: Int) = 1f

    abstract fun setTabViews(): List<IMagicTab>

    abstract fun setIndicator(): IMagicIndicator

    fun registerDataSetObserver(observer: DataSetObserver) = dataSetObservable.registerObserver(observer)

    fun unregisterDataSetObserver(observer: DataSetObserver) = dataSetObservable.unregisterObserver(observer)

    fun notifyDataSetChanged() = dataSetObservable.notifyChanged()

    fun notifyDataSetInvalidated() = dataSetObservable.notifyInvalidated()

    @FunctionalInterface
    interface OnTabClickListener {
        fun onTabClick(position: Int, tab: IMagicTab)
    }
}