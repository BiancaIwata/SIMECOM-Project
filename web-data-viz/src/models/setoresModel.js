var database = require("../database/config");

// Objeto que vai guardar os dados na memória RAM (Cache)
const cacheSetores = {};

function buscarSituacaoMercado(anoInicial) {
  const anoFinal = anoInicial + 5;
  const chave = `situacao_mercado_${anoInicial}`;

  // Se já tiver no cache, devolve instantaneamente
  if (cacheSetores[chave]) {
    console.log(`[CACHE HIT] Setores - Situação Mercado (${anoInicial}) carregado da memória!`);
    return Promise.resolve(cacheSetores[chave]);
  }

  var instrucaoSql = `
  SELECT
    ano,
    importacoes_milhoes_usd,
    exportacoes_milhoes_usd
    FROM vw_situacao_mercado
    WHERE ano BETWEEN ? AND ?
    ORDER BY ano;
  `;

  // Executa no banco e salva no cache antes de retornar
  return database.executar(instrucaoSql, [anoInicial, anoFinal]).then(resultado => {
    cacheSetores[chave] = resultado;
    return resultado;
  });
}

function buscarTopSetores(anoInicial) {
  const anoFinal = anoInicial + 5;
  const chave = `top_setores_${anoInicial}`;

  if (cacheSetores[chave]) {
    console.log(`[CACHE HIT] Setores - Top Setores (${anoInicial}) carregado da memória!`);
    return Promise.resolve(cacheSetores[chave]);
  }

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

  return database.executar(instrucaoSql, [anoInicial, anoFinal]).then(resultado => {
    cacheSetores[chave] = resultado;
    return resultado;
  });
}

function buscarTopSetoresExpotacao(anoInicial) {
  const anoFinal = anoInicial + 5;
  const chave = `top_setores_exp_${anoInicial}`;

  if (cacheSetores[chave]) {
    return Promise.resolve(cacheSetores[chave]);
  }

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

  return database.executar(instrucaoSql, [anoInicial, anoFinal]).then(resultado => {
    cacheSetores[chave] = resultado;
    return resultado;
  });
}

function buscarTopSetoresImportacao(anoInicial) {
  const anoFinal = anoInicial + 5;
  const chave = `top_setores_imp_${anoInicial}`;

  if (cacheSetores[chave]) {
    return Promise.resolve(cacheSetores[chave]);
  }

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

  return database.executar(instrucaoSql, [anoInicial, anoFinal]).then(resultado => {
    cacheSetores[chave] = resultado;
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
  console.log("🔥 [SETOR] Iniciando cache warming sequencial seguro...");
  
  const anosParaEsquentar = [2018, 2019, 2020]; 
  
  for (const ano of anosParaEsquentar) {
    try {
      console.log(`⏳ [SETOR] Pré-processando o ano ${ano} no banco...`);
      await buscarSituacaoMercado(ano);
      await buscarTopSetores(ano);
      await buscarTopSetoresExpotacao(ano);
      await buscarTopSetoresImportacao(ano);
      console.log(`✅ [SETOR] Ano ${ano} guardado com sucesso na RAM!`);
    } catch (erro) {
      console.error(`❌ [SETOR] Erro ao carregar ano ${ano}:`, erro);
    }
  }
  
  console.log("🚀 [SETOR] Cache totalmente aquecido e pronto para a banca!");
}, 5000); // Espera 5 segundos para o banco ligar totalmente