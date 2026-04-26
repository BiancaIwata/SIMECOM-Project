var database = require("../database/config");

function listarUsuarios() {
  var instrucaoSql = `
    SELECT 
      id,
      nome,
      sobrenome,
      email,
      status,
      type,
      created_at
    FROM usuarios
    ORDER BY created_at DESC;
  `;

  return database.executar(instrucaoSql);
}

function deletarUsuario(id) {
  var instrucaoSql = `
    DELETE FROM usuarios WHERE id = ${id};
  `;
  return database.executar(instrucaoSql);
}

module.exports = {
  listarUsuarios,
  deletarUsuario,
};
