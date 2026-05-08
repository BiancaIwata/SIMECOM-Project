# 🚀 Guia de Deployment em EC2

Instruções passo a passo para deployar o SIMECOM na AWS EC2.

---

## 📋 Pré-requisitos AWS

Antes de começar, prepare na AWS:

### 1. **EC2 Instance**
- Tipo: `t3.small` ou superior
- OS: Amazon Linux 2
- IAM Role: Com permissão **S3**

### 2. **RDS MySQL**
- Versão: 8.0+
- Database: `simecom`
- Security Group: Aberto para a EC2 (porta 3306)

### 3. **S3 Bucket**
- Nome: `simecom-s3` (ou seu nome)
- Arquivos em: `s3://simecom-s3/01-raw/`

### 4. **IAM Role (Importante!)**

Crie uma role com esta política:

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

Associe esta role à sua EC2.

---

## ⚡ Quick Deploy (5 minutos)

### **1. SSH na EC2**

```bash
ssh -i sua_chave.pem ec2-user@seu-ip-ec2
```

### **2. Executar Setup Automático**

```bash
# Clonar repositório
git clone https://github.com/seu-usuario/SIMECOM-Project.git
cd SIMECOM-Project

# Executar setup (vai instalar tudo e pedir credenciais)
chmod +x scripts/setup-ec2.sh
./scripts/setup-ec2.sh
```

**O script vai:**
- ✓ Instalar Java 21 e Maven
- ✓ Pedir suas credenciais RDS
- ✓ Criar arquivo `.env.production` (seguro)
- ✓ Adicionar alias `load-simecom` ao `.bashrc`

### **3. Testar Conectividade**

```bash
~/simecom/test-connectivity.sh
```

Deve exibir:
```
✓ Conexão RDS: OK
✓ Acesso S3: OK
✓ Todos os testes passaram!
```

### **4. Executar o Projeto**

```bash
# Carregar variáveis
source ~/.config/simecom/.env.production

# Entrar no projeto
cd ~/SIMECOM-Project/ApachePOI

# Listar arquivos
mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain"

# Processar TODOS os arquivos
mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain" -Dexec.args="todos"
```

---

## 🔍 Monitoramento

### **Verificar Status do Banco**

```bash
source ~/.config/simecom/.env.production

# Conectar ao banco
mysql -h $DB_HOST -u $DB_USER -p$DB_PASSWORD -D $DB_NAME

# Dentro do MySQL:
SELECT COUNT(*) FROM base_exportacao;
SELECT COUNT(*) FROM base_importacao;

# Ver logs da aplicação
SELECT * FROM log_java ORDER BY data_hora DESC LIMIT 5;
```

### **Verificar S3**

```bash
source ~/.config/simecom/.env.production

# Listar arquivos
aws s3 ls s3://$S3_BUCKET_NAME/$S3_PREFIX

# Contar arquivos
aws s3 ls s3://$S3_BUCKET_NAME/$S3_PREFIX --recursive | wc -l
```

---

## 🔧 Configuração Manual (Se o script não funcionar)

### **1. Instalar Dependências**

```bash
# Atualizar sistema
sudo yum update -y

# Java 21
sudo yum install -y java-21-amazon-corretto

# Maven
sudo yum install -y maven

# MySQL Client
sudo yum install -y mysql-community-client

# Git
sudo yum install -y git

# Verificar
java -version
mvn -version
```

### **2. Criar Arquivo de Configuração**

```bash
mkdir -p ~/.config/simecom
cat > ~/.config/simecom/.env.production << 'EOF'
DB_HOST=seu-rds-endpoint.rds.amazonaws.com
DB_PORT=3306
DB_NAME=simecom
DB_USER=admin
DB_PASSWORD=sua_senha
AWS_REGION=sa-east-1
S3_BUCKET_NAME=simecom-s3
S3_PREFIX=01-raw/
EOF

# Permissão segura
chmod 600 ~/.config/simecom/.env.production
```

### **3. Clonar Repositório**

```bash
cd ~
git clone https://github.com/seu-usuario/SIMECOM-Project.git
cd SIMECOM-Project/ApachePOI
```

### **4. Executar**

```bash
# Carregar variáveis
source ~/.config/simecom/.env.production

# Compilar
mvn clean compile

# Executar
mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain" -Dexec.args="todos"
```

---

## 🔄 Automatizar com Cron (Opcional)

Execute o projeto periodicamente:

```bash
# Editar crontab
crontab -e

# Adicionar (executar a cada dia às 2 da manhã)
0 2 * * * source ~/.config/simecom/.env.production && \
           cd ~/SIMECOM-Project/ApachePOI && \
           mvn exec:java -Dexec.mainClass="school.sptech.DataLoaderMain" -Dexec.args="todos" \
           >> /var/log/simecom.log 2>&1

# Ver crontabs
crontab -l

# Ver logs
tail -f /var/log/simecom.log
```

---

## 🐛 Troubleshooting

### **Erro: "Cannot connect to RDS"**

```bash
# 1. Verifique o endpoint
echo $DB_HOST

# 2. Teste conexão manual
mysql -h $DB_HOST -u $DB_USER -p$DB_PASSWORD -D $DB_NAME -e "SELECT 1;"

# 3. Verifique security groups
# - EC2 outbound: Aberto para porta 3306
# - RDS inbound: Aberto da EC2 (porta 3306)

# 4. Verifique credenciais
# - Usuário existe?
# - Senha está correta?
```

### **Erro: "Access denied for S3"**

```bash
# 1. Verifique IAM Role
aws sts get-caller-identity

# 2. Verifique bucket
aws s3 ls s3://$S3_BUCKET_NAME

# 3. Verifique policy
aws iam get-role --role-name seu-role

# 4. Reboot pode ajudar (para ativar a role)
sudo reboot
```

### **Erro: "XLSX processing failed"**

```bash
# 1. Verifique arquivo no S3
aws s3 ls s3://$S3_BUCKET_NAME/$S3_PREFIX

# 2. Baixe e teste localmente
aws s3 cp s3://$S3_BUCKET_NAME/$S3_PREFIX/seu-arquivo.xlsx .

# 3. Verifique formato
file seu-arquivo.xlsx
```

### **Erro: "Out of memory"**

O projeto usa SAX streaming (baixa memória), mas se ainda tiver problema:

```bash
# Aumentar heap da JVM
export MAVEN_OPTS="-Xmx1024m"
mvn exec:java ...

# Ou processar arquivo por arquivo
mvn exec:java ... -Dexec.args="EXP_2017.xlsx"
```

---

## 📊 Monitoramento Avançado

### **CloudWatch (AWS)**

Monitore logs em tempo real:

```bash
# EC2 enviando logs para CloudWatch (opcional)
# Configure CloudWatch Agent na EC2
```

### **Banco de Dados**

```bash
# Ver todas as importações
SELECT 
    nome_arquivo,
    status,
    linhas_inseridas,
    data_hora
FROM log_java
ORDER BY data_hora DESC;

# Contar registros
SELECT COUNT(*) as total FROM base_exportacao;
SELECT COUNT(*) as total FROM base_importacao;

# Erros
SELECT * FROM log_java WHERE status = 'NAO FINALIZADO';
```

---

## 🔒 Segurança em Produção

✅ **Checklist:**

- [ ] `.env.production` tem permissão 600 (`chmod 600`)
- [ ] Não commitir `.env.production` no Git
- [ ] IAM Role usada (sem chaves AWS explícitas)
- [ ] RDS com security group restritivo
- [ ] EC2 com security group restritivo
- [ ] Backup RDS habilitado
- [ ] Rotação de senhas periódica

---

## 📞 Suporte

- Encontrou um problema? Abra uma issue no GitHub
- Dúvidas? Veja [README.md](../README.md)
- Configuração? Veja [CONFIGURACAO.md](../ApachePOI/CONFIGURACAO.md)
