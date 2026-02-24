var database = require("../database/config");

function cadastrar(name, surname, email, password) {

    var instrucaoSql = `INSERT INTO users (name, surname, email, password) VALUES ('${name}', '${surname}', '${email}', '${password}');`;
    
    console.log("Executando a instrução SQL: \n" + instrucaoSql);
    return database.executar(instrucaoSql);
}

module.exports = {
    cadastrar
};