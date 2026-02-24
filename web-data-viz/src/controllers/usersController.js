var usersModel = require("../models/usersModel");

function cadastrar(req, res) {
    var name = req.body.name;
    var email = req.body.email;
    var password = req.body.password;

    if (!name || !email || !password) {
        return res.status(400).send("Campos obrigatórios não enviados!");
    }

    if (typeof name !== "string" ||
        typeof email !== "string" ||
        typeof password !== "string") {
        return res.status(400).send("Tipos inválidos!");
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email)) {
        return res.status(400).send("Email inválido!");
    }

    if (name.length > 254 || email.length > 254 || password.length > 254){
        return res.status(400).send("O texto excede o limite de caracteres.");
    }

    if (password.length < 6) {
        return res.status(400).send("Senha muito curta!");
    }

    if (password.length > 20) {
        return res.status(400).send("Senha muito longa!");
    }

    usersModel.cadastrar(name, email, password)
        .then(
            function (resultado) {
                res.json(resultado);
            }
        ).catch(
            function (erro) {
                console.log(erro);
                console.log(
                    "\nHouve um erro ao realizar o cadastro! Erro: ",
                    erro.sqlMessage
                );
                res.status(500).json(erro.sqlMessage);
            }
        );

}

module.exports = {
    cadastrar
}