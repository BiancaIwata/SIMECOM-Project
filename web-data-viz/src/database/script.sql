-- ===========================
-- DATABASE: CREATE DATABASE
-- ===========================
DROP DATABASE IF EXISTS simecom;

CREATE DATABASE simecom;

USE simecom;

-- ===========================
-- TABELA: usuarios (principal)
-- ===========================
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    sobrenome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    status TINYINT(1) DEFAULT 1,
    type CHAR(4) CONSTRAINT chk_type CHECK (type IN ('USER', 'ADM')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ===========================
-- TABELA: empresas (depende de usuarios)
-- ===========================
CREATE TABLE empresas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    cnpj CHAR(14) NOT NULL UNIQUE,
    email VARCHAR(150),
    telefone VARCHAR(20),
    uf CHAR(2),
    type CHAR(4) CONSTRAINT chek_type CHECK (type IN ('USER', 'ADM')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

-- ===========================
-- Tabela de preferencias
-- ===========================
CREATE TABLE preferencias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    estado VARCHAR(100) NOT NULL,
    municipio VARCHAR(150) NOT NULL,
    setor VARCHAR(150) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

-- ===========================
-- Tabela de publicações (posts)
-- ===========================
CREATE TABLE posts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    titulo VARCHAR(200),
    conteudo TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

-- ===========================
-- Tabela de comentários
-- ===========================
CREATE TABLE comentarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    usuario_id INT NOT NULL,
    conteudo TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

-- ===========================
-- Tabela de reações (like / dislike)
-- ===========================
CREATE TABLE reacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    usuario_id INT NOT NULL,
    type CHAR(7) CONSTRAINT chk_like CHECK (type IN ('like', 'dislike')),
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (post_id, usuario_id),
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

-- =========================
-- TABELA: codigo_municipio
-- =========================
CREATE TABLE codigo_municipio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    CO_MUN_GEO CHAR(10) NOT NULL,
    NO_MUN VARCHAR(35) NOT NULL,
    UNIQUE (CO_MUN_GEO)
);

-- =========================
-- TABELA: setores
-- =========================
CREATE TABLE setores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(150)
);

-- =========================
-- TABELA: codigo_pais
-- =========================
CREATE TABLE codigo_pais (
    id INT AUTO_INCREMENT PRIMARY KEY,
    CO_PAIS CHAR(4) NOT NULL,
    NO_PAIS VARCHAR(45) NOT NULL DEFAULT '',
    UNIQUE (CO_PAIS)
);

-- =========================
-- TABELA: codigo_sh4
-- =========================
CREATE TABLE codigo_sh4 (
    id INT AUTO_INCREMENT PRIMARY KEY,
    CO_SH4 CHAR(4) NOT NULL,
    NO_SH4_POR VARCHAR(300) NOT NULL DEFAULT '',
    fk_setor INT NULL,
	CONSTRAINT fk_codigo_sh4_setor
	FOREIGN KEY (fk_setor) REFERENCES setores(id),
    UNIQUE (CO_SH4)
);

-- =========================
-- TABELA: base_importacao
-- Suporta ambos os layouts MDIC:
--   Layout NCM: CO_ANO;CO_MES;CO_NCM;CO_UNID;CO_PAIS;SG_UF_NCM;CO_VIA;CO_URF;QT_ESTAT;KG_LIQUIDO;VL_FOB
--   Layout MUN: CO_ANO;CO_MES;SH4;CO_PAIS;SG_UF_MUN;CO_MUN;KG_LIQUIDO;VL_FOB
-- =========================
CREATE TABLE base_importacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    CO_ANO SMALLINT UNSIGNED NOT NULL,
    CO_MES TINYINT UNSIGNED NOT NULL,
    CO_NCM CHAR(8) NULL,
    SH4 CHAR(4) NOT NULL,
    CO_PAIS CHAR(4) NOT NULL,
    SG_UF_MUN CHAR(2) NOT NULL,
    CO_MUN CHAR(10) NULL,
    CO_VIA CHAR(2) NULL,
    CO_URF CHAR(7) NULL,
    QT_ESTAT DECIMAL(15, 3) NULL,
    KG_LIQUIDO DECIMAL(15, 3) NOT NULL DEFAULT 0.000,
    VL_FOB DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    SETORES_ID INT,
    FOREIGN KEY (SH4) REFERENCES codigo_sh4 (CO_SH4),
    FOREIGN KEY (CO_PAIS) REFERENCES codigo_pais (CO_PAIS),
    FOREIGN KEY (SETORES_ID) REFERENCES setores (id)
);

-- =========================
-- TABELA: base_exportacao
-- Suporta ambos os layouts MDIC:
--   Layout NCM: CO_ANO;CO_MES;CO_NCM;CO_UNID;CO_PAIS;SG_UF_NCM;CO_VIA;CO_URF;QT_ESTAT;KG_LIQUIDO;VL_FOB
--   Layout MUN: CO_ANO;CO_MES;SH4;CO_PAIS;SG_UF_MUN;CO_MUN;KG_LIQUIDO;VL_FOB
-- =========================
CREATE TABLE base_exportacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    CO_ANO SMALLINT UNSIGNED NOT NULL,
    CO_MES TINYINT UNSIGNED NOT NULL,
    CO_NCM CHAR(8) NULL,
    SH4 CHAR(4) NOT NULL,
    CO_PAIS CHAR(4) NOT NULL,
    SG_UF_MUN CHAR(2) NOT NULL,
    CO_MUN CHAR(10) NULL,
    CO_VIA CHAR(2) NULL,
    CO_URF CHAR(7) NULL,
    QT_ESTAT DECIMAL(15, 3) NULL,
    KG_LIQUIDO DECIMAL(15, 3) NOT NULL DEFAULT 0.000,
    VL_FOB DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    SETORES_ID INT,
    FOREIGN KEY (SH4) REFERENCES codigo_sh4 (CO_SH4),
    FOREIGN KEY (CO_PAIS) REFERENCES codigo_pais (CO_PAIS),
    FOREIGN KEY (SETORES_ID) REFERENCES setores (id)
);

-- =========================
-- TABELA: log_java
-- Guarda o resumo de cada carga de arquivo feita pelo Java (logJava).
-- Mesmas informações do imprimirResumo(), salvas no banco com data e hora.
-- =========================
CREATE TABLE log_java (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_arquivo VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    linhas_inseridas INT NOT NULL DEFAULT 0,
    linhas_ignoradas INT NOT NULL DEFAULT 0,
    total_erros INT NOT NULL DEFAULT 0,
    data_hora DATETIME NOT NULL
);

INSERT INTO setores (nome) VALUES
('Animais vivos e produtos do reino animal'),
('Produtos do Reino Vegetal'),
('Gorduras e Oleos Animais ou Vegetais'),
('Eletricidade e Gas'),
('Produtos das Industrias Alimentares; Bebidas e Tabaco'),
('Produtos Minerais'),
('Produtos das Industrias Quimicas'),
('Plastico e Borracha'),
('Peles, Couro e Obras'),
('Madeira, Carvao Vegetal e Cortica'),
('Pasta de Madeira, Papel e Cartao'),
('Materiais Texteis e Suas Obras'),
('Calcados, Chapeus e Semelhantes'),
('Obras de Pedra, Ceramica e Vidro'),
('Perolas, Pedras Preciosas e Metais Preciosos'),
('Metais Comuns e Suas Obras'),
('Maquina e Aparelhos, Material Eletrico'),
('Material de Transporte'),
('Armas e Municoes'),
('Mercadorias e Produtos Diversos'),
('Objetos de Arte e Antiguidades');

UPDATE codigo_sh4
SET fk_setor = CASE
    WHEN LEFT(CO_SH4, 2) BETWEEN '01' AND '05' THEN 1
    WHEN LEFT(CO_SH4, 2) BETWEEN '06' AND '14' THEN 2
    WHEN LEFT(CO_SH4, 2) = '15' THEN 3
    WHEN LEFT(CO_SH4, 2) BETWEEN '16' AND '24' THEN 5
    WHEN LEFT(CO_SH4, 2) BETWEEN '25' AND '27' THEN 6
    WHEN LEFT(CO_SH4, 2) BETWEEN '28' AND '38' THEN 7
    WHEN LEFT(CO_SH4, 2) BETWEEN '39' AND '40' THEN 8
    WHEN LEFT(CO_SH4, 2) BETWEEN '41' AND '43' THEN 9
    WHEN LEFT(CO_SH4, 2) BETWEEN '44' AND '46' THEN 10
    WHEN LEFT(CO_SH4, 2) BETWEEN '47' AND '49' THEN 11
    WHEN LEFT(CO_SH4, 2) BETWEEN '50' AND '63' THEN 12
    WHEN LEFT(CO_SH4, 2) BETWEEN '64' AND '67' THEN 13
    WHEN LEFT(CO_SH4, 2) BETWEEN '68' AND '70' THEN 14
    WHEN LEFT(CO_SH4, 2) = '71' THEN 15
    WHEN LEFT(CO_SH4, 2) BETWEEN '72' AND '83' THEN 16
    WHEN LEFT(CO_SH4, 2) BETWEEN '84' AND '85' THEN 17
    WHEN LEFT(CO_SH4, 2) BETWEEN '86' AND '89' THEN 18
    WHEN LEFT(CO_SH4, 2) = '93' THEN 19
    WHEN LEFT(CO_SH4, 2) BETWEEN '94' AND '96' THEN 20
    WHEN LEFT(CO_SH4, 2) = '97' THEN 21
    ELSE fk_setor
END
WHERE id >= 1;

-- =========================
-- OTIMIZACAO DE CONSULTAS PARA DASHBOARDS (ALTO VOLUME)
-- =========================
-- Com 56M+ linhas nas tabelas base, as dashboards nao devem agregar direto
-- em base_importacao/base_exportacao a cada request.
-- Esta secao cria:
-- 1) indices para filtro/agrupamento
-- 2) tabelas agregadas por ano/municipio e ano/setor
-- 3) procedure de refresh
-- 4) views consumidas pela API

-- =========================
-- INDICES BASE IMPORTACAO
-- =========================
CREATE INDEX idx_bi_ano_mun ON base_importacao (CO_ANO, CO_MUN);
CREATE INDEX idx_bi_ano_setor ON base_importacao (CO_ANO, SETORES_ID);
CREATE INDEX idx_bi_mun_ano ON base_importacao (CO_MUN, CO_ANO);
CREATE INDEX idx_bi_setor_ano ON base_importacao (SETORES_ID, CO_ANO);

-- =========================
-- INDICES BASE EXPORTACAO
-- =========================
CREATE INDEX idx_be_ano_mun ON base_exportacao (CO_ANO, CO_MUN);
CREATE INDEX idx_be_ano_setor ON base_exportacao (CO_ANO, SETORES_ID);
CREATE INDEX idx_be_mun_ano ON base_exportacao (CO_MUN, CO_ANO);
CREATE INDEX idx_be_setor_ano ON base_exportacao (SETORES_ID, CO_ANO);

-- =========================
-- TABELAS AGREGADAS (MATERIALIZADAS)
-- =========================
CREATE TABLE dashboard_aggr_municipio_ano (
    ano SMALLINT UNSIGNED NOT NULL,
    co_mun CHAR(10) NOT NULL,
    municipio VARCHAR(35) NOT NULL,
    importacoes_total DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    exportacoes_total DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (ano, co_mun),
    KEY idx_dam_municipio_ano (municipio, ano)
);

CREATE TABLE dashboard_aggr_setor_ano (
    ano SMALLINT UNSIGNED NOT NULL,
    id INT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    importacoes_total DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    exportacoes_total DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (ano, id),
    KEY idx_das_nome_ano (nome, ano)
);

-- =========================
-- PROCEDURE DE REFRESH DAS AGREGACOES
-- =========================
DROP PROCEDURE IF EXISTS sp_refresh_dashboard_aggregates;

DELIMITER $$
CREATE PROCEDURE sp_refresh_dashboard_aggregates()
BEGIN
    TRUNCATE TABLE dashboard_aggr_municipio_ano;
    TRUNCATE TABLE dashboard_aggr_setor_ano;

    INSERT INTO dashboard_aggr_municipio_ano (ano, co_mun, municipio, importacoes_total, exportacoes_total)
    SELECT
        z.ano,
        z.co_mun,
        MAX(z.municipio) AS municipio,
        SUM(z.importacoes_total) AS importacoes_total,
        SUM(z.exportacoes_total) AS exportacoes_total
    FROM (
        SELECT
            bi.CO_ANO AS ano,
            bi.CO_MUN AS co_mun,
            COALESCE(cm.NO_MUN, bi.CO_MUN) AS municipio,
            SUM(bi.VL_FOB) AS importacoes_total,
            0 AS exportacoes_total
        FROM base_importacao bi
        LEFT JOIN codigo_municipio cm
            ON cm.CO_MUN_GEO = bi.CO_MUN
        GROUP BY bi.CO_ANO, bi.CO_MUN, cm.NO_MUN

        UNION ALL

        SELECT
            be.CO_ANO AS ano,
            be.CO_MUN AS co_mun,
            COALESCE(cm.NO_MUN, be.CO_MUN) AS municipio,
            0 AS importacoes_total,
            SUM(be.VL_FOB) AS exportacoes_total
        FROM base_exportacao be
        LEFT JOIN codigo_municipio cm
            ON cm.CO_MUN_GEO = be.CO_MUN
        GROUP BY be.CO_ANO, be.CO_MUN, cm.NO_MUN
    ) z
    GROUP BY z.ano, z.co_mun;

    INSERT INTO dashboard_aggr_setor_ano (ano, id, nome, importacoes_total, exportacoes_total)
    SELECT
        z.ano,
        z.id,
        MAX(z.nome) AS nome,
        SUM(z.importacoes_total) AS importacoes_total,
        SUM(z.exportacoes_total) AS exportacoes_total
    FROM (
        SELECT
            bi.CO_ANO AS ano,
            s.id,
            s.nome,
            SUM(bi.VL_FOB) AS importacoes_total,
            0 AS exportacoes_total
        FROM base_importacao bi
        INNER JOIN setores s
            ON s.id = bi.SETORES_ID
        GROUP BY bi.CO_ANO, s.id, s.nome

        UNION ALL

        SELECT
            be.CO_ANO AS ano,
            s.id,
            s.nome,
            0 AS importacoes_total,
            SUM(be.VL_FOB) AS exportacoes_total
        FROM base_exportacao be
        INNER JOIN setores s
            ON s.id = be.SETORES_ID
        GROUP BY be.CO_ANO, s.id, s.nome
    ) z
    GROUP BY z.ano, z.id;
END $$
DELIMITER ;

-- Executar refresh inicial apos carga de dados.
CALL sp_refresh_dashboard_aggregates();

-- =========================
-- VIEWS PARA AS DASHBOARDS
-- =========================
DROP VIEW IF EXISTS vw_situacao_mercado;
CREATE VIEW vw_situacao_mercado AS
SELECT
    ano,
    ROUND(SUM(importacoes_total) / 1000000, 2) AS importacoes_milhoes_usd,
    ROUND(SUM(exportacoes_total) / 1000000, 2) AS exportacoes_milhoes_usd
FROM dashboard_aggr_setor_ano
GROUP BY ano;

DROP VIEW IF EXISTS vw_valor_total_por_setor;
CREATE VIEW vw_valor_total_por_setor AS
SELECT
    ano,
    id,
    nome,
    exportacoes_total AS exportacoes,
    importacoes_total AS importacoes
FROM dashboard_aggr_setor_ano;

DROP VIEW IF EXISTS vw_exportacoes_por_setor;
CREATE VIEW vw_exportacoes_por_setor AS
SELECT
    ano,
    id,
    nome,
    exportacoes_total AS valor_total
FROM dashboard_aggr_setor_ano;

DROP VIEW IF EXISTS vw_importacoes_por_setor;
CREATE VIEW vw_importacoes_por_setor AS
SELECT
    ano,
    id,
    nome,
    importacoes_total AS valor_total
FROM dashboard_aggr_setor_ano;

DROP VIEW IF EXISTS vw_situacao_anual_municipios;
CREATE VIEW vw_situacao_anual_municipios AS
SELECT
    ano,
    municipio,
    ROUND(importacoes_total / 1000000, 2) AS importacoes_milhoes_usd,
    ROUND(exportacoes_total / 1000000, 2) AS exportacoes_milhoes_usd
FROM dashboard_aggr_municipio_ano;

DROP VIEW IF EXISTS vw_ranking_municipios;
CREATE VIEW vw_ranking_municipios AS
SELECT
    ano,
    municipio,
    (importacoes_total + exportacoes_total) AS valor_total
FROM dashboard_aggr_municipio_ano;

DROP VIEW IF EXISTS vw_importacoes_por_municipio;
CREATE VIEW vw_importacoes_por_municipio AS
SELECT
    ano,
    municipio,
    importacoes_total AS valor_total
FROM dashboard_aggr_municipio_ano;

DROP VIEW IF EXISTS vw_exportacoes_por_municipio;
CREATE VIEW vw_exportacoes_por_municipio AS
SELECT
    ano,
    municipio,
    exportacoes_total AS valor_total
FROM dashboard_aggr_municipio_ano;