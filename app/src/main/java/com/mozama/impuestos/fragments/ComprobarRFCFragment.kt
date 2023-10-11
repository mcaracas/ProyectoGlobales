
package com.mozama.impuestos.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.josketres.rfcfacil.Rfc
import com.mozama.impuestos.R
import com.mozama.impuestos.utils.UtilsGraphic


/**
 * Fragment comprobar RFC persona física o moral ingresando nombre y fecha nac. o constitución
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
 * Copyright (C) 2022  Edgar Santiago
 */


private lateinit var radioFisica: RadioButton
private lateinit var radioMoral: RadioButton
private lateinit var txtPrimerNombre: TextInputEditText
private lateinit var fielPrimerNombre: TextInputLayout
private lateinit var txtPrimerApellido: TextInputEditText
private lateinit var fielPrimerApellido: TextInputLayout
private lateinit var txtSegundoApellido: TextInputEditText
private lateinit var fielSegundoApellido: TextInputLayout

private lateinit var txtFecha: TextInputEditText
private lateinit var fielFecha: TextInputLayout
private lateinit var txtNombreMoral: TextInputEditText
private lateinit var fielNombreMoral: TextInputLayout
private lateinit var tvRFC: TextView
private lateinit var layoutFisica: LinearLayout
private lateinit var layoutMoral: LinearLayout

class ComprobarRFCFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        setHasOptionsMenu(true)
        activity?.title = resources.getString(R.string.rfc)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comprobar_rfc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize(view)
        listener()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_rfc, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Detectar la opción del menú seleccionado
        return when (item.itemId) {
            R.id.menu_share -> {
                shareInfo()
                true
            }
            R.id.menu_delete -> {
                limpiar()
                true
            }
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareInfo() {
        val valorRFC = tvRFC.text
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, valorRFC)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
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

    private fun initialize(view:View){
        radioFisica = view.findViewById(R.id.rdoFisica)
        radioMoral = view.findViewById(R.id.rdoMoral)
        txtPrimerNombre = view.findViewById(R.id.txtPrimerNombre)
        fielPrimerNombre = view.findViewById(R.id.fielPrimerNombre)
        txtPrimerApellido = view.findViewById(R.id.txtPrimerApellido)
        fielPrimerApellido = view.findViewById(R.id.fielPrimerApellido)
        txtSegundoApellido = view.findViewById(R.id.txtSegundoApellido)
        fielSegundoApellido = view.findViewById(R.id.fielSegundoApellido)

        txtNombreMoral = view.findViewById(R.id.txtNombreMoral)
        fielNombreMoral = view.findViewById(R.id.fielNombreMoral)
        txtFecha = view.findViewById(R.id.txtFecha)
        fielFecha = view.findViewById(R.id.fielFecha)
        txtFecha.inputType = InputType.TYPE_NULL
        tvRFC = view.findViewById(R.id.tvRFC)
        layoutMoral = view.findViewById(R.id.layoutMoral)
        layoutFisica = view.findViewById(R.id.layoutFisica)

        radioFisica.isChecked = true
        mostrarFisica()
    }

    private fun getFecha(): String {
        return if(!txtFecha.text.isNullOrEmpty()){
            val fechaIngresada = txtFecha.text.toString()
            UtilsGraphic().fechaStringAInt(fechaIngresada)
        }else {
            UtilsGraphic().getFechaActual()
        }
    }

    private fun listener(){
        var fecha: String
        txtFecha.setOnClickListener{
            fecha = getFecha()
            mostrarDatePickerFecha(requireActivity(), txtFecha, fecha)
        }
        txtFecha.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideKeyboard()
                fecha = getFecha()
                mostrarDatePickerFecha(requireActivity(), txtFecha, fecha)
            }
        }

        radioFisica.setOnClickListener{
            mostrarFisica()
        }

        radioMoral.setOnClickListener{
            mostrarMoral()
        }
    }

    fun mostrarDatePickerFecha(activity: Activity, campoFecha: TextInputEditText, fecha:String ) {
        var fechaSeleccion: String

        val arrayFecha = fecha.split("-")
        val anio = arrayFecha[0].toInt()
        val mes = arrayFecha[1].toInt()-1
        val dia = arrayFecha[2].toInt()

        val dpd = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener {
                _, year, monthOfYear, dayOfMonth ->

            val mesDatePicker = monthOfYear+1
            fechaSeleccion = "$year-$mesDatePicker-$dayOfMonth"
            val fechaTexto = UtilsGraphic().fechaNumeroAString(fechaSeleccion)
            campoFecha.setText(fechaTexto)

            calcularRFC()

        }, anio, mes, dia)
        dpd.show()
        campoFecha.requestFocus()
    }

    private fun mostrarFisica() {
        layoutFisica.visibility = View.VISIBLE
        layoutMoral.visibility = View.GONE
        fielFecha.hint = resources.getString(R.string.fecha_nacimiento)
        limpiar()
    }

    private fun mostrarMoral() {
        layoutFisica.visibility = View.GONE
        layoutMoral.visibility = View.VISIBLE
        fielFecha.hint = resources.getString(R.string.fecha_constitucion)
        limpiar()
    }

    private fun validarIngresoFisica(): Boolean{
        return if(txtPrimerNombre.text.isNullOrEmpty()) {
            fielPrimerNombre.error = resources.getString(R.string.info_faltante)
            false
        }else true
    }

    private fun validarIngresoMoral():Boolean{
        return if(txtNombreMoral.text.isNullOrEmpty()){
            fielNombreMoral.error = resources.getString(R.string.info_faltante)
            false
        }else true
    }

    private fun calcularRFC() {
        val fecha = UtilsGraphic().fechaStringAInt( txtFecha.text.toString() )
        val arrayFecha = fecha.split("-").toTypedArray()
        val anio = arrayFecha[0].toInt()
        val mes = arrayFecha[1].toInt()
        val dia = arrayFecha[2].toInt()

        if(radioFisica.isChecked){
            if( validarIngresoFisica() ) {
                val nombre = txtPrimerNombre.text.toString().trim()
                val primerApellido = txtPrimerApellido.text.toString().trim()
                val segundoApellido = txtSegundoApellido.text.toString().trim()
                val rfc = Rfc.Builder()
                    .name(nombre)
                    .firstLastName(primerApellido)
                    .secondLastName(segundoApellido)
                    .birthday(dia, mes, anio)
                    .build()
                colocarResultado(rfc.toString())
            }
        }else{
            if (validarIngresoMoral()) {
                val nombre = txtNombreMoral.text.toString().trim()
                val rfc = Rfc.Builder()
                    .legalName(nombre)
                    .creationDate(dia, mes, anio)
                    .build()
                colocarResultado(rfc.toString())
            }
        }
    }

    private fun colocarResultado(rfc:String){
        limpiarField()
        tvRFC.text = rfc
        hideKeyboard()
    }

    private fun limpiarField(){
        fielPrimerNombre.error = ""
        fielPrimerApellido.error = ""
        fielSegundoApellido.error = ""
        fielNombreMoral.error = ""
    }

    private fun limpiar(){
        txtPrimerNombre.setText("")
        txtPrimerApellido.setText("")
        txtSegundoApellido.setText("")
        txtNombreMoral.setText("")
        txtFecha.setText("")
        tvRFC.text = ""
        hideKeyboard()
    }

    private fun Fragment.hideKeyboard() {
        val activity = this.activity
        if (activity is AppCompatActivity) {
            activity.hideKeyboard()
        }
    }
    private fun AppCompatActivity.hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            ComprobarRFCFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}