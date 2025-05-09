package es.studium.modelos_y_utiles

data class ModeloMedico (
    var idMedico : String,
    var nombreMedico : String,
    var apellidosMedico : String,
    var telefonoMedico : String,
    var emailMedico : String,
    var especialidadMedico : String,
    var numColegiadoMedico : String,
    var esAdminMedico : String,
    var idCentroMedicoFK : String,
    var idUsuarioFK : String
)