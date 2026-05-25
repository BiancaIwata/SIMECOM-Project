var municipiosModel = require("../models/municipiosModel");

function buscarSituacaoAnual(req, res) {

    var anoInicial = Number(req.query.anoInicial);
    var municipio = req.query.municipio;

    if (!anoInicial || isNaN(anoInicial)) {
        return res.status(400).send("anoInicial é obrigatório.");
    }

    if (!municipio) {
        return res.status(400).send("municipio é obrigatório.");
    }

    municipiosModel.buscarSituacaoAnual(anoInicial, municipio)
        .then(function(resultado) {

            if (resultado.length > 0) {
                res.status(200).json(resultado);
            } else {
                res.status(204).send("Nenhum resultado encontrado!");
            }

        }).catch(function(erro) {

            console.log(erro);

            res.status(500).json(erro.sqlMessage);
        });
}

function buscarTopMunicipios(req, res) {

    var anoInicial = Number(req.query.anoInicial);

    municipiosModel.buscarTopMunicipios(anoInicial)
        .then(function(resultado) {

            if (resultado.length > 0) {
                res.status(200).json(resultado);
            } else {
                res.status(204).send("Nenhum resultado encontrado!");
            }

        }).catch(function(erro) {

            console.log(erro);

            res.status(500).json(erro.sqlMessage);
        });
}

function buscarTopMunicipiosImportacao(req, res) {

    var anoInicial = Number(req.query.anoInicial);

    municipiosModel.buscarTopMunicipiosImportacao(anoInicial)
        .then(function(resultado) {

            if (resultado.length > 0) {
                res.status(200).json(resultado);
            } else {
                res.status(204).send("Nenhum resultado encontrado!");
            }

        }).catch(function(erro) {

            console.log(erro);

            res.status(500).json(erro.sqlMessage);
        });
}

function buscarTopMunicipiosExportacao(req, res) {

    var anoInicial = Number(req.query.anoInicial);

    municipiosModel.buscarTopMunicipiosExportacao(anoInicial)
        .then(function(resultado) {

            if (resultado.length > 0) {
                res.status(200).json(resultado);
            } else {
                res.status(204).send("Nenhum resultado encontrado!");
            }

        }).catch(function(erro) {

            console.log(erro);

            res.status(500).json(erro.sqlMessage);
        });
}

module.exports = {
    buscarSituacaoAnual,
    buscarTopMunicipios,
    buscarTopMunicipiosImportacao,
    buscarTopMunicipiosExportacao
};