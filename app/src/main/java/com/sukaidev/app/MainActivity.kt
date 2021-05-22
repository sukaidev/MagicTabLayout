package com.sukaidev.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sukaidev.magictablayout.MagicTabLayout
import com.sukaidev.magictablayout.tab.ScalableIconTab

class MainActivity : AppCompatActivity() {

    private val titles = listOf("首页", "关注", "详情", "个人")
    private val fragments = mutableListOf(
            TitleFragment.newInstance(titles[0]),
            TitleFragment.newInstance(titles[1]),
            TitleFragment.newInstance(titles[2]),
            TitleFragment.newInstance(titles[3]))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout = findViewById<MagicTabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val button = findViewById<AppCompatButton>(R.id.button)

        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount() = fragments.size

            override fun getItem(position: Int) = fragments[position]
        }
        tabLayout.setupWithViewPager(viewPager, titles)

        button.setOnClickListener {
            val adapter = tabLayout.getAdapter()
            adapter?.addTab(ScalableIconTab(this).apply {
                setIcon(R.drawable.ic_launcher_background)
                setAlignBaseLineMode(true)
            })
            fragments.add(TitleFragment.newInstance("增加"))
            viewPager.adapter?.notifyDataSetChanged()
        }
    }
}