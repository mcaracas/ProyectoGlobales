/*
 * This is the source code of Calculadora de Impuestos v. 1.x.x.
 * It is licensed under GNU GPL v. 3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Edgar Santiago, 2021.
 */

package com.mozama.impuestos.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mozama.impuestos.fragments.IvaFragment
import com.mozama.impuestos.fragments.ResicoFragment
import com.mozama.impuestos.fragments.RetencionFragment
import com.mozama.impuestos.fragments.UmaFragment

class ViewPageAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> RetencionFragment.newInstance()
            1 -> ResicoFragment()
            2 -> IvaFragment.newInstance()
            3 -> UmaFragment.newInstance()
            else -> RetencionFragment.newInstance()
        }
    }

}