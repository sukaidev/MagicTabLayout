package com.sukaidev.magictablayout.navigator

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import com.sukaidev.magictablayout.indicator.IMagicIndicator

/**
 * Create by sukaidev at 21/05/2021.
 * 导航栏基类
 * @author sukaidev
 */
abstract class BaseNavigator @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr), IMagicNavigator {

    abstract var adapter: BaseNavigatorAdapter?
    abstract val indicator: IMagicIndicator?

    protected val observer = object : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            onDataSetChanged()
        }
    }

    open fun initViews() {}

    open fun destroyViews() {}

    abstract fun onDataSetChanged()

    override fun onAttachToTabLayout() {
        initViews()
    }

    override fun onDetachFromTabLayout() {
        destroyViews()
    }

    override fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }
}