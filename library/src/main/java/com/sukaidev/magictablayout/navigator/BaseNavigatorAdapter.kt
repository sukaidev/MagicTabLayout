package com.sukaidev.magictablayout.navigator

import android.content.Context
import android.database.DataSetObservable
import android.database.DataSetObserver
import com.sukaidev.magictablayout.indicator.IMagicIndicator
import com.sukaidev.magictablayout.tab.IMagicTab

/**
 * Create by sukaidev at 21/05/2021.
 * 导航适配器
 * @author sukaidev
 */
abstract class BaseNavigatorAdapter {

    abstract fun getCount(): Int

    private val dataSetObservable = DataSetObservable()

    abstract fun getTabView(context: Context, index: Int): IMagicTab

    abstract fun getIndicator(context: Context): IMagicIndicator

    open fun getTitleWeight(context: Context, index: Int) = 1

    fun registerDataSetObserver(observer: DataSetObserver) = dataSetObservable.registerObserver(observer)

    fun unregisterDataSetObserver(observer: DataSetObserver) = dataSetObservable.unregisterObserver(observer)

    fun notifyDataSetChanged() = dataSetObservable.notifyChanged()

    fun notifyDataSetInvalidated() = dataSetObservable.notifyInvalidated()
}