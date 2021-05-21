package com.sukaidev.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sukaidev.magictablayout.MagicTabLayout

class MainActivity : AppCompatActivity() {

    private val titles = listOf("首页", "关注", "详情", "个人")
    private val fragments = listOf(
            TitleFragment.newInstance(titles[0]),
            TitleFragment.newInstance(titles[1]),
            TitleFragment.newInstance(titles[2]),
            TitleFragment.newInstance(titles[3]))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout = findViewById<MagicTabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)

        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount() = fragments.size

            override fun getItem(position: Int) = fragments[position]
        }
        tabLayout.setupWithViewPager(viewPager, titles)
    }
}