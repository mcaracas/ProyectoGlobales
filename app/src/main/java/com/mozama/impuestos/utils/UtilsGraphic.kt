/*
 * This is the source code of Calculadora de Impuestos v. 1.x.x.
 * It is licensed under GNU GPL v. 3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Edgar Santiago, 2021.
 */
package com.mozama.impuestos.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mozama.impuestos.R
import java.text.DecimalFormat
import java.util.*

/**
 * Clase con funciones para modificar elementos visuales
 */
class UtilsGraphic {

    fun setItemSpin(context: Context, array:Int, spin: Spinner){
        ArrayAdapter.createFromResource(
            context,
            array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spin.adapter = adapter
        }
    }

    fun getIvaPercentSpinner(spin:Spinner): Double{
        val position = spin.selectedItemPosition

        var porcentaje = 0.00
        if ( position == 0 ) porcentaje = 0.16
        else if ( position == 1 ) porcentaje = 0.08

        return porcentaje
    }

    fun getPercentCedularSpinner(spin: Spinner): Double {
        return when (spin.selectedItemPosition) {
            0 -> 0.0
            1 -> 0.02
            2 -> 0.03
            3 -> 0.04
            4 -> 0.05
            else -> -1.0
        }
    }

    fun getValSalarioMinimoSpinner(spin: Spinner, SMG: Double, ZLFN:Double): Double {
        return when (spin.selectedItemPosition) {
            0 -> SMG
            1 -> ZLFN
            else -> 0.0
        }
    }

    fun getStringShareCedular(configLocales:Int, cedular:Double, spinCedular:Spinner, txtPercentCedular:EditText):String{
        var cedularString = ""
        if(configLocales == 1){
            val valCedular = round2Dec(cedular)

            val optionCedular = spinCedular.selectedItemPosition
            if(optionCedular != 0){
                cedularString = if(optionCedular == 5){
                    val valPercent = txtPercentCedular.text.toString().toFloatOrNull()
                    "\n Imp. local $valPercent% $ $valCedular"
                }
                else{
                    val valPercent = getPercentCedularSpinner(spinCedular)
                    val percent = round0Dec(valPercent * 100)
                    "\n Cedular $percent%:  $ $valCedular"
                }
            }
        }
        return cedularString
    }

    // -1.0 is value not valid
    fun getPercentCedularEditText(editText: EditText, context: Context): Double{
        var percent = -1.0
        if(editText.text.isNotEmpty()){
            val valEditText: Double? = editText.text.toString().toDoubleOrNull()
            if(valEditText != null) percent = valEditText/100
        }else{
            editText.requestFocus()
            showToast(context.resources.getString(R.string.verifica_cedular), context)
        }

        return percent
    }


    fun hideCedular(fieldPercentCedular:TextInputLayout, fieldCedular:TextInputLayout){  //is active but dont use
        fieldPercentCedular.visibility = View.GONE
        fieldCedular.visibility = View.GONE
    }

    fun showCedular(fieldPercentCedular:TextInputLayout, fieldCedular:TextInputLayout){
        fieldPercentCedular.visibility = View.GONE
        fieldCedular.visibility = View.VISIBLE
    }

    fun showOtroCedular(fieldPercentCedular:TextInputLayout, fieldCedular:TextInputLayout){
        fieldPercentCedular.visibility = View.VISIBLE
        fieldCedular.visibility = View.VISIBLE
    }

    fun deleteComma(value:String):String{
        return value.replace(",", "")
    }

    fun round2Dec(valor: Double): String {
        val formato = DecimalFormat("###,###,###,###,###,###,##0.00")
        return formato.format(valor)
    }

    private fun round0Dec(valor: Double): String {
        val formato = DecimalFormat("###,###,###,###,###,###,##0.##")
        return formato.format(valor)
    }

    fun getFechaActual():String {
        var c = Calendar.getInstance()
        val year  = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)+1
        val day   = c.get(Calendar.DAY_OF_MONTH)

        val fechaNum = "$year-$month-$day"
        return fechaNum
    }

    fun fechaNumeroAString(fechaNumero: String):String{
        var fechaString: String
        val arrayFecha = fechaNumero.split("-")
        val anio = arrayFecha[0]
        val mesInt = arrayFecha[1].toInt()
        val mes = mesInt.toString()
        val dia = arrayFecha[2]

        val mesString = getMesString(mes)
        fechaString = "$dia $mesString $anio"
        return fechaString
    }

    private fun getMesString(mesNumero: String):String{
        var mesString = ""

        if (mesNumero == "1"){
            mesString = "enero"
        }
        else if(mesNumero == "2"){
            mesString = "febrero"
        }
        else if(mesNumero == "3"){
            mesString = "marzo"
        }
        else if(mesNumero == "4"){
            mesString = "abril"
        }
        else if(mesNumero == "5"){
            mesString = "mayo"
        }
        else if(mesNumero == "6"){
            mesString = "junio"
        }
        else if(mesNumero == "7"){
            mesString = "julio"
        }
        else if(mesNumero == "8"){
            mesString = "agosto"
        }
        else if(mesNumero == "9"){
            mesString = "septiembre"
        }
        else if(mesNumero == "10"){
            mesString = "octubre"
        }
        else if(mesNumero == "11"){
            mesString = "noviembre"
        }
        else if(mesNumero == "12"){
            mesString = "diciembre"
        }

        return mesString
    }

    /**
     * @param 4 julio 2020
     * @return 2020-07-04
     */
    fun fechaStringAInt(fechaString: String):String{
        val arrayFecha = fechaString.split(" ")
        val dia = arrayFecha[0]
        val mesString = arrayFecha[1]
        val anio = arrayFecha[2]

        val mesInt = getMesInt(mesString)
        val fechaInt = "$anio-$mesInt-$dia"
        return fechaInt
    }
    private fun getMesInt(mesString: String):String{
        var mesInt = ""

        if (mesString == "enero"){
            mesInt = "1"
        }
        else if(mesString == "febrero"){
            mesInt = "2"
        }
        else if(mesString == "marzo"){
            mesInt = "3"
        }
        else if(mesString == "abril"){
            mesInt = "4"
        }
        else if(mesString == "mayo"){
            mesInt = "5"
        }
        else if(mesString == "junio"){
            mesInt = "6"
        }
        else if(mesString == "julio"){
            mesInt = "7"
        }
        else if(mesString == "agosto"){
            mesInt = "8"
        }
        else if(mesString == "septiembre"){
            mesInt = "9"
        }
        else if(mesString == "octubre"){
            mesInt = "10"
        }
        else if(mesString == "noviembre"){
            mesInt = "11"
        }
        else if(mesString == "diciembre"){
            mesInt = "12"
        }
        return mesInt
    }

    fun showToast(msg:String, context:Context){
        Toast.makeText(context,
            msg,
            Toast.LENGTH_LONG).show()
    }
}