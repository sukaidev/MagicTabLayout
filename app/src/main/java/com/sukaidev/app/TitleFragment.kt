package com.sukaidev.app

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.sukaidev.magictablayout.ext.dp

/**
 * Create by sukaidev at 21/05/2021.
 * @author sukaidev
 */
class TitleFragment : Fragment() {

    private val title: String by lazy {
        arguments?.getString(KEY_TITLE) ?: ""
    }
    private var textView: AppCompatTextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val frameLayout = FrameLayout(requireContext())
        frameLayout.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

        textView = AppCompatTextView(requireContext())
        textView?.textSize = 18f.dp
        val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        lp.gravity = Gravity.CENTER
        textView?.layoutParams = lp
        frameLayout.addView(textView)
        return frameLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView?.text  = title
    }

    companion object {
        private const val KEY_TITLE = "key_title"
        fun newInstance(title: String): TitleFragment {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            val fragment = TitleFragment()
            fragment.arguments = args
            return fragment
        }
    }
}