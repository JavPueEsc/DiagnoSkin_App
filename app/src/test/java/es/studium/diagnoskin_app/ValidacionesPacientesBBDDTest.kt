package es.studium.diagnoskin_app

import es.studium.modelos_y_utiles.InterfazPaciente
import es.studium.modelos_y_utiles.ValidacionPacientesBBDD
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class ValidacionesPacientesBBDDTest {
    // Mock de la interfaz que simula la base de datos o lógica externa
    private lateinit var interfazMock: InterfazPaciente
    private lateinit var validador: ValidacionPacientesBBDD

    @Before
    fun setUp() {
        // Creamos el mock antes de cada test
        interfazMock = mock(InterfazPaciente::class.java)
        validador = ValidacionPacientesBBDD(interfazMock)
    }

    @Test
    fun esNuhsaUnico_devuelve_true_cuando_el_paciente_no_existe() {
        `when`(interfazMock.existePaciente("123456")).thenReturn(false)

        val resultado = validador.esNuhsaUnico("123456")

        assertTrue(resultado)
        verify(interfazMock).existePaciente("123456")
    }

    @Test
    fun esNuhsaUnico_devuelve_false_cuando_el_paciente_ya_existe() {
        `when`(interfazMock.existePaciente("123456")).thenReturn(true)

        val resultado = validador.esNuhsaUnico("123456")

        assertFalse(resultado)
        verify(interfazMock).existePaciente("123456")
    }

    @Test
    fun esNuhsaValidoParaModificacion_devuelve_true_cuando_el_NUHSA_no_ha_cambiado() {
        val resultado = validador.esNuhsaValidoParaModificacion("123456", "123456")

        assertTrue(resultado)
        // No debe llamarse a existePaciente si no ha cambiado
        verify(interfazMock, never()).existePaciente(anyString())
    }

    @Test
    fun esNuhsaValidoParaModificacion_devuelve_true_cuando_el_NUHSA_ha_cambiado_y_no_existe() {
        `when`(interfazMock.existePaciente("654321")).thenReturn(false)

        val resultado = validador.esNuhsaValidoParaModificacion("654321", "123456")

        assertTrue(resultado)
        verify(interfazMock).existePaciente("654321")
    }

    @Test
    fun esNuhsaValidoParaModificacion_devuelve_false_cuando_el_NUHSA_ha_cambiado_y_ya_existe() {
        `when`(interfazMock.existePaciente("654321")).thenReturn(true)

        val resultado = validador.esNuhsaValidoParaModificacion("654321", "123456")

        assertFalse(resultado)
        verify(interfazMock).existePaciente("654321")
    }
}