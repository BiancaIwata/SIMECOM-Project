var preferenciasModel = require("../models/preferenciasModel");

function mostrar(req, res) {
    var idUsuario = req.params.idUsuario;

    preferenciasModel.buscarPreferencias(idUsuario).then((resultado) => {
        if (resultado.length > 0) {
            console.log(resultado)
            res.json(resultado);
        } else {
            res.status(204).json(resultado);
        }
    }).catch(function (erro) {
        console.log(erro);
        console.log("Houve um erro ao buscar as preferências: ", erro.sqlMessage);
        res.status(500).json(erro.sqlMessage);
    });
}

function atualizar(req, res) {
    var id = req.params.id;

    var preferencia = {
        usuarioId: req.body.usuario_id,
        estado: req.body.estado,
        municipio: req.body.municipio,
        setor: req.body.setor
    };

    preferenciasModel.criarPreferencia(preferencia)
        .then(function (resultado) {
            console.log(resultado)
            preferenciasModel.atualizarStatus(id)
                .then(result => {
                    if (result && result.affectedRows > 0) {
                        res.status(200).json({ mensagem: "Preferência atualizada com sucesso." });
                    }
                })
                .catch(function (erro) {
                    console.log(erro);
                    console.log("Houve um erro ao atualizar a preferência: ", erro.sqlMessage);
                    res.status(500).json(erro.sqlMessage);
                });
        })
        .catch(function (erro) {
            console.log(erro);
            console.log("Houve um erro ao atualizar a preferência: ", erro.sqlMessage);
            res.status(500).json(erro.sqlMessage);
        });
}

function deletar(req, res) {
    var id = req.params.id;

    preferenciasModel.atualizarStatus(id)
        .then(result => {
            if (result && result.affectedRows > 0) {
                res.status(200).json({ mensagem: "Preferência atualizada com sucesso." });
            }
        })
        .catch(function (erro) {
            console.log(erro);
            console.log("Houve um erro ao atualizar a preferência: ", erro.sqlMessage);
            res.status(500).json(erro.sqlMessage);
        });
}

module.exports = {
    mostrar,
    atualizar,
    deletar
}