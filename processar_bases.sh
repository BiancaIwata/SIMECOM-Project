#!/usr/bin/env bash

# Script unificado:
# 1. Baixa as bases CSV
# 2. Substitui ";" por "," nos CSVs baixados
# 3. Converte os CSVs tratados para XLSX usando ssconvert

# Diretório onde todo o processo será executado
DIRETORIO_PROCESSAMENTO="./processamento_bases"

mkdir -p "$DIRETORIO_PROCESSAMENTO"
cd "$DIRETORIO_PROCESSAMENTO" || exit 1

# ==========================================================
# 01 - Baixar base de dados csv;
# ==========================================================

# ===== BASE DE DADOS =====
URLS=(
"https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_1997_MUN.csv"
"https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_1998_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_1999_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2000_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2001_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2002_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2003_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2004_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2005_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2006_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2007_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2008_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2009_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2010_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2011_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2012_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2013_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2014_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2015_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2016_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2017_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2018_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2019_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2020_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2021_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2022_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2023_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2024_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2025_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/EXP_2026_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_1997_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_1998_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_1999_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2000_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2001_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2002_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2003_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2004_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2005_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2006_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2007_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2008_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2009_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2010_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2011_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2012_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2013_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2014_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2015_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2016_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2017_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2018_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2019_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2020_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2021_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2022_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2023_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2024_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2025_MUN.csv"
# "https://balanca.economia.gov.br/balanca/bd/comexstat-bd/mun/IMP_2026_MUN.csv"
)


for url in "${URLS[@]}"; do
    echo "Baixando: $url"

    wget --no-check-certificate -c "$url"

    if [ $? -eq 0 ]; then
        echo "Download concluído."
    else
        echo "Erro ao baixar: $url"
    fi

    echo "---------------------------"
done

echo "Todos os downloads finalizados."
# ==========================================================
# 02 - Converter tudo o que é ";" para ","
# ==========================================================

echo "Iniciando substituição de ';' por ',' nos arquivos CSV..."

for arquivo in *.csv; do
    echo "Convertendo delimitador em $arquivo..."
    sed -i 's/;/,/g' "$arquivo"
done

echo "Substituição concluída."

# ==========================================================
# Usar o ssconvert para converter csv para xlsx
# ==========================================================

# Converte todos os CSV do diretório atual para XLSX

shopt -s nullglob

arquivos=( *.csv )

if [ ${#arquivos[@]} -eq 0 ]; then
  echo "Nenhum arquivo CSV encontrado."
  exit 1
fi

for INPUT in "${arquivos[@]}"; do
  OUTPUT="${INPUT%.*}.xlsx"

  echo "Convertendo $INPUT -> $OUTPUT ..."
  ssconvert "$INPUT" "$OUTPUT"

  if [ $? -eq 0 ]; then
    echo "Arquivo gerado: $OUTPUT"
  else
    echo "Erro ao converter: $INPUT"
  fi

  echo "--------------------------"
done

echo "Conversão finalizada."

# ==========================================================
# Removendo arquivos csvs antigos
# ==========================================================

echo "Removendo arquivos CSV..."
rm -f *.csv
echo "Limpeza concluída. Apenas arquivos XLSX mantidos."
