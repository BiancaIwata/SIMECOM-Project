
var database = require("../database/config");

function buscarSituacaoAnual(anoInicial, municipio) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
    SELECT
    ano,
    importacoes_milhoes_usd,
    exportacoes_milhoes_usd
    FROM vw_situacao_anual_municipios
    WHERE municipio = ?
    AND ano BETWEEN ? AND ?
    ORDER BY ano;`

  console.log({ metodo: "buscarSituacaoAnual", sql: instrucaoSql, anoInicial: anoInicial, anoFinal: anoFinal, municipio: municipio })
  return database.executar(instrucaoSql, [municipio, anoInicial, anoFinal]);
}

function buscarTopMunicipios(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
    SELECT
    municipio,
    SUM(valor_total) AS valor_total
    FROM vw_ranking_municipios
    WHERE ano BETWEEN ? AND ?
    GROUP BY municipio
    ORDER BY valor_total DESC
    LIMIT 10;
  `;

  console.log({ metodo: "buscarTopMunicipios", sql: instrucaoSql, anoInicial: anoInicial, anoFinal: anoFinal })
  return database.executar(instrucaoSql, [anoInicial, anoFinal]);
}

function buscarTopMunicipiosImportacao(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
    SELECT
    municipio,
    SUM(valor_total) AS valor_total
    FROM vw_importacoes_por_municipio
    WHERE ano BETWEEN ? AND ?
    GROUP BY municipio
    ORDER BY valor_total DESC
    LIMIT 10;
  `;

  console.log({ metodo: "buscarTopMunicipiosImportacao", sql: instrucaoSql, anoInicial: anoInicial, anoFinal: anoFinal })
  return database.executar(instrucaoSql, [anoInicial, anoFinal]);
}

function buscarTopMunicipiosExportacao(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
  SELECT
    municipio,
    SUM(valor_total) AS valor_total
    FROM vw_exportacoes_por_municipio
    WHERE ano BETWEEN ? AND ?
    GROUP BY municipio
    ORDER BY valor_total DESC
    LIMIT 10;
  `;

  console.log({ metodo: "buscarTopMunicipiosExportacao", sql: instrucaoSql, anoInicial: anoInicial, anoFinal: anoFinal })
  return database.executar(instrucaoSql, [anoInicial, anoFinal]);
}

module.exports = {
    buscarSituacaoAnual,
    buscarTopMunicipios,
    buscarTopMunicipiosImportacao,
    buscarTopMunicipiosExportacao
};
