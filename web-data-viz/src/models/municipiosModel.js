var database = require("../database/config");

// Objeto que vai guardar os dados na memória RAM (Cache)
const cacheMunicipios = {};

function buscarSituacaoAnual(anoInicial, municipio) {
  const anoFinal = anoInicial + 5;
  // A chave aqui precisa ter o nome do município e o ano para não misturar os dados
  const chave = `sit_anual_${municipio}_${anoInicial}`;

  if (cacheMunicipios[chave]) {
    console.log(`[CACHE HIT] Municípios - Situação Anual (${municipio} - ${anoInicial}) carregado da memória!`);
    return Promise.resolve(cacheMunicipios[chave]);
  }

  var instrucaoSql = `
    SELECT
    ano,
    importacoes_milhoes_usd,
    exportacoes_milhoes_usd
    FROM vw_situacao_anual_municipios
    WHERE municipio = ?
    AND ano BETWEEN ? AND ?
    ORDER BY ano;`

  return database.executar(instrucaoSql, [municipio, anoInicial, anoFinal]).then(resultado => {
    cacheMunicipios[chave] = resultado;
    return resultado;
  });
}

function buscarTopMunicipios(anoInicial) {
  const anoFinal = anoInicial + 5;
  const chave = `top_municipios_${anoInicial}`;

  if (cacheMunicipios[chave]) {
    console.log(`[CACHE HIT] Municípios - Top Municípios (${anoInicial}) carregado da memória!`);
    return Promise.resolve(cacheMunicipios[chave]);
  }

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

  return database.executar(instrucaoSql, [anoInicial, anoFinal]).then(resultado => {
    cacheMunicipios[chave] = resultado;
    return resultado;
  });
}

function buscarTopMunicipiosImportacao(anoInicial) {
  const anoFinal = anoInicial + 5;
  const chave = `top_mun_imp_${anoInicial}`;

  if (cacheMunicipios[chave]) {
    return Promise.resolve(cacheMunicipios[chave]);
  }

  var instrucaoSql = `
    SELECT
    municipio,
    SUM(valor_total) AS valor_total
    FROM vw_importacoes_por_municipio
    WHERE ano BETWEEN ? AND ?
    GROUP BY municipio
    ORDER BY valor_total DESC;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal]).then(resultado => {
    cacheMunicipios[chave] = resultado;
    return resultado;
  });
}

function buscarTopMunicipiosExportacao(anoInicial) {
  const anoFinal = anoInicial + 5;
  const chave = `top_mun_exp_${anoInicial}`;

  if (cacheMunicipios[chave]) {
    return Promise.resolve(cacheMunicipios[chave]);
  }

  var instrucaoSql = `
  SELECT
    municipio,
    SUM(valor_total) AS valor_total
    FROM vw_exportacoes_por_municipio
    WHERE ano BETWEEN ? AND ?
    GROUP BY municipio
    ORDER BY valor_total DESC;
  `;

  return database.executar(instrucaoSql, [anoInicial, anoFinal]).then(resultado => {
    cacheMunicipios[chave] = resultado;
    return resultado;
  });
}

// =========================================================================
// Faz as consultas sozinho para encher a RAM ao iniciar a API
// =========================================================================
setTimeout(() => {
  console.log("🔥 [MUNICÍPIO] Iniciando cache warming para a apresentação...");
  
  // Anos de exemplo que você vai apresentar
  const anosParaEsquentar = [2018, 2019, 2020]; 
  // Município de exemplo que estará selecionado por padrão na sua tela
  const municipioPadraoParaApresentar = 'São Paulo'; 

  anosParaEsquentar.forEach(ano => {
    buscarSituacaoAnual(ano, municipioPadraoParaApresentar);
    buscarTopMunicipios(ano);
    buscarTopMunicipiosImportacao(ano);
    buscarTopMunicipiosExportacao(ano);
  });
}, 15000); // 2.5s para não conflitar muito com o carregamento do outro arquivo

module.exports = {
    buscarSituacaoAnual,
    buscarTopMunicipios,
    buscarTopMunicipiosImportacao,
    buscarTopMunicipiosExportacao
};