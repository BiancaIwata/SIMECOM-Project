var database = require("../database/config");

function register(name, surname, email, password) {
    var instrucaoSql = `INSERT INTO usuarios (nome, sobrenome, email, senha) VALUES ('${name}', '${surname}', '${email}', '${password}');`;

    return database.executar(instrucaoSql);
}

function auth(email, password){
    var instrucaoSql = `SELECT id, nome, email FROM usuarios WHERE email = '${email}' AND senha = '${password}';`;

    return database.executar(instrucaoSql);
}

module.exports = {
    register,
    auth
};