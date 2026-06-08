var database = require("../database/config");

function atualizar(accessToken, userId, simecomUserId) {
  var instrucaoSql = `UPDATE usuarios SET slack_token = ?, slack_user_id = ? WHERE id = ?`;

  return database.executar(instrucaoSql, [accessToken, userId, simecomUserId]);
}

module.exports = {
  atualizar
};
