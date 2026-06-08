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
// =========================================================================
// O "ESQUENTA SEGURO": Faz as consultas UMA POR UMA para não travar o banco
// =========================================================================
setTimeout(async () => {
  console.log("🔥 [MUNICÍPIO] Iniciando cache warming sequencial seguro...");
  
  const anosParaEsquentar = [2018, 2019, 2020]; 
  const municipioPadraoParaApresentar = 'São Paulo'; // mude para o seu padrão

  for (const ano of anosParaEsquentar) {
    try {
      console.log(`⏳ [MUNICÍPIO] Pré-processando ${municipioPadraoParaApresentar} em ${ano}...`);
      await buscarSituacaoAnual(ano, municipioPadraoParaApresentar);
      await buscarTopMunicipios(ano);
      await buscarTopMunicipiosImportacao(ano);
      await buscarTopMunicipiosExportacao(ano);
      console.log(`✅ [MUNICÍPIO] Dados de ${ano} guardados na RAM!`);
    } catch (erro) {
      console.error(`❌ [MUNICÍPIO] Erro no ano ${ano}:`, erro);
    }
  }

  console.log("🚀 [MUNICÍPIO] Cache totalmente aquecido!");
}, 8000); // Espera 8 segundos para rodar depois do arquivo de setores