/*
 * This is the source code of Calculadora de Impuestos v. 1.x.x.
 * It is licensed under GNU GPL v. 3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Edgar Santiago, 2021.
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
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.mozama.impuestos.R
import com.mozama.impuestos.utils.DialogFragment
import com.mozama.impuestos.utils.Operations
import com.mozama.impuestos.utils.UtilsGraphic

/*
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
 */

/**
 * Fragment principal para procesar los elementos del segundo elemento del TabLayout
 * IVA
 */

class IvaFragment : Fragment() {
    private var nResultados = 0
    private val limiteAds = 4

    private lateinit var txtSubtotal: EditText
    private lateinit var txtIva: EditText
    private lateinit var txtTotal: EditText
    private lateinit var spinIva : Spinner
    private lateinit var lyCedular: LinearLayout
    private lateinit var spinCedular : Spinner
    private lateinit var fieldCedular: TextInputLayout
    private lateinit var fieldPercentCedular: TextInputLayout
    private lateinit var txtPercentCedular: EditText
    private lateinit var txtCedular: EditText
    private lateinit var icInfoIva: ImageView

    private var configLocales = 0

    private var IN_OPTION = 0
    private val IN_SUBTOTAL = 1
    private val IN_IVA = 2
    private val IN_TOTAL = 3

    private var subtotal = 0.0
    private var percentIva = 0.0
    private var percentCedular = 0.0
    private var cedular = 0.0
    private var iva = 0.0
    private var total = 0.0

    //Para identificar quién modifica el valor de los EditText
    //evitar ciclo infinito en addTextChangedListener
    private val TAG_SYSTEM = "system"
    private val TAG_USER = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_iva, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtSubtotal = view.findViewById(R.id.txtSubtotalI)
        txtTotal    = view.findViewById(R.id.txtTotalI)
        txtIva  = view.findViewById(R.id.txtIvaI)
        spinIva = view.findViewById(R.id.spinIvaI)
        lyCedular    = view.findViewById(R.id.lyCedularI)
        spinCedular  = view.findViewById(R.id.spinCedularI)
        fieldCedular = view.findViewById(R.id.fieldCedularI)
        fieldPercentCedular = view.findViewById(R.id.fieldPercentCedularI)
        txtPercentCedular = view.findViewById(R.id.txtPercentCedularI)
        txtCedular = view.findViewById(R.id.txtCedular)
        icInfoIva = view.findViewById(R.id.icInfoIva)

        setup()
        setItemIva()
        setItemCedular()
        setChangeElements()
        hideKeyboard()

        verificViewCedular()

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

        txtIva.setOnEditorActionListener { _, actionId, _ ->
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Detectar la opción del menú seleccionado
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

    private fun setChangeElements() {
        spinIva.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                hideKeyboard()
                calc(IN_OPTION)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
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
        txtTotal.tag = TAG_USER
        txtIva.tag = TAG_USER

        txtSubtotal.addTextChangedListener(generalTextWatcher)
        txtTotal.addTextChangedListener(generalTextWatcher)
        txtIva.addTextChangedListener(generalTextWatcher)

        txtCedular.setOnClickListener{hideKeyboard()}

        icInfoIva.setOnClickListener{ showDialogInfo() }
    }


    fun calc( option:Int){
        IN_OPTION = option
        percentIva = UtilsGraphic().getIvaPercentSpinner(spinIva)
        percentCedular = getTaxPercentCedular()
        when (IN_OPTION){
            IN_SUBTOTAL ->calcInputSubtotal()
            IN_IVA -> calcInputIva()
            IN_TOTAL -> calcInputTotal()
            else -> calcInputSubtotal()
        }
    }

    private fun calcInputSubtotal(){
        if (txtSubtotal.text.isNotEmpty() ){
            if (percentCedular != -1.0){
                val text = txtSubtotal.text.toString()
                val textNotComma = UtilsGraphic().deleteComma(text)
                val temp = textNotComma.toDoubleOrNull()
                if( temp != null){
                    subtotal = temp
                    iva = Operations().calcValPercentTotal( subtotal, percentIva )
                    cedular = Operations().calcValPercentTotal( subtotal, percentCedular )
                    total = subtotal + iva - cedular
                    setValuesEditText()
                }
                else cleaner()
            }
        }else cleaner()
    }

    private fun calcInputIva(){
        if (txtIva.text.isNotEmpty()){
            val text = txtIva.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                iva = temp
                subtotal = Operations().calcValSubtotalIva( iva, percentIva )
                cedular = Operations().calcValPercentTotal( subtotal, percentCedular )
                total = subtotal + iva - cedular
                setValuesEditText()
            }
            else cleaner()
        }else cleaner()
    }

    private fun calcInputTotal() {
        if (txtTotal.text.isNotEmpty()){
            val text = txtTotal.text.toString()
            val subNotComma = UtilsGraphic().deleteComma(text)
            val temp = subNotComma.toDoubleOrNull()
            if( temp != null ){
                total = temp
                val map = Operations().calSubtotalIvaTotal(total, percentIva, percentCedular)
                subtotal = map?.get("subtotal")!!
                iva      = map["iva"]!!
                cedular  = map["cedular"]!!
                setValuesEditText()
            }
            else cleaner()
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
            txtSubtotal.tag = TAG_SYSTEM //La app es quien modifica el valor
            txtSubtotal.setText( "" )
            txtSubtotal.tag = TAG_USER // Regresa al supuesto de que el usuario modificará el siguiente valor
        }
        if(IN_OPTION != IN_IVA){
            iva = 0.0
            txtIva.tag = TAG_SYSTEM
            txtIva.setText( "")
            txtIva.tag = TAG_USER
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
        val mensaje = resources.getString(R.string.iva_info)

        context?.let {
            DialogFragment().showDialogNeutral(it, tit, mensaje)
        }
    }

    private fun setItemIva(){
        UtilsGraphic().setItemSpin(requireContext(), R.array.item_iva, spinIva)
    }

    private fun setItemCedular(){
        UtilsGraphic().setItemSpin(requireContext(), R.array.item_cedular, spinCedular)
    }

    private val generalTextWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged( s: CharSequence, start: Int, before: Int,count: Int) {
        }        override fun beforeTextChanged( s: CharSequence, start: Int, count: Int,after: Int ) {
        }
        override fun afterTextChanged(s: Editable) {
            if (txtSubtotal.text.hashCode() == s.hashCode() && txtSubtotal.tag == TAG_USER) {
                calc(IN_SUBTOTAL)
            } else if (txtTotal.text.hashCode() == s.hashCode() && txtTotal.tag == TAG_USER) {
                calc(IN_TOTAL)
            }else if (txtIva.text.hashCode() == s.hashCode() && txtIva.tag == TAG_USER) {
                calc(IN_IVA)
            }

        }
    }

    private fun shareInfo(){
        //compartir el contenido de texto
        val valIva = percentIva * 100
        val valIvaInt = valIva.toInt()
        val subtotalRound = UtilsGraphic().round2Dec(subtotal)
        val ivaRound = UtilsGraphic().round2Dec(iva)
        val totalRound = UtilsGraphic().round2Dec(total)

        val cedularString = UtilsGraphic().getStringShareCedular(configLocales, cedular, spinCedular, txtPercentCedular)

        val text = "Subtotal: $ $subtotalRound \n IVA $valIvaInt%:  $ $ivaRound $cedularString \n\n TOTAL:  $ $totalRound"
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
            IvaFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}