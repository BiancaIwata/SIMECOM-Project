var database = require("../database/config");

function register(id, uf, setor, municipio) {
  var instrucaoSql = `
    INSERT INTO preferencias (usuario_id, estado, setor, municipio)
    VALUES ('${id}', '${uf}', '${setor}', '${municipio}');
  `;

  return database.executar(instrucaoSql);
}

module.exports = {
  register,
};
