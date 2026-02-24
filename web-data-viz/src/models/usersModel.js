var database = require("../database/config");

function cadastrar(name, email, password) {

    var instrucaoSql = `INSERT INTO users (name, email, password) VALUES ('${name}', '${email}', '${password}');`;
    
    console.log("Executando a instrução SQL: \n" + instrucaoSql);
    return database.executar(instrucaoSql);
}

module.exports = {
    cadastrar
};