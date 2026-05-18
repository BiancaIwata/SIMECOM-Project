var forumModel = require("../models/forumModel");

function forumGetter(req, res) {
  forumModel
    .forumGetter()
    .then(function (resultado) {
      res.status(200).json(resultado);
    })
    .catch(function (erro) {
      console.error(erro);
      res.status(500).json(erro);
    });
}

function forumTop5(req, res) {
  forumModel
    .forumTop5()
    .then(function (resultado) {
      res.status(200).json(resultado);
    })
    .catch(function (erro) {
      console.error(erro);
      res.status(500).json(erro);
    });
}

function postComment(req, res) {
  const titulo = req.body.title;
  const conteudo = req.body.content;
  const usuarioId = req.body.id;

  if (!titulo || !conteudo || !usuarioId) {
    return res.status(400).send("Campos obrigatórios não preenchidos");
  }

  forumModel
    .postComment(titulo, conteudo, usuarioId)
    .then(function (resultado) {
      res.status(200).json({ message: "Post criado com sucesso" });
    })
    .catch(function (erro) {
      console.error("Erro ao criar post:", erro);
      res.status(500).json(erro);
    });
}

function givaLike(req, res) {
  const postId = req.body.post_id;
  const usuarioId = req.body.usuario_id;

  if (!postId || !usuarioId) {
    return res.status(400).send("Dados inválidos");
  }

  forumModel
    .givaLike(postId, usuarioId)
    .then(() => {
      res.status(200).json({ message: "Like registrado" });
    })
    .catch((erro) => {
      console.error(erro);
      res.status(500).json(erro);
    });
}

module.exports = {
  forumGetter,
  forumTop5,
  postComment,
  givaLike,
};
