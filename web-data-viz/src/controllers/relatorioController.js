var relatorioService = require("../service/relatorioService");
var path = require("path");

function gerar(req, res) {
  relatorioService
    .gerarRelatorio()
    .then(function () {
      const arquivo = path.resolve(
        "../simecom-report-app/output/relatorio_comex.pdf",
      );

      res.download(arquivo, "relatorio.pdf");
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
