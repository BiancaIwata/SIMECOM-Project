var preferenciasModel = require("../models/preferenciasModel");

function mostrar(req, res) {
    var idUsuario = req.params.idUsuario;

    preferenciasModel.buscarPreferencias(idUsuario).then((result) => {
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