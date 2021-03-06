package com.sukaidev.magictablayout

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.sukaidev.magictablayout.ext.dp
import com.sukaidev.magictablayout.indicator.CommonIndicator
import com.sukaidev.magictablayout.indicator.CommonIndicator.Companion.MODE_WRAP_CONTENT
import com.sukaidev.magictablayout.indicator.IMagicIndicator
import com.sukaidev.magictablayout.indicator.NonIndicator
import com.sukaidev.magictablayout.navigator.BaseNavigator
import com.sukaidev.magictablayout.navigator.BaseNavigator.Companion.MODE_SCROLLABLE
import com.sukaidev.magictablayout.navigator.BaseNavigatorAdapter
import com.sukaidev.magictablayout.navigator.CommonNavigator
import com.sukaidev.magictablayout.tab.CommonTitleTab
import com.sukaidev.magictablayout.tab.CommonTitleTab.Companion.SELECT
import com.sukaidev.magictablayout.tab.IMagicTab

/**
 * Create by sukaidev at 20/05/2021.
 * 自定义的TabLayout
 * @author sukaidev
 */
class MagicTabLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var adapter: BaseNavigatorAdapter?
        get() = navigator.adapter
        set(value) {
            navigator.adapter = value
        }

    private var viewPager: ViewPager? = null
    private var viewPager2: ViewPager2? = null

    private var navigator: BaseNavigator = CommonNavigator(context)

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
        ViewPager.OnAdapterChangeListener { _, _, _ -> navigator.notifyDataSetChanged() }
    }

    private val onAdapterDataSetObserver by lazy(LazyThreadSafetyMode.NONE) {
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                navigator.notifyDataSetChanged()
            }
        }
    }

    /** 默认配置 */
    private var scrollPivotX = 0.5f
    private var enablePivotScroll = false
    private var isFollowTouch = false
    private var isIndicatorOnTop = false
    private var isSkimOverEnable = true
    private var isSmoothScrollEnable = true

    private var tabSelectedTextColor = 0
    private var tabUnselectedTextColor = 0
    private var tabSelectedTextSize = 0f
    private var tabUnselectedTextSize = 0f

    private var indicatorColor = 0
    private var indicatorWidth = 0
    private var showIndicator = true

    private var tabBoldMode = SELECT
    private var tabMode = MODE_SCROLLABLE

    init {
        context.obtainStyledAttributes(attrs, R.styleable.MagicTabLayout).apply {
            tabMode = getInt(R.styleable.MagicTabLayout_tabNavigateMode, MODE_SCROLLABLE)

            scrollPivotX = getFloat(R.styleable.MagicTabLayout_scrollPivotX, 0.5f)
            isFollowTouch = getBoolean(R.styleable.MagicTabLayout_isFollowTouch, false)
            isSkimOverEnable = getBoolean(R.styleable.MagicTabLayout_isSkimOverEnable, true)
            isIndicatorOnTop = getBoolean(R.styleable.MagicTabLayout_isIndicatorOnTop, false)
            enablePivotScroll = getBoolean(R.styleable.MagicTabLayout_enablePivotScroll, false)
            isSmoothScrollEnable = getBoolean(R.styleable.MagicTabLayout_isSmoothScrollEnable, true)

            tabSelectedTextColor = getColor(R.styleable.MagicTabLayout_tabSelectedTextColor, Color.BLACK)
            tabSelectedTextSize = getDimension(R.styleable.MagicTabLayout_tabSelectedTextSize, 18f.dp)

            tabUnselectedTextColor = getColor(R.styleable.MagicTabLayout_tanUnselectedTextColor, Color.GRAY)
            tabUnselectedTextSize = getDimension(R.styleable.MagicTabLayout_tabUnSelectedTextSize, 18f.dp)

            tabBoldMode = getInt(R.styleable.MagicTabLayout_tabBoldMode, SELECT)

            indicatorColor = getColor(R.styleable.MagicTabLayout_indicatorColor, Color.BLACK)
            indicatorWidth = getDimension(R.styleable.MagicTabLayout_indicatorWidth, 10f.dp).toInt()
            showIndicator = getBoolean(R.styleable.MagicTabLayout_showIndicator, true)
            if (indicatorWidth == 0) showIndicator = false
        }.recycle()

        initDefaultAdapter()
    }

    /**
     * 默认的适配器
     */
    private fun initDefaultAdapter() {
        (navigator as? CommonNavigator)?.let {
            it.mode = tabMode
            it.enablePivotScroll = enablePivotScroll
            it.isFollowTouch = isFollowTouch
            it.isIndicatorOnTop = isIndicatorOnTop
            it.isSkimOverEnable = isSkimOverEnable
            it.isSmoothScrollEnable = isSmoothScrollEnable
            it.scrollPivotX = scrollPivotX
        }
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(navigator, lp)
        navigator.onAttachToTabLayout()
    }

    fun setNavigator(navigator: BaseNavigator) {
        if (navigator == this.navigator) return
        this.navigator.onDetachFromTabLayout()
        removeAllViews()
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(navigator, lp)
        navigator.onAttachToTabLayout()
        this.navigator = navigator
    }

    /**
     * 当前页面滚动时，这个方法应该被调用
     *
     * @param position 当前显示的第一个页面的index 如果positionOffset不为0，则position+1将会显示
     * @param positionOffset 当前页面的偏移指数，取值范围[0,1)
     * @param positionOffsetPixels 当前页面的偏移量
     *
     * @see [ViewPager.OnPageChangeListener.onPageScrolled]
     * @see [ViewPager2.OnPageChangeCallback.onPageScrolled]
     */
    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        onPageScrolled(position, positionOffset, positionOffsetPixels, false)
    }

    private fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int, invokeByViewPager: Boolean) {
        if (!invokeByViewPager && (viewPager != null || viewPager2 != null)) {
            throw IllegalStateException("Once MagicTabLayout was bound to ViewPager or ViewPager2 , you should never invoke this method by yourself.")
        }
        navigator.onPageScrolled(position, positionOffset, positionOffsetPixels)
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
        navigator.onPageSelected(position)
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
        navigator.onPageScrollStateChanged(state)
    }

    /**
     * 类似官方TabLayout的使用方式，直接与[ViewPager]绑定
     * 这种情况下将会使用默认参数构造默认的适配器
     */
    fun setupWithViewPager(viewPager: ViewPager, titles: List<String>) {
        if (this.viewPager == viewPager) return
        this.viewPager?.removeOnPageChangeListener(onPageChangeListener)
        this.viewPager?.removeOnAdapterChangeListener(onAdapterChangeListener)

        setDefaultAdapter(titles) { pos, _ ->
            viewPager.currentItem = pos
        }

        viewPager.addOnPageChangeListener(onPageChangeListener)
        viewPager.addOnAdapterChangeListener(onAdapterChangeListener)
        this.viewPager = viewPager
    }

    /**
     * 类似官方TabLayout的使用方式，直接与[ViewPager2]绑定
     * 这种情况下将会使用默认参数构造默认的适配器
     */
    fun setupWithViewPager2(viewPager2: ViewPager2, titles: List<String>) {
        if (this.viewPager2 == viewPager2) return
        this.viewPager2?.unregisterOnPageChangeCallback(onPageChangeCallback)
        this.viewPager2?.adapter?.unregisterAdapterDataObserver(onAdapterDataSetObserver)

        setDefaultAdapter(titles) { pos, _ ->
            viewPager2.currentItem = pos
        }

        viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
        viewPager2.adapter?.registerAdapterDataObserver(onAdapterDataSetObserver)
        this.viewPager2 = viewPager2
    }

    /**
     * 使用默认参数构造默认适配器
     */
    private fun setDefaultAdapter(titles: List<String>, block: (pos: Int, tab: IMagicTab) -> Unit) {
        val indicator = CommonIndicator(context)
        indicator.mode = MODE_WRAP_CONTENT
        indicator.setColors(indicatorColor)
        indicator.indicatorWidth = indicatorWidth

        val adapter = object : BaseNavigatorAdapter(context) {

            override fun setTabViews() = titles.map {
                CommonTitleTab.Builder(context)
                        .setTitle(it)
                        .setAlignBaseLineMode(true)
                        .setSelectTextColor(tabSelectedTextColor)
                        .setUnselectTextColor(tabUnselectedTextColor)
                        .setSelectTextSize(TypedValue.COMPLEX_UNIT_PX, tabSelectedTextSize)
                        .setUnselectTextSize(TypedValue.COMPLEX_UNIT_PX, tabUnselectedTextSize)
                        .build()
            }

            override fun setIndicator(): IMagicIndicator {
                if (!showIndicator) return NonIndicator(context)
                return indicator
            }
        }
        adapter.addOnTabClickListener(object : BaseNavigatorAdapter.OnTabClickListener {
            override fun onTabClick(position: Int, tab: IMagicTab) {
                block.invoke(position, tab)
            }
        })
        this.adapter = adapter
    }

    /**
     * 仅绑定ViewPager的滑动监听
     * 需调用[setNavigator]配置Navigator
     */
    fun bind(viewPager: ViewPager) {
        if (this.viewPager == viewPager) return

        this.viewPager?.removeOnPageChangeListener(onPageChangeListener)
        this.viewPager?.removeOnAdapterChangeListener(onAdapterChangeListener)

        viewPager.addOnPageChangeListener(onPageChangeListener)
        viewPager.addOnAdapterChangeListener(onAdapterChangeListener)
        this.viewPager = viewPager
    }

    /**
     * 仅绑定ViewPager2的滑动监听
     * 需调用[setNavigator]配置Navigator
     */
    fun bind(viewPager2: ViewPager2) {
        if (this.viewPager2 == viewPager2) return

        this.viewPager2?.unregisterOnPageChangeCallback(onPageChangeCallback)
        this.viewPager2?.adapter?.unregisterAdapterDataObserver(onAdapterDataSetObserver)

        viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
        viewPager2.adapter?.registerAdapterDataObserver(onAdapterDataSetObserver)
        this.viewPager2 = viewPager2
    }

    fun setTextColor(@ColorInt color: Int, @ColorInt selectedColor: Int) {
        tabSelectedTextColor = selectedColor
        tabUnselectedTextColor = color
        val tabCount = navigator.adapter?.tabCount ?: 0
        val current = when {
            null != viewPager -> viewPager!!.currentItem
            null != viewPager2 -> viewPager2!!.currentItem
            else -> return
        }
        for (i in 0 until tabCount) {
            val tab = navigator.adapter?.getTab(i) as? CommonTitleTab ?: continue
            tab.selectTextColor = selectedColor
            tab.unSelectTextColor = color
            if (i == current) {
                tab.onTabSelected(i, tabCount)
            } else {
                tab.onTabUnselected(i, tabCount)
            }
        }
    }

    /**
     * 获取[index]对应tab
     */
    fun getTabAt(index: Int) = navigator.adapter?.getTab(index)

    /**
     * 设置指示器颜色
     */
    fun setIndicatorColor(@ColorInt color: Int) {
        indicatorColor = color
        val indicator = navigator.adapter?.indicator as? CommonIndicator ?: return
        indicator.setColors(color)
    }
}