package es.studium.diagnoskin_app

import androidx.test.ext.junit.runners.AndroidJUnit4
import es.studium.modelos_y_utiles.ValidacionPacientes
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ValidacionPacientesTest {
    private lateinit var validador: ValidacionPacientes

    @Before
    fun setUp() {
        validador = ValidacionPacientes()
    }

    @Test
    fun testEsNombreValido() {
        assertTrue(validador.esNombreValido("Juan"))
        assertFalse(validador.esNombreValido(""))
    }

    @Test
    fun testEsApellidosValido() {
        assertTrue(validador.esApellidosValido("Pérez García"))
        assertFalse(validador.esApellidosValido(""))
    }

    @Test
    fun testEsSexoValido() {
        val itemCero = "Seleccionar sexo"
        assertTrue(validador.esSexoValido("Masculino", itemCero))
        assertFalse(validador.esSexoValido("Seleccionar sexo", itemCero))
    }

    @Test
    fun testEsNuhsaValido() {
        assertTrue(validador.esNuhsaValido("123456789"))
        assertFalse(validador.esNuhsaValido(""))
    }

    @Test
    fun testEsTelefonoValido() {
        assertTrue(validador.esTelefonoValido("612345678"))
        assertFalse(validador.esTelefonoValido(""))
    }

    @Test
    fun testEsDireccionValida() {
        assertTrue(validador.esDireccionValida("Calle Correcta n3"))
        assertFalse(validador.esDireccionValida(""))
    }

    @Test
    fun testEsLocalidadValida() {
        assertTrue(validador.esLocalidadValida("Jerez de la frontera"))
        assertFalse(validador.esLocalidadValida(""))
    }

    @Test
    fun testEsProvinciaValida() {
        assertTrue(validador.esProvinciaValida("Cádiz"))
        assertFalse(validador.esProvinciaValida(""))
    }

    @Test
    fun testEsCodigoPostalValido() {
        assertTrue(validador.esCodigoPostalValido("11408"))
        assertFalse(validador.esCodigoPostalValido(""))
    }

    @Test
    fun testEsCodigoPostalLongitudCorrecta() {
        assertTrue(validador.esCodigoPostalLongitudCorrecta("11408"))
        assertFalse(validador.esCodigoPostalLongitudCorrecta("1140"))     // 4 dígitos
        assertFalse(validador.esCodigoPostalLongitudCorrecta("114081"))   // 6 dígitos
        assertFalse(validador.esCodigoPostalLongitudCorrecta(""))         // vacío
    }
}