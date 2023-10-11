package com.mozama.impuestos.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.mozama.impuestos.R
import com.mozama.impuestos.adapters.ViewPageAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Fragment principal para visualizar el ViewPager2 y TabLayout
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2021  Edgar Santiago
 */

class MainFragment : Fragment() {

    private lateinit var tabView: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPageAdapter: ViewPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        setHasOptionsMenu(true)
        activity?.title = resources.getString(R.string.app_name)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = activity?.findViewById(R.id.viewPage)!!
        tabView = activity?.findViewById(R.id.tabView)!!

        viewPageAdapter = ViewPageAdapter(this)
        viewPager.adapter = viewPageAdapter

        TabLayoutMediator(tabView, viewPager) { tab, position ->
            when(position){
                0 ->tab.text = resources.getString(R.string.retenciones)
                1 ->tab.text = resources.getString(R.string.resico)
                2 ->tab.text = resources.getString(R.string.iva)
                3 ->tab.text = resources.getString(R.string.uma_smg)
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        activity?.title = resources.getString(R.string.app_name)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Detectar la opción del menú seleccionado
        return when (item.itemId) {
            R.id.calificar -> {
                abrirEnlacePlay("com.mozama.impuestos")
                true
            }
            R.id.comparirApp -> {
                shareApp()
                true
            }
            R.id.menu_productosNot->{
                abrirEnlacePlay("com.mozama.notable_products")
                true
            }
            R.id.menu_mcm -> {
                abrirEnlacePlay("com.mozama.mcm_mcd")
                true
            }
            R.id.menu_hexa -> {
                abrirEnlacePlay("mx.com.mozama.hexatext")
                true
            }
            R.id.menu_trigonometria -> {
                abrirEnlacePlay("com.mozama.trigonometria")
                true
            }
            R.id.menu_recta -> {
                abrirEnlacePlay("com.mozama.lineaRecta")
                true
            }
            R.id.menu_ajustes->{
                mostrarAjustes()
                true
            }
            R.id.menu_rfc->{
                mostrarComprobarRFC()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarComprobarRFC() {
        val transaction = parentFragmentManager.beginTransaction()
        val fragmentRFC = ComprobarRFCFragment.newInstance()
        transaction.replace(R.id.container_main, fragmentRFC)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun mostrarAjustes() {
        val transaction = parentFragmentManager.beginTransaction()
        val fragmentAjustes = AjustesFragment.newInstance()
        transaction.replace(R.id.container_main, fragmentAjustes)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun abrirEnlacePlay(idApp:String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://play.google.com/store/apps/details?id=$idApp")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    private fun shareApp(){
        val url = "https://play.google.com/store/apps/details?id=com.mozama.impuestos&hl=es"
        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }, null)
        startActivity(share)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}