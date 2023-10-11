/**
* RetencionesFragment, Fragment of cal tax and values to invoice whit tax Retenidos
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


package com.mozama.impuestos.fragments
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.mozama.impuestos.R
import com.mozama.impuestos.utils.DialogFragment
import com.mozama.impuestos.utils.Operations
import com.mozama.impuestos.utils.UtilsGraphic

class RetencionFragment : Fragment() {
    private var nResultados = 0
    private val limiteAds = 4

    private lateinit var txtSubtotal: EditText
    private lateinit var fieldIva : TextInputLayout    
    private lateinit var txtIva: EditText
    private lateinit var txtIsrR: EditText
    private lateinit var fieldIvaR: TextInputLayout
    private lateinit var txtIvaR: EditText
    private lateinit var lyCedular: LinearLayout
    private lateinit var spinCedular : Spinner
    private lateinit var fieldCedular: TextInputLayout
    private lateinit var fieldPercentCedular: TextInputLayout
    private lateinit var txtPercentCedular: EditText
    private lateinit var txtCedular: EditText
    private lateinit var txtTotal: EditText
    private lateinit var spinIva : Spinner
    private lateinit var icInfoRetenciones: ImageView

    private var configLocales = 0

    private var IN_OPTION = 0
    private val IN_SUBTOTAL = 1
    private val IN_IVA = 2
    private val IN_ISR_R = 3
    private val IN_IVA_R = 4
    private val IN_TOTAL = 5
    private var percentIva = 0.0
    private var percentCedular = 0.0

    private var subtotal = 0.0
    private var iva = 0.0
    private var isrR = 0.0
    private var ivaR = 0.0
    private var cedular = 0.0
    private var total = 0.0

    //Para identificar quién modifica el valor de los EditText
    //evitar ciclo infinito en addTextChangedListener
    private val TAG_SYSTEM = "system"
    private val TAG_USER = "user"

    private val percentIsrRetenido = 0.10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        //recibir devoluciones de llamada relacionadas con el menú.
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_retencion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fieldIva = view.findViewById(R.id.fieldIva)
        fieldIvaR = view.findViewById(R.id.fieldIvaR)

        txtSubtotal = view.findViewById(R.id.txtSubtotal)
        txtIva = view.findViewById(R.id.txtIva)
        txtIsrR = view.findViewById(R.id.txtIsrR)
        txtIvaR = view.findViewById(R.id.txtIvaR)
        txtTotal = view.findViewById(R.id.txtTotal)
        spinIva = view.findViewById(R.id.spinIva)
        icInfoRetenciones = view.findViewById(R.id.icInfoRetenciones)

        lyCedular    = view.findViewById(R.id.lyCedular)
        spinCedular  = view.findViewById(R.id.spinCedular)
        fieldCedular = view.findViewById(R.id.fieldCedular)
        fieldPercentCedular = view.findViewById(R.id.fieldPercentCedular)
        txtPercentCedular = view.findViewById(R.id.txtPercentCedular)
        txtCedular = view.findViewById(R.id.txtCedular)

        setup()
        setItemIva()
        setItemCedular()
        changeElements()
        hideKeyboard()

        verificViewCedular()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Detectar la opción del menú seleccionado por el usuario
        return when (item.itemId) {
            R.id.menu_delete -> {
                IN_OPTION = 0
                hideKeyboard()
                cleaner()
                true
            }
            R.id.menu_share -> {
                shareInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        verificViewCedular()
        hideKeyboard()
    }

    private fun setup(){
        txtSubtotal.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                hideKeyboard()
                nResultados++
                validarIntersticial()
                true
            }else false
        }

        txtTotal.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                hideKeyboard()
                nResultados++
                validarIntersticial()
                true
            }else false
        }
    }

    private fun verificViewCedular(){
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val configKeyLocales = resources.getString(R.string.imp_config)

        configLocales = sharedPref!!.getInt(configKeyLocales, 0)
        if(configLocales == 0){
            lyCedular.visibility = View.GONE
        }else{
            lyCedular.visibility = View.VISIBLE
        }
    }


    private fun changeElements() {
        spinIva.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 2) hideIva()
                else showIva()
                calc(IN_OPTION)
                hideKeyboard()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { //Another interface callback
            }
        }

        spinCedular.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        UtilsGraphic().hideCedular( fieldPercentCedular, fieldCedular)
                        cedular = 0.0
                        percentCedular = 0.0
                    }
                    5 -> UtilsGraphic().showOtroCedular( fieldPercentCedular, fieldCedular )
                    else -> UtilsGraphic().showCedular( fieldPercentCedular, fieldCedular )
                }
                calc(IN_OPTION)
                hideKeyboard()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        txtPercentCedular.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (txtPercentCedular.text.toString().isNotEmpty()) {
                    val valorString = txtPercentCedular.text.toString()
                    val valorF: Float?  = valorString.toFloatOrNull()
                    valorF?.let { calc(IN_OPTION) }
                        ?:run{UtilsGraphic().showToast(resources.getString(R.string.verifica_cedular), requireContext())}
                }else UtilsGraphic().showToast(resources.getString(R.string.verifica_cedular), requireContext())
                hideKeyboard()
            }
            false
        }

        txtSubtotal.tag = TAG_USER
        txtIva.tag = TAG_USER
        txtIsrR.tag = TAG_USER
        txtIvaR.tag = TAG_USER
        txtTotal.tag = TAG_USER

        txtSubtotal.addTextChangedListener(generalTextWatcher)
        txtTotal.addTextChangedListener(generalTextWatcher)

        txtIva.setOnClickListener{hideKeyboard()}
        txtIsrR.setOnClickListener{hideKeyboard()}
        txtIvaR.setOnClickListener{hideKeyboard()}
        txtCedular.setOnClickListener{hideKeyboard()}

        icInfoRetenciones.setOnClickListener{ showDialogInfo() }
    }

    fun calc( option:Int ){
        IN_OPTION = option
        percentIva = UtilsGraphic().getIvaPercentSpinner(spinIva)
        percentCedular = getTaxPercentCedular()
        when (IN_OPTION){
            IN_SUBTOTAL ->calcInputSubtotal()
            IN_TOTAL -> cacInputTotal()
        }
    }

    private fun cacInputTotal() {
        //IVA retenido a 2/3
        //val percentIvaRetenido = ( percentIva / 3 ) * 2
        val percentIvaRetenido = percentIva  * 0.6667

        if(txtTotal.text.toString().isNotEmpty() ){
            val text = txtTotal.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                total = temp
                val map = Operations().calSubtotalRetencionesTotal(total, percentIva, percentIvaRetenido, percentIsrRetenido, percentCedular )
                iva = map?.get("iva")!!
                ivaR = map["ivaR"]!!
                isrR = map["isrR"]!!
                cedular = map["cedular"]!!
                subtotal = map["subtotal"]!!
                setValuesEditText()
            }else cleaner()
        }else cleaner()
    }

    private fun calcInputSubtotal( ){
        //IVA retenido a 2/3
        val percentIvaRetenido = ( percentIva / 3 ) * 2

        if(txtSubtotal.text.toString().isNotEmpty() ){
            val text = txtSubtotal.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                subtotal = temp
                val map = Operations().calcTotalRetencionesSubtotal(subtotal, percentIva, percentIvaRetenido, percentIsrRetenido, percentCedular )
                iva = map["iva"]!!
                ivaR = map["ivaR"]!!
                isrR = map["isrR"]!!
                cedular = map["cedular"]!!
                total = map["total"]!!
                setValuesEditText()
            }else cleaner()
        }else cleaner()

    }

    private fun getTaxPercentCedular(): Double{
        var percent: Double

        if( configLocales == 0 ) percent = 0.0
        else{
            percent = UtilsGraphic().getPercentCedularSpinner(spinCedular)

            if( percent == -1.0){
                percent = UtilsGraphic().getPercentCedularEditText( txtPercentCedular, requireContext() )
            }
        }
        return percent
    }

    private fun setValuesEditText(){

        if(IN_OPTION != IN_SUBTOTAL){
            val subtotalStrig = UtilsGraphic().round2Dec(subtotal)
            txtSubtotal.tag = TAG_SYSTEM
            txtSubtotal.setText( subtotalStrig )
            txtSubtotal.tag = TAG_USER
        }
        if(IN_OPTION != IN_IVA){
            val ivaStrig    = UtilsGraphic().round2Dec(iva)
            txtIva.tag = TAG_SYSTEM
            txtIva.setText( ivaStrig )
            txtIva.tag = TAG_USER
        }
        if(IN_OPTION != IN_ISR_R){
            val isrRStrig   = UtilsGraphic().round2Dec(isrR)
            txtIsrR.tag = TAG_SYSTEM
            txtIsrR.setText( isrRStrig )
            txtIsrR.tag = TAG_USER
        }
        if(IN_OPTION != IN_IVA_R){
            val ivaRStrig   = UtilsGraphic().round2Dec(ivaR)
            txtIvaR.tag = TAG_SYSTEM
            txtIvaR.setText( ivaRStrig )
            txtIvaR.tag = TAG_USER
        }
        if(IN_OPTION != IN_TOTAL ) {
            val totalString = UtilsGraphic().round2Dec(total)
            txtTotal.tag = TAG_SYSTEM
            txtTotal.setText( totalString )
            txtTotal.tag = TAG_USER
        }
        txtCedular.setText( UtilsGraphic().round2Dec(cedular) )
    }
    
    private fun cleaner(){
        if(IN_OPTION != IN_SUBTOTAL){
            subtotal = 0.0
            txtSubtotal.tag = TAG_SYSTEM
            txtSubtotal.setText( "" )
            txtSubtotal.tag = TAG_USER
        }
        if(IN_OPTION != IN_IVA){
            iva = 0.0
            txtIva.tag = TAG_SYSTEM
            txtIva.setText( "" )
            txtIva.tag = TAG_USER
        }
        if(IN_OPTION != IN_ISR_R){
            isrR = 0.0
            txtIsrR.tag = TAG_SYSTEM
            txtIsrR.setText( "" )
            txtIsrR.tag = TAG_USER
        }
        if(IN_OPTION != IN_IVA_R){
            ivaR = 0.0
            txtIvaR.tag = TAG_SYSTEM
            txtIvaR.setText( "" )
            txtIvaR.tag = TAG_USER
        }
        if(IN_OPTION != IN_TOTAL ) {
            total = 0.0
            txtTotal.tag = TAG_SYSTEM
            txtTotal.setText( "" )
            txtTotal.tag = TAG_USER
        }
        txtCedular.setText( "" )
    }

    private fun showDialogInfo(){
        val tit = resources.getString(R.string.titulo_dialog)
        val mensaje = resources.getString(R.string.retencion_info)

        context?.let {
            DialogFragment().showDialogNeutral(it, tit, mensaje)
        }
    }

    private fun hideIva(){
        fieldIva.visibility = View.GONE
        fieldIvaR.visibility = View.GONE
        iva = 0.0
        ivaR = 0.0
    }

    private fun showIva(){
        fieldIva.visibility = View.VISIBLE
        fieldIvaR.visibility = View.VISIBLE
    }

    private fun setItemIva(){
        UtilsGraphic().setItemSpin(requireContext(), R.array.item_iva_r, spinIva)
    }

    private fun setItemCedular(){
        UtilsGraphic().setItemSpin(requireContext(), R.array.item_cedular, spinCedular)
    }

    private val generalTextWatcher: TextWatcher = object : TextWatcher {
        //Detectar el cambio de contenido en los EditText
        override fun onTextChanged( s: CharSequence, start: Int, before: Int,count: Int) {
        }
        override fun beforeTextChanged( s: CharSequence, start: Int, count: Int,after: Int ) {
        }
        override fun afterTextChanged(s: Editable) {
            if (txtSubtotal.text.hashCode() == s.hashCode() && txtSubtotal.tag == TAG_USER) {
                calc(IN_SUBTOTAL)
            } else if (txtTotal.text.hashCode() == s.hashCode() && txtTotal.tag == TAG_USER) {
                calc(IN_TOTAL)
            }/*else if (txtIva.text.hashCode() == s.hashCode() && txtIva.tag == TAG_USER) {
                calc(IN_IVA)
            }*/

        }
    }

    private fun shareInfo(){
        //compartir el contenido de texto
        val valIva = percentIva * 100
        val valIvaInt = valIva.toInt()
        val subtotalRound = UtilsGraphic().round2Dec(subtotal)
        val ivaRound = UtilsGraphic().round2Dec(iva)
        val isrRRound = UtilsGraphic().round2Dec(isrR)
        val ivaRRound = UtilsGraphic().round2Dec(ivaR)
        val totalRound = UtilsGraphic().round2Dec(total)

        val cedularString = UtilsGraphic().getStringShareCedular(configLocales, cedular, spinCedular, txtPercentCedular)

        val text = "Subtotal: $ $subtotalRound \n\n IVA $valIvaInt%: $ $ivaRound \n ISR retn:   $ $isrRRound \n IVA retn:   $ $ivaRRound $cedularString\n\n TOTAL:  $ $totalRound"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun validarIntersticial(){
        if( nResultados == limiteAds-1) loadInterstitial()
        else if ( nResultados == limiteAds ) {
            showInterstitial()
            nResultados = 0
        }
    }
    private fun loadInterstitial() {
        Log.d("ADS***", "LOAD***")
    }
    private fun showInterstitial() {
        Log.d("ADS***", "SHOW***")
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
            RetencionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}