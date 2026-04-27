# COMEX Report App

Aplicação Java para leitura de bases CSV de importação e exportação, aplicação de filtros, cálculo de métricas, geração de gráficos e criação de relatório final em PDF, com suporte opcional a geração de insights por IA.

## Objetivo do projeto

Este projeto foi construído para validar um fluxo simples e direto de análise sobre dados de comércio exterior usando apenas Java, sem Spring Boot e sem frameworks web.

Fluxo da aplicação:

1. Receber filtros via arquivo JSON
2. Ler CSV de importação, exportação ou ambos
3. Filtrar os registros
4. Calcular métricas
5. Gerar 3 gráficos
6. Gerar texto analítico via IA ou fallback local
7. Criar o PDF final

## Características principais

- Java puro, executado por linha de comando
- Sem Spring Boot
- Leitura de CSV separado por `;`
- Suporte a duas bases distintas:
  - importação
  - exportação
- Suporte aos modos:
  - apenas importação
  - apenas exportação
  - comparação entre ambos
- Geração de métricas iniciais para validação
- Geração de gráficos em PNG
- Geração de relatório PDF com Apache PDFBox
- Integração opcional com IA por HTTP
- Fallback local automático quando a IA não está configurada ou falha

---

## Estrutura do projeto

```text
comex-report-app/
├── data/
│   ├── importacoes.csv
│   └── exportacoes.csv
├── output/
├── src/
│   └── main/
│       └── java/
│           └── com/example/comex/
│               ├── app/
│               │   └── Main.java
│               ├── config/
│               │   └── AppConfig.java
│               ├── model/
│               │   ├── ChartFiles.java
│               │   ├── FilterRequest.java
│               │   ├── MetricsResult.java
│               │   ├── ReportContext.java
│               │   ├── TradeRecord.java
│               │   └── TradeType.java
│               ├── service/
│               │   ├── AiInsightService.java
│               │   ├── CalculationService.java
│               │   ├── ChartService.java
│               │   ├── CsvReaderService.java
│               │   ├── FilterService.java
│               │   ├── LocalInsightService.java
│               │   └── PdfReportService.java
│               └── util/
│                   ├── CsvUtils.java
│                   ├── FileUtils.java
│                   └── NumberUtils.java
├── filtros-exemplo.json
├── pom.xml
└── README.md
```

---

## Requisitos de ambiente

### Obrigatórios

- JDK 21
- Maven 3.9+ recomendado
- Sistema operacional com suporte a execução Java

### Verificação rápida

```bash
java -version
javac -version
mvn -version
```

O projeto foi configurado com:

```xml
<maven.compiler.source>21</maven.compiler.source>
<maven.compiler.target>21</maven.compiler.target>
```

Se sua máquina estiver usando Java 17, 11 ou inferior, ajuste o JDK antes de compilar.

---

## Dependências do projeto

Bibliotecas usadas:

- **Jackson Databind**: leitura do JSON de filtros
- **JFreeChart**: geração de gráficos PNG
- **Apache PDFBox**: geração do relatório PDF

Essas dependências são gerenciadas pelo Maven no `pom.xml`.

---

## Formato esperado dos CSVs

A aplicação espera arquivos CSV com este cabeçalho:

```csv
"CO_ANO";"CO_MES";"SH4";"CO_PAIS";"SG_UF_MUN";"CO_MUN";"KG_LIQUIDO";"VL_FOB"
```

Exemplo de linha:

```csv
"2001";"01";"7210";"023";"RJ";"3306305";2296388;1022323
```

### Observações importantes

- O separador deve ser `;`
- O leitor atual ignora a primeira linha como cabeçalho
- A aplicação espera no mínimo 8 colunas por linha
- O código trata os números usando `BigDecimal`
- Importação e exportação devem estar em arquivos separados

---

## Como o sistema funciona

### 1. Entrada dos filtros

A aplicação recebe um arquivo JSON no momento da execução.

### 2. Leitura dos CSVs

Com base no campo `tradeType`, a aplicação:

- lê apenas o CSV de importação
- ou apenas o CSV de exportação
- ou os dois

### 3. Filtragem

A filtragem é feita em memória pelos campos:

- ano inicial e final
- mês inicial e final
- SH4
- país
- UF
- município

### 4. Cálculo de métricas

A versão atual calcula:

- total de `VL_FOB` de importação
- total de `VL_FOB` de exportação
- total de `KG_LIQUIDO` de importação
- total de `KG_LIQUIDO` de exportação
- diferença absoluta de `VL_FOB`
- diferença percentual de `VL_FOB`
- série mensal de `VL_FOB` por tipo
- ranking dos 10 maiores SH4 por `VL_FOB`

### 5. Geração dos gráficos

São gerados 3 arquivos PNG:

- `grafico_1_comparativo_fob.png`
- `grafico_2_serie_mensal.png`
- `grafico_3_top_sh4.png`

### 6. Geração do texto analítico

A aplicação tenta usar a IA.

Se a IA estiver indisponível, desconfigurada ou retornar erro, o sistema usa um texto local padrão gerado internamente.

### 7. Geração do PDF

O PDF final reúne:

- título do relatório
- filtros aplicados
- total de registros filtrados
- indicação se o texto veio de IA ou fallback local
- métricas principais
- insights
- gráficos gerados

Arquivo final:

- `output/relatorio_comex.pdf`

---

## Como compilar o projeto

Na raiz do projeto, execute:

```bash
mvn clean package
```

Se tudo estiver certo, o Maven vai gerar o jar principal em:

```text
target/comex-report-app-1.0.0-jar-with-dependencies.jar
```

---

## Como executar o projeto

### Execução básica

```bash
java -jar target/comex-report-app-1.0.0-jar-with-dependencies.jar filtros-exemplo.json
```

### Exemplo completo

```bash
cd /caminho/para/comex-report-app
mvn clean package
java -jar target/comex-report-app-1.0.0-jar-with-dependencies.jar filtros-exemplo.json
```

### Saída esperada no terminal

A aplicação imprime mensagens como:

- quantidade de registros lidos
- quantidade de registros filtrados
- caminho do PDF gerado
- caminho dos gráficos gerados
- se a IA foi usada ou se o fallback local foi ativado

---

## Exemplo de arquivo de filtros

Arquivo `filtros-exemplo.json`:

```json
{
  "importCsvPath": "data/importacoes.csv",
  "exportCsvPath": "data/exportacoes.csv",
  "tradeType": "BOTH",
  "yearStart": 2024,
  "yearEnd": 2024,
  "monthStart": 1,
  "monthEnd": 12,
  "sh4List": ["7210", "8301", "8409", "3923", "3004"],
  "countryList": [],
  "ufList": [],
  "municipalityList": [],
  "outputDirectory": "output",
  "reportTitle": "Relatório Inicial COMEX - Validação",
  "prompt": "Gere insights iniciais sobre o recorte filtrado, destacando comparação entre importação e exportação, concentração por SH4 e comportamento mensal."
}
```

---

## Campos do JSON de entrada

### Caminhos dos arquivos

- `importCsvPath`: caminho para o CSV de importação
- `exportCsvPath`: caminho para o CSV de exportação

### Tipo de análise

- `tradeType`: pode ser:
  - `IMPORT`
  - `EXPORT`
  - `BOTH`

### Período

- `yearStart`: ano inicial
- `yearEnd`: ano final
- `monthStart`: mês inicial
- `monthEnd`: mês final

### Listas de filtros

- `sh4List`: lista de códigos SH4
- `countryList`: lista de códigos de país
- `ufList`: lista de UFs
- `municipalityList`: lista de códigos de município

### Saída e relatório

- `outputDirectory`: diretório de saída
- `reportTitle`: título do relatório
- `prompt`: instrução base para a IA

### Regras práticas

- listas vazias significam “sem filtro” para aquele campo
- se `tradeType` vier nulo, o sistema define `BOTH`
- o diretório de saída é criado automaticamente se não existir

---

## Como habilitar a IA

A integração de IA é opcional e usa variáveis de ambiente.

### Variáveis suportadas

- `AI_API_URL`
- `AI_API_KEY`
- `AI_MODEL`

### Comportamento atual

A classe `AppConfig` considera a IA habilitada quando:

- `AI_API_URL` está preenchida
- `AI_API_KEY` está preenchida

O modelo, se não for definido, assume este padrão:

```text
gpt-4.1-mini
```

### Exemplo no Linux

```bash
export AI_API_URL="https://sua-api.example.com/v1/responses"
export AI_API_KEY="sua_chave_aqui"
export AI_MODEL="gpt-4.1-mini"
```

Depois disso, rode normalmente:

```bash
java -jar target/comex-report-app-1.0.0-jar-with-dependencies.jar filtros-exemplo.json
```

---

## Formato esperado da resposta da IA

A implementação atual envia um JSON simples por HTTP POST com os campos:

```json
{
  "model": "...",
  "input": "..."
}
```

E tenta extrair texto da resposta nesta ordem:

1. `output[].content[].text`
2. `text`
3. corpo bruto da resposta

Isso significa que a API que você usar deve ser compatível com esse formato ou deve ser colocada atrás de um adaptador que responda dessa forma.

### Headers enviados

- `Authorization: Bearer <AI_API_KEY>`
- `Content-Type: application/json`

### Timeouts atuais

- conexão: 20 segundos
- requisição: 60 segundos

---

## O que acontece quando a IA falha

Se a IA não estiver configurada ou retornar erro:

- a aplicação não interrompe o fluxo
- o relatório continua sendo gerado
- o texto analítico passa a ser criado pelo `LocalInsightService`

Isso garante que o PDF sempre possa ser produzido mesmo sem integração externa.

---

## Arquivos gerados na execução

No diretório definido em `outputDirectory`, a aplicação gera:

```text
output/
├── grafico_1_comparativo_fob.png
├── grafico_2_serie_mensal.png
├── grafico_3_top_sh4.png
└── relatorio_comex.pdf
```

---

## Resumo técnico das classes principais

### `Main`

Orquestra toda a execução:

- lê o JSON
- decide quais CSVs carregar
- aplica filtro
- calcula métricas
- gera gráficos
- tenta IA
- aplica fallback se necessário
- gera PDF

### `CsvReaderService`

Lê os arquivos CSV e transforma cada linha em `TradeRecord`.

### `FilterService`

Aplica os filtros em memória sobre a lista de registros lidos.

### `CalculationService`

Calcula as métricas consolidadas usadas nos gráficos e no relatório.

### `ChartService`

Gera os três gráficos em PNG usando JFreeChart.

### `AiInsightService`

Faz a chamada HTTP para o endpoint de IA.

### `LocalInsightService`

Gera o texto local de fallback.

### `PdfReportService`

Monta o PDF final usando Apache PDFBox.

---

## Métricas atualmente implementadas

As métricas atuais são de validação inicial. Elas servem para comprovar o funcionamento do pipeline.

Hoje o projeto já entrega:

- soma de FOB por tipo
- soma de peso líquido por tipo
- diferença absoluta entre exportação e importação
- diferença percentual entre exportação e importação
- agrupamento mensal de FOB por tipo
- top 10 SH4 por FOB

Essas métricas podem ser trocadas ou expandidas depois sem alterar a arquitetura geral do fluxo.

---

## Limitações atuais da primeira versão

Esta é uma versão inicial de validação. Alguns pontos importantes:

- os dados são processados em memória
- não há banco de dados
- não há interface web
- não há autenticação
- o layout do PDF é simples e técnico
- a integração de IA depende do formato de resposta esperado pela implementação atual
- filtros muito amplos sobre arquivos muito grandes podem consumir bastante memória

---

## Problemas comuns e solução

### 1. `mvn: command not found`

Instale o Maven.

Exemplo em Debian/Ubuntu:

```bash
sudo apt update
sudo apt install maven
```

### 2. erro de versão do Java

Se aparecer erro de compilação relacionado ao target/source 21, verifique o JDK:

```bash
java -version
javac -version
```

### 3. CSV não encontrado

Revise os caminhos definidos em:

- `importCsvPath`
- `exportCsvPath`

Use caminhos relativos corretos à pasta de execução ou caminhos absolutos.

### 4. IA não funciona

Verifique:

- `AI_API_URL`
- `AI_API_KEY`
- conectividade de rede
- formato da resposta da API

Se não funcionar, o projeto deve cair automaticamente no fallback local.

### 5. PDF ou gráficos não aparecem

Confirme se:

- o diretório de saída existe ou pode ser criado
- o processo tem permissão de escrita
- a execução foi concluída sem exceções

---

## Exemplo de execução sem IA

```bash
mvn clean package
java -jar target/comex-report-app-1.0.0-jar-with-dependencies.jar filtros-exemplo.json
```

Nesse caso, se nenhuma variável de IA estiver definida, o PDF ainda será gerado com o texto do fallback local.

---

## Exemplo de execução com IA

```bash
export AI_API_URL="https://sua-api.example.com/v1/responses"
export AI_API_KEY="sua_chave"
export AI_MODEL="gpt-4.1-mini"

mvn clean package
java -jar target/comex-report-app-1.0.0-jar-with-dependencies.jar filtros-exemplo.json
```

---

## Melhorias futuras recomendadas

- permitir métricas configuráveis por JSON
- suportar múltiplos layouts de relatório
- paginação e otimização para CSVs muito grandes
- exportação adicional para CSV ou JSON consolidado
- camada de testes automatizados
- logs estruturados
- validação mais forte do arquivo de entrada
- desacoplar a integração de IA em interface própria
- adicionar interface web ou API REST em uma fase posterior

---

## Resumo final

Este projeto implementa um pipeline completo e simples para:

- leitura de bases CSV de comércio exterior
- aplicação de filtros
- cálculo de métricas
- geração de gráficos
- produção de insights
- geração de PDF

Ele foi desenhado para servir como uma base inicial de validação técnica, com estrutura clara, dependências enxutas e possibilidade de evolução posterior.
