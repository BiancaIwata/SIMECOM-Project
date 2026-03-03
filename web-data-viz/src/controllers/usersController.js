var usersModel = require("../models/usersModel");

function cadastrar(req, res) {
    var name = req.body.name;
    var surname = req.body.surname
    var email = req.body.email;
    var password = req.body.password;

    if (!name || !email || !password || !surname) {
        return res.status(400).send("Campos obrigatórios não enviados!");
    }

    if (typeof name !== "string" ||
        typeof email !== "string" ||
        typeof password !== "string" ||
        typeof surname !== "string" ) {
        return res.status(400).send("Tipos inválidos!");
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email)) {
        return res.status(400).send("Email inválido!");
    }

    if (name.length > 254 || surname.length > 254 || email.length > 254 || password.length > 254){
        return res.status(400).send("O texto excede o limite de caracteres.");
    }

    if (password.length < 6) {
        return res.status(400).send("Senha muito curta!");
    }

    if (password.length > 20) {
        return res.status(400).send("Senha muito longa!");
    }

    usersModel.cadastrar(name, surname, email, password)
        .then(
            function (resultado) {
                res.json(resultado);
            }
        ).catch(
            function (erro) {
                console.log(erro);
                console.log(
                    "\nHouve um erro ao realizar o cadastro!"
                );
                res.status(500).json(erro.sqlMessage);
            }
        );

}

function auth(req, res) {
    var email = req.body.email;
    var password = req.body.password;

    if (email == undefined) {
        return res.status(400).send("Email esta como undefined");
    }

    if (password == undefined) {
        return res.status(400).send("Senha esta como undefined")
    }
    
    if (typeof email !== "string" || typeof password !== "string") {
        return res.status(400).send("Tipos inválidos!");
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email)) {
        return res.status(400).send("Email em formato inválido!");
    }

    usersModel.auth(email, password)
        .then(
            function (resultAuth){
                if (resultAuth.length == 1) {
                    if (resultAuth.length > 0) {
                        res.json({
                            id: resultAuth[0].id,
                            email: resultAuth[0].email,
                            name: resultAuth[0].name,
                            password: resultAuth[0].password
                        });
                    } else {
                        res.status(204).json(resultAuth);
                    }
                }else if (resultAuth.length == 0) {
                    res.status(403).send("Credenciais inválidas!");
                }
            }
        ).catch(
            function (erro) {
                console.log(erro);
                console.log("\nHouve um erro ao realizar o login!");
                res.status(500).json(erro.sqlMessage);
            }
        );
}

module.exports = {
    cadastrar,
    auth
}