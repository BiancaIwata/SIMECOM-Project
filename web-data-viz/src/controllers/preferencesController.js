var preferencesModel = require("../models/preferencesModel");

function register(req, res) {
  var id = req.body.id;
  var uf = req.body.uf;
  var setor = req.body.setor;
  var municipio = req.body.municipio;

  if (!id || !uf || !setor || !municipio) {
    return res.status(400).send("Campos obrigatórios não preenchidos");
  }

  preferencesModel
    .register(id, uf, setor, municipio)
    .then(function () {
      res.status(201).send("Preferência cadastrada com sucesso!");
    })
    .catch(function (erro) {
      console.error(erro);
      res.status(500).send("Erro ao cadastrar preferência");
    });
}

module.exports = {
  register,
};
