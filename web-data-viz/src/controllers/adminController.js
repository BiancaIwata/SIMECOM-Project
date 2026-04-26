var adminModel = require("../models/adminModel");

function listarUsuarios(req, res) {
  adminModel
    .listarUsuarios()
    .then((resultado) => {
      res.status(200).json(resultado);
    })
    .catch((erro) => {
      console.error(erro);
      res.status(500).json(erro);
    });
}
function deletarUsuario(req, res) {
  const { id } = req.body;

  if (!id) {
    return res.status(400).send("ID não enviado");
  }

  adminModel
    .deletarUsuario(id)
    .then(() => {
      res.sendStatus(200);
    })
    .catch((erro) => {
      console.error(erro);
      res.status(500).json(erro);
    });
}

module.exports = {
  listarUsuarios,
  deletarUsuario,
};
