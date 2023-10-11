/*
 * This is the source code of Calculadora de Impuestos v. 1.x.x.
 * It is licensed under GNU GPL v. 3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Edgar Santiago, 2021.
 */
package com.mozama.impuestos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mozama.impuestos.fragments.MainFragment

class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = this.supportFragmentManager.beginTransaction()
        val fragmentMain = MainFragment.newInstance()
        val tag = resources.getString(R.string.principal)
        transaction.replace(R.id.container_main, fragmentMain, tag)
        transaction.commit()
    }

}