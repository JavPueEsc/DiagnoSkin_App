package es.studium.diagnoskin_app

import es.studium.modelos_y_utiles.InterfazMedico
import es.studium.modelos_y_utiles.InterfazUsuario
import es.studium.modelos_y_utiles.ValidacionMedicosUsuariosBBDD
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class ValidacioensMedicosUsuariosBBDDTest {
    private lateinit var interfazUsuarioMock: InterfazUsuario
    private lateinit var interfazMedicoMock: InterfazMedico
    private lateinit var validacion: ValidacionMedicosUsuariosBBDD

    @Before
    fun setUp() {
        interfazUsuarioMock = mock(InterfazUsuario::class.java)
        interfazMedicoMock = mock(InterfazMedico::class.java)
        validacion = ValidacionMedicosUsuariosBBDD(interfazUsuarioMock, interfazMedicoMock)
    }

    @Test
    fun existeUsuario_devuelve_true_si_el_usuario_existe() {
        `when`(interfazUsuarioMock.consultarExistenciaUsuario("usuario")).thenReturn(true)

        val resultado = validacion.existeUsuario("usuario")

        assertTrue(resultado)
        verify(interfazUsuarioMock).consultarExistenciaUsuario("usuario")
    }

    @Test
    fun existeUsuario_devuelve_false_si_el_usuario_no_existe() {
        `when`(interfazUsuarioMock.consultarExistenciaUsuario("usuario")).thenReturn(false)

        val resultado = validacion.existeUsuario("usuario")

        assertFalse(resultado)
        verify(interfazUsuarioMock).consultarExistenciaUsuario("usuario")
    }

    @Test
    fun existeMedico_devuelve_true_si_el_médico_existe() {
        `when`(interfazMedicoMock.consultarExistenciaMedico("1234")).thenReturn(true)

        val resultado = validacion.existeMedico("1234")

        assertTrue(resultado)
        verify(interfazMedicoMock).consultarExistenciaMedico("1234")
    }

    @Test
    fun existeMedico_devuelve_false_si_el_médico_no_existe() {
        `when`(interfazMedicoMock.consultarExistenciaMedico("1234")).thenReturn(false)

        val resultado = validacion.existeMedico("1234")

        assertFalse(resultado)
        verify(interfazMedicoMock).consultarExistenciaMedico("1234")
    }

    @Test
    fun esNumColegiadoValidoParaModificacion_devuelve_true_cuando_el_número_no_ha_cambiado() {
        val resultado = validacion.esNumColegiadoValidoParaModificacion("1111", "1111")

        assertTrue(resultado)
        verify(interfazMedicoMock, never()).consultarExistenciaMedico(anyString())
    }

    @Test
    fun esNumColegiadoValidoParaModificacion_devuelve_true_cuando_el_nuevo_número_no_existe() {
        `when`(interfazMedicoMock.consultarExistenciaMedico("2222")).thenReturn(false)

        val resultado = validacion.esNumColegiadoValidoParaModificacion("2222", "1111")

        assertTrue(resultado)
        verify(interfazMedicoMock).consultarExistenciaMedico("2222")
    }

    @Test
    fun esNumColegiadoValidoParaModificacion_devuelve_false_cuando_el_nuevo_número_ya_existe() {
        `when`(interfazMedicoMock.consultarExistenciaMedico("2222")).thenReturn(true)

        val resultado = validacion.esNumColegiadoValidoParaModificacion("2222", "1111")

        assertFalse(resultado)
        verify(interfazMedicoMock).consultarExistenciaMedico("2222")
    }
}