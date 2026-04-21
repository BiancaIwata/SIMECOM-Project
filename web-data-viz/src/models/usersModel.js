var database = require("../database/config");

function register(name, surname, email, password) {
  var instrucaoSql = `INSERT INTO usuarios (nome, sobrenome, email, senha) VALUES ('${name}', '${surname}', '${email}', '${password}');`;

  return database.executar(instrucaoSql);
}

function auth(email, password) {
  var instrucaoSql = `SELECT id, nome, email, status FROM usuarios WHERE email = '${email}' AND senha = '${password}';`;

  return database.executar(instrucaoSql);
}

function getter(id) {
  var instrucaoSql = `SELECT id, nome, email, senha FROM usuarios WHERE id = '${id}';`;

  return database.executar(instrucaoSql);
}

function setter(id, nome, email, senha) {
  var instrucaoSql = `
    UPDATE usuarios 
    SET nome = '${nome}', 
        email = '${email}', 
        senha = '${senha}'
    WHERE id = '${id}';
  `;

  return database.executar(instrucaoSql);
}

function deleter(id) {
  var instrucaoSql = `
    UPDATE usuarios 
    SET status = 'inativo'
    WHERE id = '${id}';
  `;

  return database.executar(instrucaoSql);
}

module.exports = {
  register,
  auth,
  getter,
  setter,
  deleter,
};
