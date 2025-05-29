package es.studium.diagnoskin_app

import es.studium.modelos_y_utiles.InterfazCredenciales
import es.studium.modelos_y_utiles.ValidacionesOtrasBBDD
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`


class ValidacionesOtrasBBDDTest {
    private lateinit var interfazMock: InterfazCredenciales
    private lateinit var validador: ValidacionesOtrasBBDD

    @Before
    fun setUp() {
        // Creamos el mock de la interfaz
        interfazMock = mock(InterfazCredenciales::class.java)
        // Inyectamos el mock en la clase a testear
        validador = ValidacionesOtrasBBDD(interfazMock)
    }

    @Test
    fun sonCredencialesCorrectas_devuelve_true_cuando_credenciales_son_correctas() {
        `when`(interfazMock.comprobarCredenciales("usuario", "clave")).thenReturn(true)

        val resultado = validador.sonCredencialesCorrectas("usuario", "clave")

        assertTrue(resultado)
        verify(interfazMock).comprobarCredenciales("usuario", "clave")
    }

    @Test
    fun sonCredencialesCorrectas_devuelve_false_cuando_credenciales_son_incorrectas() {
        `when`(interfazMock.comprobarCredenciales("usuario", "malaclave")).thenReturn(false)

        val resultado = validador.sonCredencialesCorrectas("usuario", "malaclave")

        assertFalse(resultado)
        verify(interfazMock).comprobarCredenciales("usuario", "malaclave")
    }

    @Test
    fun estaBloqueado_devuelve_true_cuando_el_usuario_está_bloqueado() {
        `when`(interfazMock.comprobarBloqueo("bloqueado")).thenReturn(true)

        val resultado = validador.estaBloqueado("bloqueado")

        assertTrue(resultado)
        verify(interfazMock).comprobarBloqueo("bloqueado")
    }

    @Test
    fun estaBloqueado_devuelve_false_cuando_el_usuario_NO_está_bloqueado() {
        `when`(interfazMock.comprobarBloqueo("libre")).thenReturn(false)

        val resultado = validador.estaBloqueado("libre")

        assertFalse(resultado)
        verify(interfazMock).comprobarBloqueo("libre")
    }

}