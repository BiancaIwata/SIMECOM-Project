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
    
    FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ===========================
-- TABELA: importacoes (independente)
-- ===========================
CREATE TABLE importacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ano YEAR,
    mes TINYINT CHECK (mes BETWEEN 1 AND 12),
    pais VARCHAR(100),
    uf CHAR(2),
    municipio VARCHAR(150)
);

-- ===========================
-- TABELA: dados_importacao (produtos e valores)
-- ===========================
CREATE TABLE dados_importacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    importacao_id INT NOT NULL,
    produto VARCHAR(150),
    peso_kg DECIMAL(10,2),
    valor_usd DECIMAL(15,2),
    preco DECIMAL(15,2),
    desconto DECIMAL(10,2),
    frete DECIMAL(10,2),

    FOREIGN KEY (importacao_id)
        REFERENCES importacoes(id)
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

-- ===========================
-- TABELA: tributos
-- ===========================
CREATE TABLE tributos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    importacao_id INT NOT NULL,
    icms DECIMAL(10,2),
    beneficio DECIMAL(10,2),
    ncm VARCHAR(10),
    mercado VARCHAR(100),
    ano YEAR,

    FOREIGN KEY (importacao_id)
        REFERENCES importacoes(id)
);
