package es.studium.modelos

data class ModeloMedico (
    var idMedicoBD : String,
    var nombreMedicoBD : String,
    var apellidosMedicoBD : String,
    var telefonoMedicoBD : String,
    var emailMedicoBD : String,
    var especialidadMedicoBD : String,
    var numColegiadoMedicoBD : String,
    var esAdminMedicoBD : String,
    var idCentroMedicoFKBD : String,
    var idUsuarioFKBD : String
)