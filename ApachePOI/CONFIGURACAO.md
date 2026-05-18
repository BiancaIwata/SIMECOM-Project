# 🔐 SIMECOM - Guia de Configuração (Variáveis de Ambiente)

## ⚙️ Configuração Rápida

### 1. **Em desenvolvimento (Local)**

Defina as variáveis de ambiente antes de executar:

```bash
# Linux/Mac
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=simecom
export DB_USER=root
export DB_PASSWORD=sua_senha_aqui
export S3_BUCKET_NAME=simecom-s3
export AWS_REGION=us-east-1
export S3_PREFIX=01-raw/

# Depois execute
mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain"
```

**Ou** crie um arquivo `.env` na raiz do projeto:
```bash
cp .env.example .env
# Edite .env com suas credenciais
source .env  # Carrega as variáveis
```

### 2. **Em EC2 (Produção)**

Configure variáveis na instância:

```bash
# SSH na EC2
ssh -i sua_chave.pem ec2-user@seu-ip

# Defina as variáveis
export DB_HOST=seu-rds-endpoint.amazonaws.com
export DB_PORT=3306
export DB_NAME=simecom
export DB_USER=admin
export DB_PASSWORD=xxxxxxxxxxxxx
export AWS_REGION=sa-east-1

# Use IAM Role para AWS S3 (melhor segurança)
# Não precisa de AWS_ACCESS_KEY_ID e AWS_SECRET_ACCESS_KEY
```

### 3. **Em Docker/Container**

```dockerfile
FROM maven:3.9-eclipse-temurin-21

ENV DB_HOST=mysql-service
ENV DB_PORT=3306
ENV DB_NAME=simecom
ENV DB_USER=${DB_USER}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV AWS_REGION=sa-east-1

WORKDIR /app
COPY . .

CMD ["mvn", "exec:java", "-Dexec.mainClass=school.sptech.DataLoaderMain"]
```

Execute:
```bash
docker run -e DB_USER=admin -e DB_PASSWORD=xxx seu-container
```

---

## 📋 Variáveis Disponíveis

| Variável | Descrição | Padrão | Obrigatória |
|----------|-----------|--------|------------|
| `DB_HOST` | Host do MySQL | `localhost` | ❌ |
| `DB_PORT` | Porta do MySQL | `3306` | ❌ |
| `DB_NAME` | Nome do banco | `simecom` | ❌ |
| `DB_USER` | Usuário BD | - | ✅ |
| `DB_PASSWORD` | Senha BD | - | ✅ |
| `S3_BUCKET_NAME` | Bucket S3 | `simecom-s3` | ❌ |
| `AWS_REGION` | Região AWS | `us-east-1` | ❌ |
| `S3_PREFIX` | Prefixo S3 | `01-raw/` | ❌ |
| `LOG_LEVEL` | Nível de log | `INFO` | ❌ |

---

## 🔑 Credenciais AWS (Segurança)

### ✅ **Recomendado: IAM Role (EC2)**

Se executar em EC2, configure uma **IAM Role** com permissões S3:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::simecom-s3",
        "arn:aws:s3:::simecom-s3/*"
      ]
    }
  ]
}
```

**Vantagem:** Sem credenciais explícitas no código!

### ⚠️ Alternativa: Chaves programáticas

Se precisar usar chaves:

```bash
export AWS_ACCESS_KEY_ID=AKIA...
export AWS_SECRET_ACCESS_KEY=xxxxx
```

---

## 🚀 Executar o Projeto

```bash
# Modo 1: Listar arquivos
mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain"

# Modo 2: Processar um arquivo
mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain" \
  -Dexec.args="EXP_2017.xlsx"

# Modo 3: Processar todos
mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain" \
  -Dexec.args="todos"
```

---

## ✅ Checklist de Configuração

- [ ] `DB_USER` e `DB_PASSWORD` definidos
- [ ] Banco MySQL rodando e acessível
- [ ] Bucket S3 configurado e credenciais AWS funcionando
- [ ] Variáveis de ambiente carregadas (`echo $DB_USER`)
- [ ] Maven instalado e funcionando (`mvn --version`)

---

## 🐛 Solução de Problemas

**"Variável de ambiente obrigatória não está definida"**
```bash
# Verifique
env | grep DB_

# Configure
export DB_USER=seu_usuario
export DB_PASSWORD=sua_senha
```

**"Falha ao conectar ao banco"**
```bash
# Teste a conexão
mysql -h $DB_HOST -u $DB_USER -p$DB_PASSWORD -D $DB_NAME

# Ou com mysql-client
mysql -h localhost -u root -p simecom
```

**"Access denied for S3"**
```bash
# Verifique credenciais AWS
aws s3 ls s3://simecom-s3/

# Ou configure AWS CLI
aws configure
```
