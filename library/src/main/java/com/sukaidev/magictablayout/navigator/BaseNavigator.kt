package com.sukaidev.magictablayout.navigator

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import androidx.annotation.IntDef
import com.sukaidev.magictablayout.indicator.IMagicIndicator

/**
 * Create by sukaidev at 21/05/2021.
 * 导航栏基类
 * @author sukaidev
 */
abstract class BaseNavigator @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr), IMagicNavigator {

    abstract var mode: @Mode Int
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

    @MustBeDocumented
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [MODE_SCROLLABLE, MODE_FIXED])
    @Target(AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
    annotation class Mode

    companion object {
        // 可滚动
        const val MODE_SCROLLABLE = 0

        // 自动填充
        const val MODE_FIXED = 1
    }
}