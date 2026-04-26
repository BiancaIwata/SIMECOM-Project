var database = require("../database/config");

function forumGetter() {
  var instrucaoSql = `
    SELECT 
    p.id,
    p.titulo,
    p.conteudo,
    p.created_at,
    u.nome AS autor
    FROM posts p
    JOIN usuarios u ON p.usuario_id = u.id
    ORDER BY p.created_at DESC
    LIMIT 15;
  `;

  return database.executar(instrucaoSql);
}

function forumTop5() {
  var instrucaoSql = `
SELECT 
  p.id,
  p.titulo,
  p.conteudo,
  p.created_at,
  u.nome AS autor,
  COUNT(r.id) AS total_likes
FROM posts p
JOIN usuarios u ON p.usuario_id = u.id
LEFT JOIN reacoes r ON r.post_id = p.id
GROUP BY p.id, p.titulo, p.conteudo, p.created_at, u.nome
ORDER BY total_likes DESC
LIMIT 5;
  `;

  return database.executar(instrucaoSql);
}

function postComment(titulo, conteudo, usuarioId) {
  var instrucaoSql = `
    INSERT INTO posts (usuario_id, titulo, conteudo)
    VALUES (${usuarioId}, '${titulo}', '${conteudo}');
  `;

  return database.executar(instrucaoSql);
}

function givaLike(postId, usuarioId) {
  var instrucaoSql = `
    INSERT IGNORE INTO reacoes (post_id, usuario_id)
    VALUES (${postId}, ${usuarioId});
  `;

  return database.executar(instrucaoSql);
}
module.exports = {
  forumGetter,
  forumTop5,
  postComment,
  givaLike,
};
