
var database = require("../database/config");

function buscarSituacaoAnual(anoInicial, municipio) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
        SELECT
            ano,
            ROUND(IFNULL(SUM(importacoes),0) / 1000000, 2) AS importacoes_milhoes_usd,
            ROUND(IFNULL(SUM(exportacoes),0) / 1000000, 2) AS exportacoes_milhoes_usd
        FROM (

            SELECT
                bi.CO_ANO AS ano,
                SUM(bi.VL_FOB) AS importacoes,
                0 AS exportacoes
            FROM base_importacao bi

            INNER JOIN codigo_municipio cm
                ON bi.CO_MUN = cm.CO_MUN_GEO

            WHERE bi.CO_ANO BETWEEN ? AND ?
            AND cm.NO_MUN = ?

            GROUP BY bi.CO_ANO

            UNION ALL

            SELECT
                be.CO_ANO AS ano,
                0 AS importacoes,
                SUM(be.VL_FOB) AS exportacoes
            FROM base_exportacao be

            INNER JOIN codigo_municipio cm
                ON be.CO_MUN = cm.CO_MUN_GEO

            WHERE be.CO_ANO BETWEEN ? AND ?
            AND cm.NO_MUN = ?

            GROUP BY be.CO_ANO

        ) consolidado

        GROUP BY ano
        ORDER BY ano;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal, municipio, anoInicial, anoFinal, municipio]);
}

function buscarTopMunicipios(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
    SELECT
        municipio,
        SUM(valor_total) AS valor_total
    FROM (
        SELECT
            cm.NO_MUN AS municipio,
            SUM(bi.VL_FOB) AS valor_total
        FROM base_importacao bi
        INNER JOIN codigo_municipio cm
                ON bi.CO_MUN = cm.CO_MUN_GEO
        WHERE bi.CO_ANO BETWEEN ? AND ?
        GROUP BY cm.NO_MUN
        UNION ALL
        SELECT
            cm.NO_MUN AS municipio,
            SUM(be.VL_FOB) AS valor_total
        FROM base_exportacao be
        INNER JOIN codigo_municipio cm
            ON be.CO_MUN = cm.CO_MUN_GEO
        WHERE be.CO_ANO BETWEEN ? AND ?
        GROUP BY cm.NO_MUN
        ) consolidado
    GROUP BY municipio
    ORDER BY valor_total DESC
    LIMIT 10;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal, anoInicial, anoFinal]);
}

function buscarTopMunicipiosImportacao(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
    SELECT
        cm.NO_MUN AS municipio,
         SUM(bi.VL_FOB) AS valor_total
    FROM base_importacao bi
        INNER JOIN codigo_municipio cm
            ON bi.CO_MUN = cm.CO_MUN_GEO
        WHERE bi.CO_ANO BETWEEN ? AND ?
        GROUP BY cm.NO_MUN
        ORDER BY valor_total DESC;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal]);
}

function buscarTopMunicipiosExportacao(anoInicial) {
  const anoFinal = anoInicial + 5;

  var instrucaoSql = `
  SELECT
    cm.NO_MUN AS municipio,
    SUM(be.VL_FOB) AS valor_total
    FROM base_exportacao be
    INNER JOIN codigo_municipio cm
        ON be.CO_MUN = cm.CO_MUN_GEO
    WHERE be.CO_ANO BETWEEN ? AND ?

    GROUP BY cm.NO_MUN
    ORDER BY valor_total DESC;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal]);
}

module.exports = {
    buscarSituacaoAnual,
    buscarTopMunicipios,
    buscarTopMunicipiosImportacao,
    buscarTopMunicipiosExportacao
};
