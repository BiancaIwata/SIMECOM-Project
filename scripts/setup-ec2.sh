#!/bin/bash
# ====================================================================
# SIMECOM - Setup Automático para EC2
# ====================================================================
# Este script instala todas as dependências e configura variáveis
# de ambiente para rodar o projeto em produção (EC2 + RDS + S3)
# ====================================================================

set -e  # Exit se houver erro

echo "╔════════════════════════════════════════════════════════════╗"
echo "║     SIMECOM - Data Loader Setup para EC2                  ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# ====================================================================
# 1. INSTALAÇÃO DE DEPENDÊNCIAS
# ====================================================================

echo "📦 Atualizando sistema..."
sudo yum update -y > /dev/null 2>&1

echo "☕ Instalando Java 21..."
sudo yum install -y java-21-amazon-corretto > /dev/null 2>&1

echo "🏗️ Instalando Maven..."
sudo yum install -y maven > /dev/null 2>&1

echo "💾 Instalando MySQL Client..."
sudo yum install -y mysql-community-client > /dev/null 2>&1

echo "📝 Instalando Git..."
sudo yum install -y git > /dev/null 2>&1

echo "✓ Dependências instaladas com sucesso!"
echo ""

# ====================================================================
# 2. CRIAR DIRETÓRIOS
# ====================================================================

echo "📁 Criando estrutura de diretórios..."
mkdir -p ~/simecom
mkdir -p ~/.config/simecom
chmod 700 ~/.config/simecom  # Permissão segura

echo "✓ Diretórios criados"
echo ""

# ====================================================================
# 3. COLETAR CREDENCIAIS
# ====================================================================

echo "🔐 Configurando Credenciais (RDS + S3)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

read -p "📍 Endpoint do RDS (ex: simecom-rds.xxxxx.rds.amazonaws.com): " DB_HOST
read -p "👤 Usuário do RDS (ex: admin): " DB_USER
read -sp "🔑 Senha do RDS: " DB_PASSWORD
echo ""
read -p "🏪 Nome do Bucket S3 (ex: simecom-s3) [padrão: simecom-s3]: " S3_BUCKET
S3_BUCKET=${S3_BUCKET:-simecom-s3}

read -p "🌍 Região AWS (ex: sa-east-1) [padrão: us-east-1]: " AWS_REGION
AWS_REGION=${AWS_REGION:-us-east-1}

read -p "📂 Prefixo S3 (ex: 01-raw/) [padrão: 01-raw/]: " S3_PREFIX
S3_PREFIX=${S3_PREFIX:-01-raw/}

echo ""
echo "🔍 Valores confirmados:"
echo "  ├─ DB_HOST: $DB_HOST"
echo "  ├─ DB_USER: $DB_USER"
echo "  ├─ S3_BUCKET: $S3_BUCKET"
echo "  ├─ AWS_REGION: $AWS_REGION"
echo "  └─ S3_PREFIX: $S3_PREFIX"
echo ""

# ====================================================================
# 4. CRIAR ARQUIVO .env.production
# ====================================================================

echo "📄 Criando arquivo .env.production..."

ENV_FILE=~/.config/simecom/.env.production

cat > "$ENV_FILE" << EOF
# ════════════════════════════════════════════════════════════════
# SIMECOM - Configuração de Produção (EC2 + RDS + S3)
# ════════════════════════════════════════════════════════════════
# Gerado automaticamente via setup-ec2.sh
# Data: $(date)

# 🗄️ BANCO DE DADOS (RDS MySQL)
DB_HOST=$DB_HOST
DB_PORT=3306
DB_NAME=simecom
DB_USER=$DB_USER
DB_PASSWORD=$DB_PASSWORD

# ☁️ AWS S3
S3_BUCKET_NAME=$S3_BUCKET
AWS_REGION=$AWS_REGION
S3_PREFIX=$S3_PREFIX

# 📝 LOGGING
LOG_LEVEL=INFO
EOF

# Permissões seguras para arquivo com senhas
chmod 600 "$ENV_FILE"

echo "✓ Arquivo criado: $ENV_FILE"
echo ""

# ====================================================================
# 5. CRIAR SCRIPT DE CARREGAMENTO DE VARIÁVEIS
# ====================================================================

echo "📄 Criando script de carregamento..."

LOAD_SCRIPT=~/simecom/load-env.sh

cat > "$LOAD_SCRIPT" << 'EOF'
#!/bin/bash
# Carrega variáveis de ambiente para o SIMECOM

ENV_FILE=~/.config/simecom/.env.production

if [ ! -f "$ENV_FILE" ]; then
    echo "❌ Arquivo de configuração não encontrado: $ENV_FILE"
    echo "Execute setup-ec2.sh novamente"
    exit 1
fi

source "$ENV_FILE"

echo "✓ Variáveis carregadas de: $ENV_FILE"
echo "  Você pode agora executar o projeto:"
echo ""
echo "  cd ~/SIMECOM-Project/ApachePOI"
echo "  mvn exec:java -Dexec.mainClass=\"school.sptech.DataLoaderMain\" -Dexec.args=\"todos\""
EOF

chmod +x "$LOAD_SCRIPT"

echo "✓ Script criado: $LOAD_SCRIPT"
echo ""

# ====================================================================
# 6. CRIAR SCRIPT DE TESTE DE CONECTIVIDADE
# ====================================================================

echo "📄 Criando script de teste..."

TEST_SCRIPT=~/simecom/test-connectivity.sh

cat > "$TEST_SCRIPT" << 'EOF'
#!/bin/bash
# Testa conectividade com RDS e S3

source ~/.config/simecom/.env.production

echo "╔════════════════════════════════════════════════════════════╗"
echo "║         SIMECOM - Teste de Conectividade                   ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Teste RDS
echo "🧪 Testando MySQL (RDS)..."
if mysql -h "$DB_HOST" -u "$DB_USER" -p"$DB_PASSWORD" -D "$DB_NAME" -e "SELECT NOW();" > /dev/null 2>&1; then
    echo "✓ Conexão RDS: OK"
else
    echo "❌ Conexão RDS: FALHOU"
    exit 1
fi

echo ""

# Teste S3
echo "🧪 Testando S3..."
if aws s3 ls "s3://$S3_BUCKET_NAME/$S3_PREFIX" > /dev/null 2>&1; then
    echo "✓ Acesso S3: OK"
else
    echo "❌ Acesso S3: FALHOU"
    echo "Dica: Verifique IAM Role da EC2"
    exit 1
fi

echo ""
echo "✓ Todos os testes passaram!"
echo ""
echo "Próximo passo:"
echo "  cd ~/SIMECOM-Project/ApachePOI"
echo "  source ~/.config/simecom/.env.production"
echo "  mvn exec:java -Dexec.mainClass=\"school.sptech.DataLoaderMain\""
EOF

chmod +x "$TEST_SCRIPT"

echo "✓ Script criado: $TEST_SCRIPT"
echo ""

# ====================================================================
# 7. ADICIONAR VARIÁVEIS AO ~/.bashrc
# ====================================================================

echo "📝 Configurando ~/.bashrc..."

if ! grep -q "simecom/.env.production" ~/.bashrc; then
    echo "" >> ~/.bashrc
    echo "# SIMECOM - Data Loader" >> ~/.bashrc
    echo "export SIMECOM_ENV=~/.config/simecom/.env.production" >> ~/.bashrc
    echo "alias load-simecom='source \$SIMECOM_ENV'" >> ~/.bashrc
fi

echo "✓ Alias adicionado: load-simecom"
echo ""

# ====================================================================
# 8. RESUMO FINAL
# ====================================================================

echo "╔════════════════════════════════════════════════════════════╗"
echo "║              ✓ SETUP CONCLUÍDO COM SUCESSO!               ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

echo "📍 Arquivos criados:"
echo "  ├─ ~/.config/simecom/.env.production (credenciais)"
echo "  ├─ ~/simecom/load-env.sh (carrega variáveis)"
echo "  └─ ~/simecom/test-connectivity.sh (testa conexão)"
echo ""

echo "🚀 Próximos passos:"
echo ""
echo "1️⃣ Testar conectividade:"
echo "   $ ~/simecom/test-connectivity.sh"
echo ""
echo "2️⃣ Carregar variáveis:"
echo "   $ source ~/.config/simecom/.env.production"
echo "   OU use alias:"
echo "   $ load-simecom"
echo ""
echo "3️⃣ Clonar/atualizar repositório:"
echo "   $ cd ~ && git clone https://github.com/seu-usuario/SIMECOM-Project.git"
echo "   $ cd SIMECOM-Project/ApachePOI"
echo ""
echo "4️⃣ Executar o projeto:"
echo "   $ mvn exec:java -Dexec.mainClass=\"school.sptech.DataLoaderMain\" -Dexec.args=\"todos\""
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "               Você pode agora usar o projeto! 🎉"
echo "═══════════════════════════════════════════════════════════════"
