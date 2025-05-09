package es.studium.modelos_y_utiles


data class ModeloDiagnostico (
    var idDiagnostico : String,
    var fechaDiagnostico : String,
    var diagnosticoDiagnostico : String,
    var gravedadDiagnostico : String,
    var fotoDiagnostico : ByteArray,
    var idMedicoFK : String,
    var idPacienteFK : String
)