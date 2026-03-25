-- ===========================
-- DATABASE: CREATE DATABASE
-- ===========================
DROP DATABASE simecom;
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
    status ENUM('ativo','inativo') DEFAULT 'ativo',
    type ENUM('adm', 'user') DEFAULT 'user' NOT NULL,

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
    status ENUM('ativo','inativo') DEFAULT 'ativo',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
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

    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
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

    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
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


    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ===========================
-- Tabela de reações (like / dislike)
-- ===========================
CREATE TABLE reacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    usuario_id INT NOT NULL,
    tipo ENUM('like', 'dislike') NOT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (post_id, usuario_id),

    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
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
    NO_SH4_POR VARCHAR(80) NOT NULL,
    UNIQUE (CO_SH4)
);

-- =========================
-- TABELA: codigo_pais
-- =========================
CREATE TABLE codigo_pais (
    id INT AUTO_INCREMENT PRIMARY KEY,
    CO_PAIS CHAR(4) NOT NULL,
    NO_PAIS VARCHAR(45) NOT NULL,
    UNIQUE (CO_PAIS)
);

-- =========================
-- TABELA: base_importacao
-- =========================
CREATE TABLE base_importacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    CO_ANO SMALLINT UNSIGNED NOT NULL,
    CO_MES TINYINT UNSIGNED NOT NULL,
    SH4 CHAR(4) NOT NULL,
    CO_PAIS CHAR(4) NOT NULL,
    CO_MUN CHAR(10) NOT NULL,
    SG_UF_MUN CHAR(2) NOT NULL,
    KG_LIQUIDO DECIMAL(15,3) NOT NULL DEFAULT 0.000,
    VL_FOB DECIMAL(15,2) NOT NULL DEFAULT 0.00,

    FOREIGN KEY (SH4) REFERENCES codigo_sh4(CO_SH4),
    FOREIGN KEY (CO_PAIS) REFERENCES codigo_pais(CO_PAIS),
    FOREIGN KEY (CO_MUN) REFERENCES codigo_municipio(CO_MUN_GEO)
);

-- =========================
-- TABELA: base_exportacao
-- =========================
CREATE TABLE base_exportacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    CO_ANO SMALLINT UNSIGNED NOT NULL,
    CO_MES TINYINT UNSIGNED NOT NULL,
    SH4 CHAR(4) NOT NULL,
    CO_PAIS CHAR(4) NOT NULL,
    CO_MUN CHAR(10) NOT NULL,
    SG_UF_MUN CHAR(2) NOT NULL,
    KG_LIQUIDO DECIMAL(15,3) NOT NULL DEFAULT 0.000,
    VL_FOB DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    
    FOREIGN KEY (SH4) REFERENCES codigo_sh4(CO_SH4),
    FOREIGN KEY (CO_PAIS) REFERENCES codigo_pais(CO_PAIS),
    FOREIGN KEY (CO_MUN) REFERENCES codigo_municipio(CO_MUN_GEO)
);