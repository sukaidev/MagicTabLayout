package com.sukaidev.magictablayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.sukaidev.magictablayout.navigator.CommonNavigator
import com.sukaidev.magictablayout.navigator.IMagicNavigator

/**
 * Create by sukaidev at 20/05/2021.
 *
 * 框架入口
 * @author sukaidev
 */
class MagicTabLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var viewPager: ViewPager? = null
    private var viewPager2: ViewPager2? = null

    private var navigator: IMagicNavigator? = null

    private val onPageChangeListener: ViewPager.OnPageChangeListener by lazy(LazyThreadSafetyMode.NONE) {
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                onPageScrolled(position, positionOffset, positionOffsetPixels, true)
            }

            override fun onPageSelected(position: Int) {
                onPageSelected(position, true)
            }

            override fun onPageScrollStateChanged(state: Int) {
                onPageScrollStateChanged(state, true)
            }
        }
    }
    private val onPageChangeCallback: ViewPager2.OnPageChangeCallback by lazy(LazyThreadSafetyMode.NONE) {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                onPageScrolled(position, positionOffset, positionOffsetPixels, true)
            }

            override fun onPageSelected(position: Int) {
                onPageSelected(position, true)
            }

            override fun onPageScrollStateChanged(state: Int) {
                onPageScrollStateChanged(state, true)
            }
        }
    }

    private val onAdapterChangeListener by lazy(LazyThreadSafetyMode.NONE) {
        ViewPager.OnAdapterChangeListener { _, _, _ -> navigator?.notifyDataSetChanged() }
    }

    private val onAdapterDataSetObserver by lazy(LazyThreadSafetyMode.NONE) {
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                navigator?.notifyDataSetChanged()
            }
        }
    }

    private var scrollPivotX = 0.5f
    private var enablePivotScroll = false
    private var isFollowTouch = false
    private var isIndicatorOnTop = false
    private var isSkimOverEnable = true
    private var isSmoothScrollEnable = true

    init {
        context.obtainStyledAttributes(attrs, R.styleable.MagicTabLayout).apply {
            scrollPivotX = getFloat(R.styleable.MagicTabLayout_scrollPivotX, 0.5f)
            isFollowTouch = getBoolean(R.styleable.MagicTabLayout_isFollowTouch, false)
            isSkimOverEnable = getBoolean(R.styleable.MagicTabLayout_isSkimOverEnable, true)
            isIndicatorOnTop = getBoolean(R.styleable.MagicTabLayout_isIndicatorOnTop, false)
            enablePivotScroll = getBoolean(R.styleable.MagicTabLayout_enablePivotScroll, false)
            isSmoothScrollEnable = getBoolean(R.styleable.MagicTabLayout_isSmoothScrollEnable, true)
        }.recycle()
    }

    fun setNavigator(navigator: IMagicNavigator) {
        if (navigator == this.navigator) return
        this.navigator?.onDetachFromTabLayout()
        removeAllViews()
        if (navigator is View) {
            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            addView(navigator, lp)
            navigator.onAttachToTabLayout()
        }
        this.navigator = navigator
    }

    /**
     * 当前页面滚动时，这个方法应该被调用
     * @see [ViewPager.OnPageChangeListener.onPageScrolled]
     * @see [ViewPager2.OnPageChangeCallback.onPageScrolled]
     *
     * @param position 当前显示的第一个页面的index 如果positionOffset不为0，则position+1将会显示
     * @param positionOffset 当前页面的偏移指数，取值范围[0,1)
     * @param positionOffsetPixels 当前页面的偏移量
     */
    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        onPageScrolled(position, positionOffset, positionOffsetPixels, false)
    }

    private fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int, invokeByViewPager: Boolean) {
        if (!invokeByViewPager && (viewPager != null || viewPager2 != null)) {
            throw IllegalStateException("Once MagicTabLayout was bound to ViewPager or ViewPager2 , you should never invoke this method by yourself.")
        }
        navigator?.onPageScrolled(position, positionOffset, positionOffsetPixels)
    }

    /**
     * 当前页面被选中时，这个方法应该被调用
     * @see [ViewPager.OnPageChangeListener.onPageSelected]
     * @see [ViewPager2.OnPageChangeCallback.onPageSelected]
     *
     * @param position 当前选中的页面index
     */
    fun onPageSelected(position: Int) {
        onPageSelected(position, false)
    }

    private fun onPageSelected(position: Int, invokeByViewPager: Boolean) {
        if (!invokeByViewPager && (viewPager != null || viewPager2 != null)) {
            throw IllegalStateException("Once MagicTabLayout was bound to ViewPager or ViewPager2 , you should never invoke this method by yourself.")
        }
        navigator?.onPageSelected(position)
    }

    /**
     * 当前页面滚动状态改变时，这个方法应该被调用
     * @see [ViewPager.OnPageChangeListener.onPageScrollStateChanged]
     * @see [ViewPager2.OnPageChangeCallback.onPageScrollStateChanged]
     *
     * @param state [ViewPager2.ScrollState]
     */
    fun onPageScrollStateChanged(@ViewPager2.ScrollState state: Int) {
        onPageScrollStateChanged(state, false)
    }

    private fun onPageScrollStateChanged(@ViewPager2.ScrollState state: Int, invokeByViewPager: Boolean) {
        if (!invokeByViewPager && (viewPager != null || viewPager2 != null)) {
            throw IllegalStateException("Once MagicTabLayout was bound to ViewPager or ViewPager2 , you should never invoke this method by yourself.")
        }
        navigator?.onPageScrollStateChanged(state)
    }

    /**
     * 类似官方TabLayout的使用方式，直接与[ViewPager]绑定
     */
    fun setupWithViewPager(viewPager: ViewPager) {
        if (this.viewPager == viewPager) return
        this.viewPager?.removeOnPageChangeListener(onPageChangeListener)
        this.viewPager?.removeOnAdapterChangeListener(onAdapterChangeListener)

        setDefaultNavigator()
        viewPager.addOnPageChangeListener(onPageChangeListener)
        viewPager.addOnAdapterChangeListener(onAdapterChangeListener)
        this.viewPager = viewPager
    }

    /**
     * 类似官方TabLayout的使用方式，直接与[ViewPager2]绑定
     * @param autoRefresh 当viewPager2内容改变时是否自动刷新
     */
    fun setupWithViewPager2(viewPager2: ViewPager2) {
        if (this.viewPager2 == viewPager2) return
        this.viewPager2?.unregisterOnPageChangeCallback(onPageChangeCallback)
        this.viewPager2?.adapter?.unregisterAdapterDataObserver(onAdapterDataSetObserver)

        setDefaultNavigator()
        viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
        viewPager2.adapter?.registerAdapterDataObserver(onAdapterDataSetObserver)
        this.viewPager2 = viewPager2
    }

    private fun setDefaultNavigator() {
        val navigator = CommonNavigator(context)
        navigator.enablePivotScroll = enablePivotScroll
        navigator.isFollowTouch
        navigator.isIndicatorOnTop
        navigator.isSkimOverEnable
        navigator.isSmoothScrollEnable
        navigator.scrollPivotX
        setNavigator(navigator)
    }

    fun bind(viewPager: ViewPager) {
        if (this.viewPager == viewPager) return
        if (navigator == null) throw  IllegalStateException("set navigator before bind to a viewPager.")

        this.viewPager?.removeOnPageChangeListener(onPageChangeListener)
        this.viewPager?.removeOnAdapterChangeListener(onAdapterChangeListener)

        viewPager.addOnPageChangeListener(onPageChangeListener)
        viewPager.addOnAdapterChangeListener(onAdapterChangeListener)
        this.viewPager = viewPager
    }

    fun bind(viewPager2: ViewPager2) {
        if (this.viewPager2 == viewPager2) return
        if (navigator == null) throw  IllegalStateException("set navigator before bind to a viewPager.")

        this.viewPager2?.unregisterOnPageChangeCallback(onPageChangeCallback)
        this.viewPager2?.adapter?.unregisterAdapterDataObserver(onAdapterDataSetObserver)

        viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
        viewPager2.adapter?.registerAdapterDataObserver(onAdapterDataSetObserver)
        this.viewPager2 = viewPager2
    }
}