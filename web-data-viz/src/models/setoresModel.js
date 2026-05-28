var database = require("../database/config");

function buscarSituacaoMercado(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
  SELECT
    ano,
    importacoes_milhoes_usd,
    exportacoes_milhoes_usd
    FROM vw_situacao_mercado
    WHERE ano BETWEEN ? AND ?
    ORDER BY ano;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal]);
}

function buscarTopSetores(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
  SELECT
    id,
    nome,
    SUM(exportacoes + importacoes) AS valor_total
    FROM vw_valor_total_por_setor
    WHERE ano BETWEEN ? AND ?
    GROUP BY id, nome
    ORDER BY valor_total DESC;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal]);
}

function buscarTopSetoresExpotacao(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
  SELECT
    id,
    nome,
    IFNULL(SUM(valor_total), 0) AS valor_total
    FROM vw_exportacoes_por_setor
    WHERE ano BETWEEN ? AND ?
    OR ano IS NULL
    GROUP BY id, nome
    ORDER BY valor_total DESC;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal]);
}

function buscarTopSetoresImportacao(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
  SELECT
    id,
    nome,
    IFNULL(SUM(valor_total), 0) AS valor_total
    FROM vw_importacoes_por_setor
    WHERE ano BETWEEN ? AND ?
    OR ano IS NULL
    GROUP BY id, nome
    ORDER BY valor_total DESC;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal]);
}

module.exports = {
  buscarSituacaoMercado,
  buscarTopSetores,
  buscarTopSetoresExpotacao,
  buscarTopSetoresImportacao
};
