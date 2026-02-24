var database = require("../database/config");

function cadastrar(name, surname, email, password) {
    var instrucaoSql = `INSERT INTO users (name, surname, email, password) VALUES ('${name}', '${surname}', '${email}', '${password}');`;

    return database.executar(instrucaoSql);
}

function auth(email, password){
    var instrucaoSql = `SELECT id, name, email FROM users WHERE email = '${email}' AND password = '${password}';`;

    return database.executar(instrucaoSql);
}

module.exports = {
    cadastrar,
    auth
};