var database = require("../database/config");

function buscarPreferencias(idUsuario) {
    var instrucaoSql = `SELECT id, estado, municipio, setor FROM preferencias WHERE usuario_id = '${idUsuario}' AND status = 'ativo';`;
    return database.executar(instrucaoSql);
}

function criarPreferencia(preferencia) {
    var instrucaoSql = `INSERT INTO preferencias (usuario_id, estado, municipio, setor) VALUES 
        ('${preferencia.usuarioId}', '${preferencia.estado}', '${preferencia.municipio}', '${preferencia.setor}');`;
    return database.executar(instrucaoSql);
}

function atualizarStatus(id) {
    var instrucaoSql = `UPDATE preferencias SET status = 'inativo' WHERE id = '${id}';`;
    return database.executar(instrucaoSql);
}

function contarPreferenciasAtivas(idUsuario) {
    var instrucaoSql = `SELECT COUNT(*) as total FROM preferencias WHERE usuario_id = '${idUsuario}' AND status = 'ativo';`;
    return database.executar(instrucaoSql);
}

module.exports = {
    buscarPreferencias,
    criarPreferencia,
    atualizarStatus,
    contarPreferenciasAtivas
};