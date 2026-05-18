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
-- TABELA: codigo_sh4
-- =========================
CREATE TABLE codigo_sh4 (
    id INT AUTO_INCREMENT PRIMARY KEY,
    CO_SH4 CHAR(4) NOT NULL,
    NO_SH4_POR VARCHAR(80) NOT NULL DEFAULT '',
    UNIQUE (CO_SH4)
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
-- TABELA: setores
-- =========================
CREATE TABLE setores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(150)
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
('Gorduras e Óleos Animais ou Vegetais'),
('Eletricidade e Gás'),
('Produtos das Indústrias Alimentares; Bebidas e Tabaco'),
('Produtos Minerais'),
('Produtos das Indústrias Químicas'),
('Plástico e Borracha'),
('Peles, Couro e Obras'),
('Madeira, Carvão Vegetal e Cortiça'),
('Pasta de Madeira, Papel e Cartão'),
('Materiais Têxteis e Suas Obras'),
('Calçados, Chapéus e Semelhantes'),
('Obras de Pedra, Cerâmica e Vidro'),
('Pérolas, Pedras Preciosas e Metais Preciosos'),
('Metais Comuns e Suas Obras'),
('Máquina e Aparelhos, Material Elétrico'),
('Material de Transporte'),
('Armas e Munições'),
('Mercadorias e Produtos Diversos'),
('Objetos de Arte e Antiguidades');