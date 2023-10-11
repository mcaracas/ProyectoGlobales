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
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.mozama.impuestos.R
import com.mozama.impuestos.utils.DialogFragment
import com.mozama.impuestos.utils.Operations
import com.mozama.impuestos.utils.UtilsGraphic

class UmaFragment : Fragment() {
    private var nResultados = 0
    private val limiteAds = 4

    private lateinit var txtUma: EditText
    private lateinit var txtPesos: EditText
    private lateinit var icInfo: ImageView

    private lateinit var spinSalario : Spinner
    private lateinit var txtSalario: EditText
    private lateinit var txtPesosSalario: EditText
    private lateinit var icInfoSalario: ImageView

    private val umaEnCurso = 103.74
    private val umaAnterior = 96.22
    private var valorUma = umaAnterior
    private val valorSMG_ZLFN = 312.41
    private val valorSMG = 207.44

    private var IN_OPTION = 0
    private val IN_UMA = 1
    private val IN_PESOS = 2
    private val IN_SALARIO = 3
    private val IN_PESOS_SALARIO = 4
    private val TAG_SYSTEM = "system"
    private val TAG_USER = "user"

    private var uma = 0.0
    private var pesos = 0.0
    private var nSalario = 0.0
    private var pesosSalario = 0.0
    private var valSalario = 0.0

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_uma, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtUma = view.findViewById(R.id.txtUma)
        txtPesos = view.findViewById(R.id.txtPesos)
        icInfo = view.findViewById(R.id.icInfo)

        spinSalario = view.findViewById(R.id.spinSalario)
        txtSalario = view.findViewById(R.id.txtSalario)
        txtPesosSalario = view.findViewById(R.id.txtPesosSmg)
        icInfoSalario = view.findViewById(R.id.icInfoSalario)
        setItemSalario()

        setChangeElements()
        setup()
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Detectar la opción del menú seleccionado
        return when (item.itemId) {
            R.id.menu_delete -> {
                IN_OPTION = 0
                hideKeyboard()
                cleaner()
                cleanerSalario()
                true
            }
            R.id.menu_share -> {
                shareInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setup(){
        validaValorUma()
        txtUma.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                hideKeyboard()
                nResultados++
                validarIntersticial()
                true
            }else false
        }

        txtPesos.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                hideKeyboard()
                nResultados++
                validarIntersticial()
                true
            }else false
        }

        txtSalario.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                hideKeyboard()
                nResultados++
                validarIntersticial()
                true
            }else false
        }

        txtPesosSalario.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                hideKeyboard()
                nResultados++
                validarIntersticial()
                true
            }else false
        }
    }

    private fun validaValorUma(){
        valorUma = if(Operations().fechaMayorA("01", "02", "2023")){
            umaEnCurso
        }else umaAnterior
    }

    private fun shareInfo() {
        //compartir el contenido de texto
        var text = ""

        if(txtUma.text.isNotEmpty()){
            val valUma = UtilsGraphic().round2Dec(uma)
            val valPesos = UtilsGraphic().round2Dec(pesos)
            text += "UMA: $valUma \nPESOS:  $ $valPesos \n\nValor UMA 2021: $$valorUma"
        }

        if (txtSalario.text.isNotEmpty()){
            val valNSalario = UtilsGraphic().round2Dec(nSalario)
            val valPesosSalario = UtilsGraphic().round2Dec(pesosSalario)
            text += "\n\nSalarios Mínimos: $valNSalario \nPESOS:  $ $valPesosSalario \n\nValor SMG: $$valSalario"
        }

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun setChangeElements(){
        txtUma.tag = TAG_USER
        txtPesos.tag = TAG_USER
        txtSalario.tag = TAG_USER
        txtPesosSalario.tag = TAG_USER

        txtUma.addTextChangedListener(generalTextWatcher)
        txtPesos.addTextChangedListener(generalTextWatcher)

        txtSalario.addTextChangedListener(generalTextWatcher)
        txtPesosSalario.addTextChangedListener(generalTextWatcher)

        icInfo.setOnClickListener{ showDialogInfo("uma") }
        icInfoSalario.setOnClickListener{ showDialogInfo("salario") }

        spinSalario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                hideKeyboard()
                calc(IN_SALARIO)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    fun calc( option:Int){
        IN_OPTION = option
        when (IN_OPTION){
            IN_UMA ->calcInputUma()
            IN_PESOS -> calcInputPesos()
            IN_SALARIO -> calcInputSalario()
            IN_PESOS_SALARIO -> calcInputPesosSalario()
        }
    }

    private fun setItemSalario(){
        UtilsGraphic().setItemSpin(requireContext(), R.array.item_salarios, spinSalario)
    }

    private fun calcInputSalario(){
        if (txtSalario.text.isNotEmpty()){
            val text = txtSalario.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                nSalario = temp
                valSalario = UtilsGraphic().getValSalarioMinimoSpinner(spinSalario, valorSMG, valorSMG_ZLFN)
                pesosSalario = Operations().calPesosSalario( nSalario, valSalario )
                Log.d("CALC***",pesosSalario.toString())
                setValuesEditTextSalarios()
            }
            else cleanerSalario()
        }else cleanerSalario()
    }

    private fun calcInputPesosSalario(){
        if (txtPesosSalario.text.isNotEmpty()){
            val text = txtPesosSalario.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                pesosSalario = temp
                valSalario = UtilsGraphic().getValSalarioMinimoSpinner(spinSalario, valorSMG, valorSMG_ZLFN)
                nSalario = Operations().calSalarioPesos( pesosSalario, valSalario )
                setValuesEditTextSalarios()
            }
            else cleanerSalario()
        }else cleanerSalario()
    }

    private fun calcInputUma(){
        if (txtUma.text.isNotEmpty()){
            val text = txtUma.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                uma = temp
                pesos = Operations().calPesosUma( uma, valorUma )
                setValuesEditText()
            }
            else cleaner()
        }else cleaner()
    }

    private fun calcInputPesos(){
        if (txtPesos.text.isNotEmpty()){
            val text = txtPesos.text.toString()
            val textNotComma = UtilsGraphic().deleteComma(text)
            val temp = textNotComma.toDoubleOrNull()
            if( temp != null){
                pesos = temp
                uma = Operations().calUmaPesos( pesos, valorUma )
                setValuesEditText()
            }
            else cleaner()
        }else cleaner()
    }

    private fun showDialogInfo(tipo:String){
        var tit: String
        var mensaje: String

        if (tipo == "uma") {
            tit =  resources.getString(R.string.unidad_ma)
            val umaString = valorUma.toString()
            mensaje = resources.getString(R.string.uma_info,umaString)
        }else  {
            tit =  resources.getString(R.string.smg)
            val smgString = valorSMG.toString()
            val smgZFString = valorSMG_ZLFN.toString()
            mensaje = resources.getString(R.string.salario_info, smgZFString, smgString)
        }

        context?.let {
            DialogFragment().showDialogNeutral(it, tit, mensaje)
        }
    }

    private fun setValuesEditText(){
        if(IN_OPTION != IN_UMA){
            val subtotalStrig = UtilsGraphic().round2Dec(uma)
            txtUma.tag = TAG_SYSTEM
            txtUma.setText( subtotalStrig )
            txtUma.tag = TAG_USER
        }
        if(IN_OPTION != IN_PESOS){
            val pesos = UtilsGraphic().round2Dec(pesos)
            txtPesos.tag = TAG_SYSTEM
            txtPesos.setText( pesos )
            txtPesos.tag = TAG_USER
        }
    }

    private fun setValuesEditTextSalarios(){
        if( IN_OPTION != IN_PESOS_SALARIO ){
            val pesos = UtilsGraphic().round2Dec(pesosSalario)
            txtPesosSalario.tag = TAG_SYSTEM
            txtPesosSalario.setText( pesos )
            txtPesosSalario.tag = TAG_USER
        }
        if( IN_OPTION != IN_SALARIO ){
            val salario = UtilsGraphic().round2Dec(nSalario)
            txtSalario.tag = TAG_SYSTEM
            txtSalario.setText( salario )
            txtSalario.tag = TAG_USER
        }
    }

    private fun cleaner(){
        if(IN_OPTION != IN_UMA){
            pesos = 0.0
            txtUma.tag = TAG_SYSTEM //La app es quien modifica el valor
            txtUma.setText("")
            txtUma.tag = TAG_USER // Regresa al supuesto de que el usuario modificará el siguiente valor
        }
        if(IN_OPTION != IN_PESOS){
            uma = 0.0
            txtPesos.tag = TAG_SYSTEM
            txtPesos.setText("")
            txtPesos.tag = TAG_USER
        }
    }

    private fun cleanerSalario(){
        if( IN_OPTION != IN_SALARIO ){
            pesosSalario = 0.0
            txtSalario.tag = TAG_SYSTEM //La app es quien modifica el valor
            txtSalario.setText("")
            txtSalario.tag = TAG_USER // Regresa al supuesto de que el usuario modificará el siguiente valor
        }
        if( IN_OPTION != IN_PESOS_SALARIO ){
            uma = 0.0
            txtPesosSalario.tag = TAG_SYSTEM
            txtPesosSalario.setText("")
            txtPesosSalario.tag = TAG_USER
        }
    }

    private val generalTextWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged( s: CharSequence, start: Int, before: Int,count: Int) {
        }
        override fun beforeTextChanged( s: CharSequence, start: Int, count: Int,after: Int ) {
        }
        override fun afterTextChanged(s: Editable) {
            if (txtUma.text.hashCode() == s.hashCode() && txtUma.tag == TAG_USER) {
                calc(IN_UMA)
            } else if (txtPesos.text.hashCode() == s.hashCode() && txtPesos.tag == TAG_USER) {
                calc(IN_PESOS)
            }else if (txtSalario.text.hashCode() == s.hashCode() && txtSalario.tag == TAG_USER) {
                calc(IN_SALARIO)
            }else if (txtPesosSalario.text.hashCode() == s.hashCode() && txtPesosSalario.tag == TAG_USER){
                calc(IN_PESOS_SALARIO)
            }
        }
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
            UmaFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}