var relatorioService = require("../service/relatorioService");
var path = require("path");

function gerar(req, res) {
  relatorioService
    .gerarRelatorio()
    .then(function () {
      const arquivo = path.resolve(
        "/home/ec2-user/relatorios/saida/relatorio.xlsx",
      );

      res.download(arquivo, "relatorio.xlsx");
    })
    .catch(function (erro) {
      console.error(erro);

      res.status(500).json({
        sucesso: false,
        mensagem: "Erro ao gerar relatório",
      });
    });
}

module.exports = {
  gerar,
};
