var setoresModel = require("../models/setoresModel");

function buscarSituacaoMercado(req, res) {
  var anoInicial = Number(req.query.anoInicial);

  if (!anoInicial || isNaN(anoInicial)) {
    return res.status(400).send("anoInicial é obrigatório.");
  }

  setoresModel.buscarSituacaoMercado(anoInicial)
    .then(function (resultado) {
      if (resultado.length > 0) {
        res.status(200).json(resultado);
      } else {
        res.status(204).send("Nenhum resultado encontrado!");
      }
    }).catch(function (erro) {
      console.log(erro);
      console.log("Houve um erro ao buscar situação do mercado.", erro.sqlMessage);
      res.status(500).json(erro.sqlMessage);
    });
}

function buscarTopSetores(req, res) {
  var anoInicial = Number(req.query.anoInicial);

  if (!anoInicial || isNaN(anoInicial)) {
    return res.status(400).send("anoInicial é obrigatório.");
  }

  setoresModel.buscarTopSetores(anoInicial)
    .then(function (resultado) {
      if (resultado.length > 0) {
        res.status(200).json(resultado);
      } else {
        res.status(204).send("Nenhum resultado encontrado!");
      }
    }).catch(function (erro) {
      console.log(erro);
      console.log("Houve um erro ao buscar top setores.", erro.sqlMessage);
      res.status(500).json(erro.sqlMessage);
    });
}

function buscarTopSetoresExportacao(req, res) {
  var anoInicial = Number(req.query.anoInicial);

  if (!anoInicial || isNaN(anoInicial)) {
    return res.status(400).send("anoInicial é obrigatório.");
  }

  setoresModel.buscarTopSetoresExpotacao(anoInicial)
    .then(function (resultado) {
      if (resultado.length > 0) {
        res.status(200).json(resultado);
      } else {
        res.status(204).send("Nenhum resultado encontrado!");
      }
    }).catch(function (erro) {
      console.log(erro);
      console.log("Houve um erro ao buscar top setores de exportação.", erro.sqlMessage);
      res.status(500).json(erro.sqlMessage);
    });
}

function buscarTopSetoresImportacao(req, res) {
  var anoInicial = Number(req.query.anoInicial);

  if (!anoInicial || isNaN(anoInicial)) {
    return res.status(400).send("anoInicial é obrigatório.");
  }

  setoresModel.buscarTopSetoresImportacao(anoInicial)
    .then(function (resultado) {
      if (resultado.length > 0) {
        res.status(200).json(resultado);
      } else {
        res.status(204).send("Nenhum resultado encontrado!");
      }
    }).catch(function (erro) {
      console.log(erro);
      console.log("Houve um erro ao buscar top setores de importação.", erro.sqlMessage);
      res.status(500).json(erro.sqlMessage);
    });
}

module.exports = {
  buscarSituacaoMercado,
  buscarTopSetores,
  buscarTopSetoresExportacao,
  buscarTopSetoresImportacao
};