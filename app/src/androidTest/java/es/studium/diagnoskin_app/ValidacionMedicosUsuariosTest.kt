package es.studium.diagnoskin_app

import androidx.test.ext.junit.runners.AndroidJUnit4
import es.studium.modelos_y_utiles.ValidacionMedicosUsuarios
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ValidacionMedicosUsuariosTest {

    private lateinit var validador: ValidacionMedicosUsuarios

    @Before
    fun setUp() {
        validador = ValidacionMedicosUsuarios()
    }

    @Test
    fun testEsNombreUsuarioValido() {
        assertTrue(validador.esNombreUsuarioValido("usuario"))
        assertFalse(validador.esNombreUsuarioValido(""))
    }

    @Test
    fun testEsNumColegiadoValido() {
        assertTrue(validador.esNumColegiadoValido("12345"))
        assertFalse(validador.esNumColegiadoValido(""))
    }

    @Test
    fun testEsClave1Valida() {
        assertTrue(validador.esClave1Valida("clave"))
        assertFalse(validador.esClave1Valida(""))
    }

    @Test
    fun testEsClave1LongitudCorrecta() {
        assertTrue(validador.esClave1LongitudCorrecta("123456"))
        assertFalse(validador.esClave1LongitudCorrecta("123"))
    }

    @Test
    fun testEsClave2Valida() {
        assertTrue(validador.esClave2Valida("clave"))
        assertFalse(validador.esClave2Valida(""))
    }

    @Test
    fun testClavesCoinciden() {
        assertTrue(validador.clavesCoinciden("clave", "clave"))
        assertFalse(validador.clavesCoinciden("clave1", "clave2"))
    }

    @Test
    fun testEsNombreMedicoValido() {
        assertTrue(validador.esNombreMedicoValido("Juan"))
        assertFalse(validador.esNombreMedicoValido(""))
    }

    @Test
    fun testEsApellidosMedicoValidos() {
        assertTrue(validador.esApellidosMedicoValidos("Pérez"))
        assertFalse(validador.esApellidosMedicoValidos(""))
    }

    @Test
    fun testEsEspecialidadSeleccionada() {
        assertTrue(validador.esEspecialidadSeleccionada(1))
        assertFalse(validador.esEspecialidadSeleccionada(0))
    }

    @Test
    fun testEsCentroMedicoSeleccionado() {
        assertTrue(validador.esCentroMedicoSeleccionado(2))
        assertFalse(validador.esCentroMedicoSeleccionado(0))
    }

    @Test
    fun testEsTelefonoValido() {
        assertTrue(validador.esTelefonoValido("123456789"))
        assertFalse(validador.esTelefonoValido(""))
    }

    @Test
    fun testEsEmailValido() {
        assertTrue(validador.esEmailValido("correo@example.com"))
        assertFalse(validador.esEmailValido(""))
    }
}