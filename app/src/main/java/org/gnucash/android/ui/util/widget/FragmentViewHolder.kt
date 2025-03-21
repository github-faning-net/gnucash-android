package org.gnucash.android.ui.util.widget

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * [ViewHolder] implementation for handling [Fragment]s. Used in
 * [FragmentStateAdapter].
 */
class FragmentViewHolder private constructor(
    container: FrameLayout,
    private val pager: ViewPager2
) : RecyclerView.ViewHolder(container) {

    private val container: FrameLayout
        get() = itemView as FrameLayout

    private var fragment: Fragment? = null

    fun bind(fragment: Fragment, fragmentManager: FragmentManager) {
        val fragmentOld = this.fragment
        if (fragment == fragmentOld) {
            fragment.onResume()
            return // some fragment already bound
        }
        val container = this.container

        fragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewCreated(
                    fm: FragmentManager,
                    f: Fragment,
                    v: View,
                    savedInstanceState: Bundle?
                ) {
                    super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                    if (f == fragment) {
                        fm.unregisterFragmentLifecycleCallbacks(this)
                        if (v.parent === container) return
                        container.removeAllViews()
                        container.addView(v)
                        fragment.setMenuVisibility(layoutPosition == pager.currentItem)
                    }
                }
            }, false
        )

        fragment.setMenuVisibility(false)
        if (fragmentOld != null) {
            fragmentManager.beginTransaction()
                .remove(fragmentOld)
                .commitNowAllowingStateLoss()
        }
        fragmentManager.beginTransaction()
            .remove(fragment)
            .add(fragment, "f$itemId")
            .commitNowAllowingStateLoss()
        this.fragment = fragment
    }

    companion object {
        @JvmStatic
        fun create(parent: ViewGroup, pager: ViewPager2): FragmentViewHolder {
            val container = FrameLayout(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = ViewCompat.generateViewId()
                isSaveEnabled = false
            }
            return FragmentViewHolder(container, pager)
        }
    }
}