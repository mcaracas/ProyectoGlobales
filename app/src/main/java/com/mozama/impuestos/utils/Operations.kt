/*
 * This is the source code of Calculadora de Impuestos v. 1.x.x.
 * It is licensed under GNU GPL v. 3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Edgar Santiago, 2021.
 */
package com.mozama.impuestos.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date


/**
 * Clase con la lógica y operaciones matemáticas para obtener los montos
 */

class Operations {

    fun fechaMayorA(dia:String, mes:String, anio:String): Boolean {

        val dateString = "$dia $mes $anio"

        val formatter = DateTimeFormatter.ofPattern("dd MM yyyy")
        val dateValidation = LocalDate.parse(dateString, formatter)

        val dateNow = LocalDate.now()
        return dateNow >= dateValidation
    }

    fun calcValPercentTotal(total: Double, percent: Double): Double {
        return total * percent
    }

    fun calcValSubtotalIva(iva: Double, percent: Double): Double {
        val constant = 100 / (percent * 100)
        return iva * constant
    }

    private fun calcAllIva(subtotal:Double, percentIva:Double, percentCedular:Double): Map<String,Double>{
        val iva = calcValPercentTotal( subtotal, percentIva )
        val cedular = calcValPercentTotal( subtotal, percentCedular )
        val total = subtotal + iva - cedular

        return mapOf(
            "iva" to iva,
            "cedular" to cedular,
            "total" to total,
            "subtotal" to subtotal
        )
    }

    fun calSubtotalIvaTotal(total:Double, percentIva:Double, percentCedular:Double): Map<String,Double>?{
        var subtotal = total + 1
        var map: Map<String, Double>? = null
        var totalCiclo = 0.0
        var diferencia: Double
        var diferenciaAnterior = 0.0
        var flag = 0
        var dif: Double

        while ( total != totalCiclo  && flag == 0){

            map = calcAllIva( subtotal, percentIva, percentCedular )
            totalCiclo = map["total"]!!

            diferencia = total - totalCiclo
            subtotal += diferencia
            dif = diferenciaAnterior + diferencia

            if ( diferencia > 0 && diferencia <= 0.001 ) flag = 1
            else if ( diferencia < 0 && diferencia >= -0.001 ) flag = 1
            if ( dif == 0.0) flag = 1

            diferenciaAnterior = diferencia
        }
        return map
    }

    /**
     * RSP
     * RESICO
     */

    fun calcTotalRetencionesSubtotal(subtotal:Double, percentIva:Double, percentIvaRetenido:Double, percentIsrRetenido:Double, percentCedular: Double): Map<String,Double>{
        val iva = calcValPercentTotal( subtotal, percentIva )
        val ivaR = calcValPercentTotal( subtotal, percentIvaRetenido )
        val isrR = calcValPercentTotal( subtotal, percentIsrRetenido )
        val cedular = calcValPercentTotal( subtotal, percentCedular )
        val total = subtotal + iva - ivaR - isrR - cedular

        return mapOf(
            "iva" to iva,
            "ivaR" to ivaR,
            "isrR" to isrR,
            "cedular" to cedular,
            "total" to total,
            "subtotal" to subtotal
        )
    }

    fun calSubtotalRetencionesTotal(total:Double, percentIva:Double, percentIvaRetenido:Double, percentIsrRetenido:Double, percentCedular:Double): Map<String,Double>?{
        var subtotal = total + 1
        var map: Map<String, Double>? = null
        var totalCiclo = 0.0
        var diferencia: Double
        var diferenciaAnterior = 0.0
        var flag = 0
        var dif: Double

        while ( total != totalCiclo  && flag == 0){

            map = calcTotalRetencionesSubtotal( subtotal, percentIva, percentIvaRetenido, percentIsrRetenido, percentCedular )
            totalCiclo = map["total"]!!

            diferencia = total - totalCiclo
            subtotal += diferencia
            dif = diferenciaAnterior + diferencia

            if ( diferencia > 0 && diferencia <= 0.001 ) flag = 1
            else if ( diferencia < 0 && diferencia >= -0.001 ) flag = 1
            if ( dif == 0.0) flag = 1

            diferenciaAnterior = diferencia
        }
        return map
    }


    fun calPesosUma(nUma:Double, valorUma:Double):Double{
        return nUma * valorUma
    }

    fun calUmaPesos(pesos:Double, valorUma:Double):Double{
        return pesos / valorUma
    }

    fun calPesosSalario(nSalarios:Double, valorSalario:Double):Double{
        return nSalarios * valorSalario
    }

    fun calSalarioPesos(pesos:Double, valorSalario:Double):Double{
        return pesos / valorSalario
    }
}