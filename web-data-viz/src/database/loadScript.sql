USE simecom;

-- =========================================================
-- USUARIOS (10)
-- =========================================================
INSERT INTO usuarios (nome, sobrenome, email, senha, status) VALUES
('Ana', 'Silva', 'ana.silva@simecom.com', '123456', 'ativo'),
('Bruno', 'Oliveira', 'bruno.oliveira@simecom.com', '123456', 'ativo'),
('Carla', 'Souza', 'carla.souza@simecom.com', '123456', 'ativo'),
('Diego', 'Lima', 'diego.lima@simecom.com', '123456', 'ativo'),
('Eduarda', 'Pereira', 'eduarda.pereira@simecom.com', '123456', 'ativo'),
('Felipe', 'Costa', 'felipe.costa@simecom.com', '123456', 'ativo'),
('Gabriela', 'Almeida', 'gabriela.almeida@simecom.com', '123456', 'ativo'),
('Henrique', 'Ferreira', 'henrique.ferreira@simecom.com', '123456', 'ativo'),
('Isabela', 'Rodrigues', 'isabela.rodrigues@simecom.com', '123456', 'ativo'),
('Joao', 'Martins', 'joao.martins@simecom.com', '123456', 'ativo');

-- =========================================================
-- EMPRESAS (10)
-- 1 empresa para cada usuario
-- =========================================================
INSERT INTO empresas (usuario_id, nome, cnpj, email, telefone, uf, status) VALUES
(1, 'Silva Comercio Exterior', '12345678000101', 'contato@silvacomex.com', '11999990001', 'SP', 'ativo'),
(2, 'Oliveira Importadora', '12345678000102', 'contato@oliveiraimp.com', '21999990002', 'RJ', 'ativo'),
(3, 'Souza Exportacoes', '12345678000103', 'contato@souzaexp.com', '31999990003', 'MG', 'ativo'),
(4, 'Lima Trading', '12345678000104', 'contato@limatrading.com', '41999990004', 'PR', 'ativo'),
(5, 'Pereira Global', '12345678000105', 'contato@pereiraglobal.com', '51999990005', 'RS', 'ativo'),
(6, 'Costa Mercantil', '12345678000106', 'contato@costamercantil.com', '11999990006', 'SP', 'ativo'),
(7, 'Almeida Negocios', '12345678000107', 'contato@almeidanegocios.com', '27999990007', 'ES', 'ativo'),
(8, 'Ferreira Logistica', '12345678000108', 'contato@ferreiralog.com', '61999990008', 'DF', 'ativo'),
(9, 'Rodrigues Trade', '12345678000109', 'contato@rodriguestrade.com', '71999990009', 'BA', 'ativo'),
(10, 'Martins Comercial', '12345678000110', 'contato@martinscomercial.com', '81999990010', 'PE', 'ativo');

-- =========================================================
-- PREFERENCIAS (20)
-- 2 preferencias para cada usuario
-- =========================================================
INSERT INTO preferencias (usuario_id, estado, municipio, setor) VALUES
(1, 'Sao Paulo', 'Sao Paulo', 'Tecnologia'),
(1, 'Sao Paulo', 'Campinas', 'Agronegocio'),
(2, 'Rio de Janeiro', 'Rio de Janeiro', 'Energia'),
(2, 'Rio de Janeiro', 'Niteroi', 'Petroleo e Gas'),
(3, 'Minas Gerais', 'Belo Horizonte', 'Mineracao'),
(3, 'Minas Gerais', 'Uberlandia', 'Alimentos'),
(4, 'Parana', 'Curitiba', 'Logistica'),
(4, 'Parana', 'Londrina', 'Maquinas'),
(5, 'Rio Grande do Sul', 'Porto Alegre', 'Calcados'),
(5, 'Rio Grande do Sul', 'Caxias do Sul', 'Metalurgia'),
(6, 'Sao Paulo', 'Santos', 'Comercio Exterior'),
(6, 'Sao Paulo', 'Ribeirao Preto', 'Agronegocio'),
(7, 'Espirito Santo', 'Vitoria', 'Rochas Ornamentais'),
(7, 'Espirito Santo', 'Serra', 'Siderurgia'),
(8, 'Distrito Federal', 'Brasilia', 'Servicos'),
(8, 'Goias', 'Anapolis', 'Farmaceutico'),
(9, 'Bahia', 'Salvador', 'Quimico'),
(9, 'Bahia', 'Feira de Santana', 'Textil'),
(10, 'Pernambuco', 'Recife', 'Tecnologia'),
(10, 'Pernambuco', 'Suape', 'Logistica Portuaria');

-- =========================================================
-- POSTS (10)
-- 1 por usuario
-- =========================================================
INSERT INTO posts (usuario_id, titulo, conteudo) VALUES
(1, 'Panorama das importacoes em Sao Paulo', 'Analise inicial sobre o crescimento das importacoes no municipio de Sao Paulo.'),
(2, 'Oportunidades de exportacao para o Rio', 'Levantamento de oportunidades para empresas exportadoras do Rio de Janeiro.'),
(3, 'Mineracao e mercado externo', 'Resumo sobre o impacto da mineracao mineira no comercio exterior.'),
(4, 'Logistica no Sul do Brasil', 'Reflexoes sobre infraestrutura e distribuicao na regiao Sul.'),
(5, 'Setor calcadista em foco', 'Breve analise sobre a relevancia do setor calcadista para exportacao.'),
(6, 'Santos como hub comercial', 'Importancia do porto de Santos para a entrada de mercadorias.'),
(7, 'Mercado de rochas ornamentais', 'Perspectivas comerciais para rochas ornamentais no exterior.'),
(8, 'Centro-oeste e distribuicao', 'Estudo sobre distribuicao e integracao logistica no Centro-Oeste.'),
(9, 'Industria quimica e exportacao', 'Observacoes sobre produtos quimicos e competitividade internacional.'),
(10, 'Tecnologia e comercio exterior', 'Como a tecnologia pode apoiar decisoes baseadas em dados no comercio exterior.');

-- =========================================================
-- COMENTARIOS (100)
-- 1 comentario de cada usuario em cada post
-- =========================================================
INSERT INTO comentarios (post_id, usuario_id, conteudo) VALUES
(1, 1, 'Comentario do usuario 1 na publicacao 1.'),
(1, 2, 'Comentario do usuario 2 na publicacao 1.'),
(1, 3, 'Comentario do usuario 3 na publicacao 1.'),
(1, 4, 'Comentario do usuario 4 na publicacao 1.'),
(1, 5, 'Comentario do usuario 5 na publicacao 1.'),
(1, 6, 'Comentario do usuario 6 na publicacao 1.'),
(1, 7, 'Comentario do usuario 7 na publicacao 1.'),
(1, 8, 'Comentario do usuario 8 na publicacao 1.'),
(1, 9, 'Comentario do usuario 9 na publicacao 1.'),
(1, 10, 'Comentario do usuario 10 na publicacao 1.'),

(2, 1, 'Comentario do usuario 1 na publicacao 2.'),
(2, 2, 'Comentario do usuario 2 na publicacao 2.'),
(2, 3, 'Comentario do usuario 3 na publicacao 2.'),
(2, 4, 'Comentario do usuario 4 na publicacao 2.'),
(2, 5, 'Comentario do usuario 5 na publicacao 2.'),
(2, 6, 'Comentario do usuario 6 na publicacao 2.'),
(2, 7, 'Comentario do usuario 7 na publicacao 2.'),
(2, 8, 'Comentario do usuario 8 na publicacao 2.'),
(2, 9, 'Comentario do usuario 9 na publicacao 2.'),
(2, 10, 'Comentario do usuario 10 na publicacao 2.'),

(3, 1, 'Comentario do usuario 1 na publicacao 3.'),
(3, 2, 'Comentario do usuario 2 na publicacao 3.'),
(3, 3, 'Comentario do usuario 3 na publicacao 3.'),
(3, 4, 'Comentario do usuario 4 na publicacao 3.'),
(3, 5, 'Comentario do usuario 5 na publicacao 3.'),
(3, 6, 'Comentario do usuario 6 na publicacao 3.'),
(3, 7, 'Comentario do usuario 7 na publicacao 3.'),
(3, 8, 'Comentario do usuario 8 na publicacao 3.'),
(3, 9, 'Comentario do usuario 9 na publicacao 3.'),
(3, 10, 'Comentario do usuario 10 na publicacao 3.'),

(4, 1, 'Comentario do usuario 1 na publicacao 4.'),
(4, 2, 'Comentario do usuario 2 na publicacao 4.'),
(4, 3, 'Comentario do usuario 3 na publicacao 4.'),
(4, 4, 'Comentario do usuario 4 na publicacao 4.'),
(4, 5, 'Comentario do usuario 5 na publicacao 4.'),
(4, 6, 'Comentario do usuario 6 na publicacao 4.'),
(4, 7, 'Comentario do usuario 7 na publicacao 4.'),
(4, 8, 'Comentario do usuario 8 na publicacao 4.'),
(4, 9, 'Comentario do usuario 9 na publicacao 4.'),
(4, 10, 'Comentario do usuario 10 na publicacao 4.'),

(5, 1, 'Comentario do usuario 1 na publicacao 5.'),
(5, 2, 'Comentario do usuario 2 na publicacao 5.'),
(5, 3, 'Comentario do usuario 3 na publicacao 5.'),
(5, 4, 'Comentario do usuario 4 na publicacao 5.'),
(5, 5, 'Comentario do usuario 5 na publicacao 5.'),
(5, 6, 'Comentario do usuario 6 na publicacao 5.'),
(5, 7, 'Comentario do usuario 7 na publicacao 5.'),
(5, 8, 'Comentario do usuario 8 na publicacao 5.'),
(5, 9, 'Comentario do usuario 9 na publicacao 5.'),
(5, 10, 'Comentario do usuario 10 na publicacao 5.'),

(6, 1, 'Comentario do usuario 1 na publicacao 6.'),
(6, 2, 'Comentario do usuario 2 na publicacao 6.'),
(6, 3, 'Comentario do usuario 3 na publicacao 6.'),
(6, 4, 'Comentario do usuario 4 na publicacao 6.'),
(6, 5, 'Comentario do usuario 5 na publicacao 6.'),
(6, 6, 'Comentario do usuario 6 na publicacao 6.'),
(6, 7, 'Comentario do usuario 7 na publicacao 6.'),
(6, 8, 'Comentario do usuario 8 na publicacao 6.'),
(6, 9, 'Comentario do usuario 9 na publicacao 6.'),
(6, 10, 'Comentario do usuario 10 na publicacao 6.'),

(7, 1, 'Comentario do usuario 1 na publicacao 7.'),
(7, 2, 'Comentario do usuario 2 na publicacao 7.'),
(7, 3, 'Comentario do usuario 3 na publicacao 7.'),
(7, 4, 'Comentario do usuario 4 na publicacao 7.'),
(7, 5, 'Comentario do usuario 5 na publicacao 7.'),
(7, 6, 'Comentario do usuario 6 na publicacao 7.'),
(7, 7, 'Comentario do usuario 7 na publicacao 7.'),
(7, 8, 'Comentario do usuario 8 na publicacao 7.'),
(7, 9, 'Comentario do usuario 9 na publicacao 7.'),
(7, 10, 'Comentario do usuario 10 na publicacao 7.'),

(8, 1, 'Comentario do usuario 1 na publicacao 8.'),
(8, 2, 'Comentario do usuario 2 na publicacao 8.'),
(8, 3, 'Comentario do usuario 3 na publicacao 8.'),
(8, 4, 'Comentario do usuario 4 na publicacao 8.'),
(8, 5, 'Comentario do usuario 5 na publicacao 8.'),
(8, 6, 'Comentario do usuario 6 na publicacao 8.'),
(8, 7, 'Comentario do usuario 7 na publicacao 8.'),
(8, 8, 'Comentario do usuario 8 na publicacao 8.'),
(8, 9, 'Comentario do usuario 9 na publicacao 8.'),
(8, 10, 'Comentario do usuario 10 na publicacao 8.'),

(9, 1, 'Comentario do usuario 1 na publicacao 9.'),
(9, 2, 'Comentario do usuario 2 na publicacao 9.'),
(9, 3, 'Comentario do usuario 3 na publicacao 9.'),
(9, 4, 'Comentario do usuario 4 na publicacao 9.'),
(9, 5, 'Comentario do usuario 5 na publicacao 9.'),
(9, 6, 'Comentario do usuario 6 na publicacao 9.'),
(9, 7, 'Comentario do usuario 7 na publicacao 9.'),
(9, 8, 'Comentario do usuario 8 na publicacao 9.'),
(9, 9, 'Comentario do usuario 9 na publicacao 9.'),
(9, 10, 'Comentario do usuario 10 na publicacao 9.'),

(10, 1, 'Comentario do usuario 1 na publicacao 10.'),
(10, 2, 'Comentario do usuario 2 na publicacao 10.'),
(10, 3, 'Comentario do usuario 3 na publicacao 10.'),
(10, 4, 'Comentario do usuario 4 na publicacao 10.'),
(10, 5, 'Comentario do usuario 5 na publicacao 10.'),
(10, 6, 'Comentario do usuario 6 na publicacao 10.'),
(10, 7, 'Comentario do usuario 7 na publicacao 10.'),
(10, 8, 'Comentario do usuario 8 na publicacao 10.'),
(10, 9, 'Comentario do usuario 9 na publicacao 10.'),
(10, 10, 'Comentario do usuario 10 na publicacao 10.');

-- =========================================================
-- REACOES
-- Cada post com pelo menos 4 reacoes
-- =========================================================
INSERT INTO reacoes (post_id, usuario_id, tipo) VALUES
(1, 2, 'like'),
(1, 4, 'like'),
(1, 6, 'dislike'),
(1, 8, 'like'),

(2, 1, 'like'),
(2, 3, 'like'),
(2, 5, 'dislike'),
(2, 7, 'like'),

(3, 2, 'like'),
(3, 4, 'dislike'),
(3, 8, 'like'),
(3, 10, 'like'),

(4, 1, 'like'),
(4, 5, 'like'),
(4, 7, 'dislike'),
(4, 9, 'like'),

(5, 2, 'like'),
(5, 3, 'like'),
(5, 6, 'like'),
(5, 10, 'dislike'),

(6, 1, 'like'),
(6, 4, 'like'),
(6, 7, 'like'),
(6, 9, 'dislike'),

(7, 2, 'dislike'),
(7, 5, 'like'),
(7, 8, 'like'),
(7, 10, 'like'),

(8, 1, 'like'),
(8, 3, 'dislike'),
(8, 6, 'like'),
(8, 9, 'like'),

(9, 2, 'like'),
(9, 4, 'like'),
(9, 7, 'dislike'),
(9, 10, 'like'),

(10, 1, 'like'),
(10, 5, 'like'),
(10, 6, 'dislike'),
(10, 8, 'like');

-- =========================================================
-- CODIGO_PAIS (5 paises)
-- =========================================================
INSERT INTO codigo_pais (CO_PAIS, NO_PAIS) VALUES
('0105', 'Argentina'),
('0249', 'Estados Unidos'),
('0275', 'Brasil'),
('0589', 'China'),
('0634', 'Alemanha');

-- =========================================================
-- CODIGO_MUNICIPIO (5 municipios)
-- =========================================================
INSERT INTO codigo_municipio (CO_MUN_GEO, NO_MUN) VALUES
('3550308000', 'Sao Paulo'),
('3304557000', 'Rio de Janeiro'),
('3106200000', 'Belo Horizonte'),
('4106902000', 'Curitiba'),
('4314902000', 'Porto Alegre');

-- =========================================================
-- CODIGO_SH4 (5 codigos)
-- =========================================================
INSERT INTO codigo_sh4 (CO_SH4, NO_SH4_POR) VALUES
('1001', 'Trigo e mistura de trigo com centeio'),
('1201', 'Graos de soja, mesmo triturados'),
('1701', 'Acucar de cana ou de beterraba'),
('2203', 'Cervejas de malte'),
('8703', 'Automoveis de passageiros');

-- =========================================================
-- BASE_IMPORTACAO (50)
-- 5 paises diferentes
-- =========================================================
INSERT INTO base_importacao
(CO_ANO, CO_MES, SH4, CO_PAIS, CO_MUN, SG_UF_MUN, KG_LIQUIDO, VL_FOB) VALUES
(2021, 1, '1001', '0105', '3550308000', 'SP', 1200.500, 15000.00),
(2021, 2, '1201', '0249', '3304557000', 'RJ', 980.250, 22000.00),
(2021, 3, '1701', '0275', '3106200000', 'MG', 1500.000, 18000.00),
(2021, 4, '2203', '0589', '4106902000', 'PR', 760.400, 19500.00),
(2021, 5, '8703', '0634', '4314902000', 'RS', 3000.000, 120000.00),

(2021, 6, '1001', '0105', '3304557000', 'RJ', 1150.000, 14800.00),
(2021, 7, '1201', '0249', '3106200000', 'MG', 1020.700, 22600.00),
(2021, 8, '1701', '0275', '4106902000', 'PR', 1430.200, 17650.00),
(2021, 9, '2203', '0589', '4314902000', 'RS', 810.900, 20500.00),
(2021, 10, '8703', '0634', '3550308000', 'SP', 3200.000, 128500.00),

(2022, 1, '1001', '0105', '3106200000', 'MG', 1195.300, 15120.00),
(2022, 2, '1201', '0249', '4106902000', 'PR', 1105.000, 23200.00),
(2022, 3, '1701', '0275', '4314902000', 'RS', 1498.100, 18150.00),
(2022, 4, '2203', '0589', '3550308000', 'SP', 845.600, 20980.00),
(2022, 5, '8703', '0634', '3304557000', 'RJ', 3100.300, 123400.00),

(2022, 6, '1001', '0105', '4106902000', 'PR', 1250.000, 15440.00),
(2022, 7, '1201', '0249', '4314902000', 'RS', 1111.100, 23410.00),
(2022, 8, '1701', '0275', '3550308000', 'SP', 1525.000, 18300.00),
(2022, 9, '2203', '0589', '3304557000', 'RJ', 790.000, 20110.00),
(2022, 10, '8703', '0634', '3106200000', 'MG', 3300.000, 129900.00),

(2023, 1, '1001', '0105', '4314902000', 'RS', 1210.900, 15250.00),
(2023, 2, '1201', '0249', '3550308000', 'SP', 1099.500, 23670.00),
(2023, 3, '1701', '0275', '3304557000', 'RJ', 1410.400, 17990.00),
(2023, 4, '2203', '0589', '3106200000', 'MG', 830.200, 20750.00),
(2023, 5, '8703', '0634', '4106902000', 'PR', 3400.000, 131500.00),

(2023, 6, '1001', '0105', '3550308000', 'SP', 1235.000, 15380.00),
(2023, 7, '1201', '0249', '3304557000', 'RJ', 1125.600, 23890.00),
(2023, 8, '1701', '0275', '3106200000', 'MG', 1460.000, 18240.00),
(2023, 9, '2203', '0589', '4106902000', 'PR', 855.900, 21020.00),
(2023, 10, '8703', '0634', '4314902000', 'RS', 3500.000, 133800.00),

(2024, 1, '1001', '0105', '3304557000', 'RJ', 1270.000, 15600.00),
(2024, 2, '1201', '0249', '3106200000', 'MG', 1150.000, 24000.00),
(2024, 3, '1701', '0275', '4106902000', 'PR', 1480.800, 18450.00),
(2024, 4, '2203', '0589', '4314902000', 'RS', 870.000, 21210.00),
(2024, 5, '8703', '0634', '3550308000', 'SP', 3600.000, 136000.00),

(2024, 6, '1001', '0105', '3106200000', 'MG', 1295.700, 15880.00),
(2024, 7, '1201', '0249', '4106902000', 'PR', 1185.200, 24350.00),
(2024, 8, '1701', '0275', '4314902000', 'RS', 1502.500, 18620.00),
(2024, 9, '2203', '0589', '3550308000', 'SP', 890.400, 21450.00),
(2024, 10, '8703', '0634', '3304557000', 'RJ', 3700.000, 138200.00),

(2025, 1, '1001', '0105', '4106902000', 'PR', 1310.000, 16050.00),
(2025, 2, '1201', '0249', '4314902000', 'RS', 1198.900, 24600.00),
(2025, 3, '1701', '0275', '3550308000', 'SP', 1530.000, 18800.00),
(2025, 4, '2203', '0589', '3304557000', 'RJ', 905.600, 21680.00),
(2025, 5, '8703', '0634', '3106200000', 'MG', 3800.000, 140700.00),

(2025, 6, '1001', '0105', '4314902000', 'RS', 1330.800, 16220.00),
(2025, 7, '1201', '0249', '3550308000', 'SP', 1215.000, 24840.00),
(2025, 8, '1701', '0275', '3304557000', 'RJ', 1558.400, 19010.00),
(2025, 9, '2203', '0589', '3106200000', 'MG', 920.000, 21900.00),
(2025, 10, '8703', '0634', '4106902000', 'PR', 3900.000, 143100.00);

-- =========================================================
-- BASE_EXPORTACAO (50)
-- 5 paises diferentes
-- =========================================================
INSERT INTO base_exportacao
(CO_ANO, CO_MES, SH4, CO_PAIS, CO_MUN, SG_UF_MUN, KG_LIQUIDO, VL_FOB) VALUES
(2021, 1, '1001', '0249', '3550308000', 'SP', 2100.500, 28000.00),
(2021, 2, '1201', '0589', '3304557000', 'RJ', 3200.000, 41000.00),
(2021, 3, '1701', '0634', '3106200000', 'MG', 1750.800, 26500.00),
(2021, 4, '2203', '0105', '4106902000', 'PR', 980.200, 22300.00),
(2021, 5, '8703', '0275', '4314902000', 'RS', 2500.000, 119500.00),

(2021, 6, '1001', '0249', '3304557000', 'RJ', 2185.000, 28450.00),
(2021, 7, '1201', '0589', '3106200000', 'MG', 3305.200, 41780.00),
(2021, 8, '1701', '0634', '4106902000', 'PR', 1820.000, 27120.00),
(2021, 9, '2203', '0105', '4314902000', 'RS', 1025.400, 22980.00),
(2021, 10, '8703', '0275', '3550308000', 'SP', 2600.000, 121900.00),

(2022, 1, '1001', '0249', '3106200000', 'MG', 2250.000, 28990.00),
(2022, 2, '1201', '0589', '4106902000', 'PR', 3380.000, 42100.00),
(2022, 3, '1701', '0634', '4314902000', 'RS', 1885.500, 27500.00),
(2022, 4, '2203', '0105', '3550308000', 'SP', 1040.300, 23120.00),
(2022, 5, '8703', '0275', '3304557000', 'RJ', 2700.000, 124700.00),

(2022, 6, '1001', '0249', '4106902000', 'PR', 2295.700, 29320.00),
(2022, 7, '1201', '0589', '4314902000', 'RS', 3425.100, 42680.00),
(2022, 8, '1701', '0634', '3550308000', 'SP', 1910.000, 27950.00),
(2022, 9, '2203', '0105', '3304557000', 'RJ', 1088.600, 23540.00),
(2022, 10, '8703', '0275', '3106200000', 'MG', 2800.000, 126900.00),

(2023, 1, '1001', '0249', '4314902000', 'RS', 2340.000, 29810.00),
(2023, 2, '1201', '0589', '3550308000', 'SP', 3480.400, 43120.00),
(2023, 3, '1701', '0634', '3304557000', 'RJ', 1950.300, 28260.00),
(2023, 4, '2203', '0105', '3106200000', 'MG', 1110.900, 23870.00),
(2023, 5, '8703', '0275', '4106902000', 'PR', 2900.000, 129500.00),

(2023, 6, '1001', '0249', '3550308000', 'SP', 2405.100, 30250.00),
(2023, 7, '1201', '0589', '3304557000', 'RJ', 3520.000, 43780.00),
(2023, 8, '1701', '0634', '3106200000', 'MG', 1988.800, 28690.00),
(2023, 9, '2203', '0105', '4106902000', 'PR', 1135.000, 24120.00),
(2023, 10, '8703', '0275', '4314902000', 'RS', 3000.000, 131800.00),

(2024, 1, '1001', '0249', '3304557000', 'RJ', 2450.000, 30700.00),
(2024, 2, '1201', '0589', '3106200000', 'MG', 3580.000, 44200.00),
(2024, 3, '1701', '0634', '4106902000', 'PR', 2025.400, 29020.00),
(2024, 4, '2203', '0105', '4314902000', 'RS', 1160.300, 24480.00),
(2024, 5, '8703', '0275', '3550308000', 'SP', 3100.000, 134400.00),

(2024, 6, '1001', '0249', '3106200000', 'MG', 2510.200, 31150.00),
(2024, 7, '1201', '0589', '4106902000', 'PR', 3622.600, 44680.00),
(2024, 8, '1701', '0634', '4314902000', 'RS', 2075.000, 29450.00),
(2024, 9, '2203', '0105', '3550308000', 'SP', 1188.500, 24890.00),
(2024, 10, '8703', '0275', '3304557000', 'RJ', 3200.000, 136900.00),

(2025, 1, '1001', '0249', '4106902000', 'PR', 2560.000, 31640.00),
(2025, 2, '1201', '0589', '4314902000', 'RS', 3680.400, 45220.00),
(2025, 3, '1701', '0634', '3550308000', 'SP', 2120.300, 29980.00),
(2025, 4, '2203', '0105', '3304557000', 'RJ', 1210.200, 25210.00),
(2025, 5, '8703', '0275', '3106200000', 'MG', 3300.000, 139700.00),

(2025, 6, '1001', '0249', '4314902000', 'RS', 2625.500, 32080.00),
(2025, 7, '1201', '0589', '3550308000', 'SP', 3725.000, 45890.00),
(2025, 8, '1701', '0634', '3304557000', 'RJ', 2168.700, 30340.00),
(2025, 9, '2203', '0105', '3106200000', 'MG', 1235.600, 25560.00),
(2025, 10, '8703', '0275', '4106902000', 'PR', 3400.000, 142300.00);