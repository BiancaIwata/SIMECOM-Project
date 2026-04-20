var database = require("../database/config");

function buscarPreferencias(idUsuario) {
    var instrucaoSql = `SELECT id, estado, municipio, setor FROM preferencias WHERE usuario_id = '${idUsuario}';`;

    return database.executar(instrucaoSql);
}


module.exports = {
    buscarPreferencias
};