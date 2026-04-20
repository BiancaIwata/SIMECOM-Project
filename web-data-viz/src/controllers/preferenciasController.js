var preferenciasModel = require("../models/preferenciasModel");

function mostrar(req, res) {
    var idUsuario = req.params.idUsuario;

    preferenciasModel.buscarPreferencias(idUsuario).then((resultado) => {
        if (resultado.length > 0) {
            res.status(200).json(resultado);
        } else {
            res.status(204).json([]);
        }
    }).catch(function (erro) {
        console.log(erro);
        console.log("Houve um erro ao buscar as preferências: ", erro.sqlMessage);
        res.status(500).json(erro.sqlMessage);
    });
}

function atualizar(req, res) {
    var id = req.params.id;
    var usuarioId = req.body.usuario_id;
    var estado = req.body.estado;
    var municipio = req.body.municipio;
    var setor = req.body.setor;
 
    if (!usuarioId || !estado || !municipio || !setor) {
        return res.status(400).send("Campos obrigatórios não enviados.");
    }
 
    preferenciasModel.atualizarPreferencia(id, usuarioId, estado, municipio, setor)
        .then(function (resultado) {
            if (resultado.affectedRows == 0) {
                return res.status(404).send("Preferência não encontrada.");
            }
            res.status(200).json({ mensagem: "Preferência atualizada com sucesso." });
        })
        .catch(function (erro) {
            console.log(erro);
            console.log("Houve um erro ao atualizar a preferência: ", erro.sqlMessage);
            res.status(500).json(erro.sqlMessage);
        });
}

function deletar(req, res) {
    var id = req.params.id; 
    var usuarioId = req.body.usuario_id;
 
    if (!usuarioId) {
        return res.status(400).send("usuario_id é obrigatório");
    }
 
    preferenciasModel.deletarPreferencia(id, usuarioId)
        .then(function (resultado) {
            if (resultado.affectedRows == 0) {
                return res.status(404).send("Preferência não encontrada.");
            }
            res.status(200).json({ mensagem: "Preferência deletada com sucesso." });
        })
        .catch(function (erro) {
            console.log(erro);
            console.log("Houve um erro ao deletar a preferência: ", erro.sqlMessage);
            res.status(500).json(erro.sqlMessage);
        });
}

module.exports = {
    mostrar,
    atualizar,
    deletar
}