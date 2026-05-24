var database = require("../database/config");

function buscarTopSetores(inicio, final) {
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
      WHERE base.CO_ANO BETWEEN '${inicio}' AND '${final}'
      GROUP BY sh4.fk_setor
  ) exp ON exp.setor = s.id
  LEFT JOIN (
      SELECT 
          sh4.fk_setor AS setor,
          SUM(base.VL_FOB) AS valor
      FROM base_importacao base
      INNER JOIN codigo_sh4 sh4
          ON base.SH4 = sh4.CO_SH4
      WHERE base.CO_ANO BETWEEN '${inicio}' AND '${final}'
      GROUP BY sh4.fk_setor
  ) imp ON imp.setor = s.id
  ORDER BY valor_total DESC;
  `;

  return database.executar(instrucaoSql);
}

function buscarTopSetoresExpotacao(inicio, final) {
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
      AND base.CO_ANO BETWEEN '${inicio}' AND '${final}'
  GROUP BY s.id, s.nome
  ORDER BY valor_total DESC;
  `;

  return database.executar(instrucaoSql);
}

function buscarTopSetoresImportacao(inicio, final) {
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
      AND base.CO_ANO BETWEEN '${inicio}' AND '${final}'
  GROUP BY s.id, s.nome
  ORDER BY valor_total DESC;
  `;

  return database.executar(instrucaoSql);
}

module.exports = {
  buscarTopSetores,
  buscarTopSetoresExpotacao,
  buscarTopSetoresImportacao
};
