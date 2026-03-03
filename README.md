# SIMECOM-Project

Este repositório tem por finalidade a centralização de código-fonte e artefatos relacionados ao projeto **SIMECOM**.

Aqui são armazenados:

- Código da aplicação (backend e/ou frontend)
- Scripts de banco de dados
- Documentação técnica
- Arquivos auxiliares do projeto
- Testes automatizados

---

# 📌 Padrão de Commits (Conventional Commits)

Este projeto utiliza o padrão **Conventional Commits** para manter um histórico de alterações organizado, legível e padronizado.

---

## 🚀 feat
Utilizado para adicionar uma nova funcionalidade ou nova implementação ao código.

Exemplo:
```
git commit -m "feat: upload de arquivos csv para atualização da metrica"
```

## 🐛 fix
Indica que um bug ou problema foi corrigido.

Exemplo:
```
git commit -m "fix: bug de loop infinito da pagina login"
```

## 📚 docs
Usado para mudanças que afetam apenas arquivos de documentação, como o README.

Exemplo:
```
git commit -m "docs: atualizando contexto para detalhar mercado e diretrizes do artesp"
```

## 🎨 style
Para alterações na formatação do código que não afetam sua lógica, como:
```
git commit -m "style: descrição da alteração"
```
- Indentação
- Espaçamento
- Remoção de comentários
- Ajustes visuais

Exemplo:
```
git commit -m "style: criando efeitos de animação do login e cadastro com toast em estado de sucesso"
```

## ♻️ refactor
Utilizado quando o código é modificado sem adicionar novas funcionalidades ou corrigir bugs.

Exemplo:
```
git commit -m "refactor: código do UserController seguindo padrão early return e async/await"
```

## ⚡ perf
Indica uma alteração que melhora o desempenho da aplicação.

Exemplo:
```
git commit -m "perf: criando views para consulta de metricas da dashboard principal com indexação de tabelas"
```

## 🧪 test
Para adicionar ou modificar testes unitários ou de integração.

Exemplo:
```
git commit -m "test: criando teste unitario do serviço de autenticação de administrador"
```

## 🔧 chore
Usado para tarefas que não impactam diretamente o código da aplicação, como:

- Atualização de dependências
- Configuração de ambiente
- Ajustes em arquivos de build
- Scripts auxiliares

Exemplo:
```
git commit -m "chore: atualizando dependencias do projeto"
```

## 🎯 Objetivo do Padrão
- Melhorar a organização do histórico de commits
- Facilitar code reviews
- Permitir geração automática de changelog futuramente
- Manter padrão profissional de versionamento

## 📎 Boas Práticas
- Sempre escrever mensagens claras e objetivas
- Usar verbos no infinitivo
- Evitar mensagens genéricas como: "ajustes" ou "alterações"
