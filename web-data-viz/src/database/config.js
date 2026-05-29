var mysql = require("mysql2");

var mySqlConfig = {
    host: process.env.DB_HOST,
    database: process.env.DB_DATABASE,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    port: process.env.DB_PORT,
    waitForConnections: true,
    connectionLimit: Number(process.env.DB_POOL_SIZE) || 20,
    queueLimit: 0
};

var pool = mysql.createPool(mySqlConfig);

function executar(instrucao, valores = []) {

    // if (process.env.AMBIENTE_PROCESSO !== "producao" && process.env.AMBIENTE_PROCESSO !== "desenvolvimento") {
    //     console.log("\nO AMBIENTE (produção OU desenvolvimento) NÃO FOI DEFINIDO EM .env OU dev.env OU app.js\n");
    //     return Promise.reject("AMBIENTE NÃO CONFIGURADO EM .env");
    // }

    return new Promise(function (resolve, reject) {
        pool.query(instrucao, valores, function (erro, resultados) {
            if (erro) {
                reject(erro);
                return;
            }
            resolve(resultados);
        });
    });
}

module.exports = {
    executar
};
