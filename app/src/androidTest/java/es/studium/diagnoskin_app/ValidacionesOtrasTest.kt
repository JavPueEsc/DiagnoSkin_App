package es.studium.diagnoskin_app

import androidx.test.ext.junit.runners.AndroidJUnit4
import es.studium.modelos_y_utiles.ValidacionesOtras
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class ValidacionesOtrasTest {
    private lateinit var validador: ValidacionesOtras
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    @Before
    fun setUp() {
        validador = ValidacionesOtras()
    }

    //  Login
    @Test
    fun testEsUsuarioValido() {
        assertTrue(validador.esUsuarioValido("usuario123"))
        assertFalse(validador.esUsuarioValido(""))
    }

    @Test
    fun testEsClaveValida() {
        assertTrue(validador.esClaveValida("claveSegura"))
        assertFalse(validador.esClaveValida(""))
    }

    //  Fechas no vacías
    @Test
    fun testEsFechaDesdeNoVacia() {
        assertTrue(validador.esFechaDesdeNoVacia("01/01/2024"))
        assertFalse(validador.esFechaDesdeNoVacia(""))
    }

    @Test
    fun testEsFechaHastaNoVacia() {
        assertTrue(validador.esFechaHastaNoVacia("31/12/2024"))
        assertFalse(validador.esFechaHastaNoVacia(""))
    }

    //  Rango de fechas válido
    @Test
    fun testEsRangoFechasValidoCorrecto() {
        val desde = dateFormat.parse("01/01/2024")
        val hasta = dateFormat.parse("31/12/2024")
        assertTrue(validador.esRangoFechasValido(desde, hasta))
    }

    @Test
    fun testEsRangoFechasValidoMismoDia() {
        val fecha = dateFormat.parse("15/03/2024")
        assertTrue(validador.esRangoFechasValido(fecha, fecha))
    }

    @Test
    fun testEsRangoFechasValidoIncorrecto() {
        val desde = dateFormat.parse("31/12/2024")
        val hasta = dateFormat.parse("01/01/2024")
        assertFalse(validador.esRangoFechasValido(desde, hasta))
    }

    @Test
    fun testEsRangoFechasValidoConNull() {
        val hasta = dateFormat.parse("31/12/2024")
        assertFalse(validador.esRangoFechasValido(null, hasta))
        assertFalse(validador.esRangoFechasValido(hasta, null))
        assertFalse(validador.esRangoFechasValido(null, null))
    }
}