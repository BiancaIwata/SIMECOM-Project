# 🚀 SIMECOM - Data Loader (Apache POI + S3 + MySQL)

Pipeline de carga de dados de comércio exterior (MDIC) para banco de dados MySQL via AWS S3.

**Tecnologias:** Java 21 | Maven | Apache POI | AWS S3 | MySQL | Docker

---

## 📋 Pré-requisitos

### Local (Desenvolvimento)
- Java 21+
- Maven 3.8+
- MySQL 8.0+
- Git

### EC2 (Produção)
- Amazon EC2 (t3.small ou superior)
- Amazon RDS MySQL 8.0+
- AWS S3 Bucket
- IAM Role com permissão S3

---

## ⚡ Quick Start em EC2

### **1. Conectar na EC2**

```bash
ssh -i sua_chave.pem ec2-user@seu-ip-ec2
```

### **2. Executar Script de Setup**

```bash
# Clonar
git clone https://github.com/seu-usuario/SIMECOM-Project.git
cd SIMECOM-Project

# Setup automático (instala dependências)
chmod +x scripts/setup-ec2.sh
./scripts/setup-ec2.sh

# Ele vai:
# ✓ Instalar Java 21
# ✓ Instalar Maven
# ✓ Criar diretório /home/ec2-user/simecom/
# ✓ Pedir credenciais de RDS e S3
# ✓ Criar arquivo .env.production com as variáveis
```

### **3. Configurar Credenciais (se não fizer no Script)**

```bash
# Editar arquivo de produção
nano /home/ec2-user/simecom/.env.production

# Conteúdo esperado:
# DB_HOST=seu-rds-endpoint.rds.amazonaws.com
# DB_USER=admin
# DB_PASSWORD=sua_senha_rds
# AWS_REGION=sa-east-1
# S3_BUCKET_NAME=simecom-s3
```

### **4. Processar Dados**

```bash
# Carregar variáveis
source /home/ec2-user/simecom/.env.production

# Entrar no projeto
cd ~/SIMECOM-Project/ApachePOI

# Listar arquivos do S3
mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain"

# Processar TODOS os arquivos
mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain" -Dexec.args="todos"
```

---

## 📊 Modos de Execução

```bash
# 1. Listar arquivos disponíveis no S3
mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain"

# 2. Processar um arquivo específico
mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain" -Dexec.args="EXP_2017.xlsx"

# 3. Processar todos os arquivos
mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain" -Dexec.args="todos"
```

---

## 🏗️ Arquitetura

```
AWS S3 Bucket
   (EXP_2017.xlsx)
         ↓
    EC2 Instance
    (DataLoader)
         ↓
    RDS MySQL
   (simecom)
```

---

## 🔐 Variáveis de Ambiente

| Variável | Exemplo | Obrigatório |
|----------|---------|------------|
| DB_HOST | simecom-rds.xxxxx.rds.amazonaws.com | ✅ |
| DB_USER | admin | ✅ |
| DB_PASSWORD | senha123 | ✅ |
| DB_PORT | 3306 | ❌ |
| DB_NAME | simecom | ❌ |
| S3_BUCKET_NAME | simecom-s3 | ❌ |
| AWS_REGION | sa-east-1 | ❌ |
| S3_PREFIX | 01-raw/ | ❌ |

---

## 🚨 Troubleshooting

```bash
# Verifique conectividade RDS
mysql -h $DB_HOST -u $DB_USER -p$DB_PASSWORD -D $DB_NAME -e "SELECT 1;"

# Verifique acesso S3
aws s3 ls s3://$S3_BUCKET_NAME/$S3_PREFIX

# Verifique se variáveis estão carregadas
echo $DB_HOST
```

---

## 📚 Documentação

- [ApachePOI/CONFIGURACAO.md](ApachePOI/CONFIGURACAO.md) - Detalhes de configuração
- [scripts/setup-ec2.sh](scripts/setup-ec2.sh) - Script de setup

---

## 👤 Autor

Ricardo Perdigão
