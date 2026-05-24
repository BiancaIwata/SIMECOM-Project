var database = require("../database/config");

function buscarSituacaoMercado(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
  SELECT
    ano,
    ROUND(SUM(importacoes) / 1000000, 2) AS importacoes_milhoes_usd,
    ROUND(SUM(exportacoes) / 1000000, 2) AS exportacoes_milhoes_usd
  FROM (
      SELECT
        CO_ANO AS ano,
        SUM(VL_FOB) AS importacoes,
        0 AS exportacoes
      FROM base_importacao
      WHERE CO_ANO BETWEEN ? AND ?
      GROUP BY CO_ANO
      UNION ALL
      SELECT
        CO_ANO AS ano,
        0 AS importacoes,
        SUM(VL_FOB) AS exportacoes
      FROM base_exportacao
      WHERE CO_ANO BETWEEN ? AND ?
      GROUP BY CO_ANO
  ) AS consolidado
  GROUP BY ano
  ORDER BY ano;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal, anoInicial, anoFinal]);
}

function buscarTopSetores(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
  SELECT
      s.id,
      s.nome,
      IFNULL(exp.valor, 0) + IFNULL(imp.valor, 0) AS valor_total
  FROM setores s
  LEFT JOIN (
      SELECT 
          sh4.fk_setor AS setor,
          SUM(base.VL_FOB) AS valor
      FROM base_exportacao base
      INNER JOIN codigo_sh4 sh4
          ON base.SH4 = sh4.CO_SH4
      WHERE base.CO_ANO BETWEEN ? AND ?
      GROUP BY sh4.fk_setor
  ) exp ON exp.setor = s.id
  LEFT JOIN (
      SELECT 
          sh4.fk_setor AS setor,
          SUM(base.VL_FOB) AS valor
      FROM base_importacao base
      INNER JOIN codigo_sh4 sh4
          ON base.SH4 = sh4.CO_SH4
      WHERE base.CO_ANO BETWEEN ? AND ?
      GROUP BY sh4.fk_setor
  ) imp ON imp.setor = s.id
  ORDER BY valor_total DESC;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal, anoInicial, anoFinal]);
}

function buscarTopSetoresExpotacao(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
  SELECT 
      s.id,
      s.nome,
      IFNULL(SUM(base.VL_FOB), 0) AS valor_total
  FROM setores s
  LEFT JOIN codigo_sh4 sh4 
      ON sh4.fk_setor = s.id
  LEFT JOIN base_exportacao base
      ON base.SH4 = sh4.CO_SH4
      AND base.CO_ANO BETWEEN ? AND ?
  GROUP BY s.id, s.nome
  ORDER BY valor_total DESC;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal]);
}

function buscarTopSetoresImportacao(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
  SELECT 
      s.id,
      s.nome,
      IFNULL(SUM(base.VL_FOB), 0) AS valor_total
  FROM setores s
  LEFT JOIN codigo_sh4 sh4 
      ON sh4.fk_setor = s.id
  LEFT JOIN base_importacao base
      ON base.SH4 = sh4.CO_SH4
      AND base.CO_ANO BETWEEN ? AND ?
  GROUP BY s.id, s.nome
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
