document.getElementById("select-periodo").addEventListener("change", function () {
    var anoInicial = Number(this.value);

    if (!anoInicial) return;

    var anoFinal = anoInicial + 5;

    // Atualiza o texto do período na tela
    this.closest(".card").querySelector("p").textContent = anoInicial + "-" + anoFinal;

    // Recarrega todos os gráficos com o novo período
    buscarSituacaoMercado(anoInicial);
    buscarTopSetores(anoInicial);
    buscarTopSetoresExportacao(anoInicial);
    buscarTopSetoresImportacao(anoInicial);
});

// Gráfico de Linhas (Evolução das Importações)
function buscarSituacaoMercado(anoInicial) {
  fetch(`/setores/buscarSituacaoMercado?anoInicial=${anoInicial}`, { cache: 'no-store' })
    .then(function (response) {
      if (response.ok) {
        response.json().then(resultado => {
          console.log(resultado)
          plotarGraficoLinhas(resultado);
        });
      } else {
        console.error('Nenhum dado encontrado ou erro na API');
      }
    }).catch(function (error) {
      console.error(`Erro na obtenção dos dados p/ gráfico: ${error.message}`);
    });
}

function plotarGraficoLinhas(dados) {
  var labels = [];
  var importacoes = [];
  var exportacoes = [];

  for (var i = 0; i < dados.length; i++) {
    labels.push(String(dados[i].ano));
    importacoes.push(dados[i].importacoes_milhoes_usd);
    exportacoes.push(dados[i].exportacoes_milhoes_usd);
  }

  const ctx = document.getElementById("lineChart");

  new Chart(ctx, {
    type: "line",
    data: {
      labels: labels,
      datasets: [
        {
          label: "Importações (Milhões USD$)",
          data: importacoes,
          borderColor: "#0036c9",
          backgroundColor: "rgba(0, 77, 201, 0.1)",
          fill: true,
          tension: 0.45,
          borderWidth: 4,
          pointBackgroundColor: "#0036c9",
          pointBorderColor: "#ffffff",
          pointBorderWidth: 2,
          pointRadius: 5,
          pointHoverRadius: 8,
          pointHoverBackgroundColor: "#0036c9",
        },
        {
          label: "Exportações (Milhões USD$)",
          data: exportacoes,
          borderColor: "#0a9317",
          backgroundColor: "rgba(21, 179, 18, 0.1)",
          fill: true,
          tension: 0.45,
          borderWidth: 4,
          pointBackgroundColor: "#0a9317",
          pointBorderColor: "#ffffff",
          pointBorderWidth: 2,
          pointRadius: 5,
          pointHoverRadius: 8,
          pointHoverBackgroundColor: "#0a9317",
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,

      interaction: {
        mode: "index",
        intersect: false,
      },

      plugins: {
        legend: {
          position: "top",

          labels: {
            color: "#111827",

            font: {
              size: 14,
              weight: "bold",
            },

            padding: 20,
            usePointStyle: true,
            pointStyle: "circle",
          },
        },

        tooltip: {
          backgroundColor: "#ffffff",

          titleColor: "#111827",
          bodyColor: "#374151",

          borderColor: "#e5e7eb",
          borderWidth: 1,

          padding: 12,

          displayColors: true,

          callbacks: {
            label: function (context) {
              return ` ${context.dataset.label}: $${context.parsed.y}M`;
            },
          },
        },
      },

      scales: {
        x: {
          grid: {
            color: "rgba(0,0,0,0.05)",
            drawBorder: false,
          },

          ticks: {
            color: "#374151",

            font: {
              size: 12,
            },
          },
        },

        y: {
          beginAtZero: true,

          grid: {
            color: "rgba(0,0,0,0.05)",
            drawBorder: false,
          },

          ticks: {
            color: "#374151",

            callback: function (value) {
              return "$" + value + "M";
            },
          },
        },
      },

      animation: {
        duration: 1800,
        easing: "easeOutQuart",
      },

      elements: {
        line: {
          cubicInterpolationMode: "monotone",
        },
      },
    }
  });
}

function buscarTopSetores(anoInicial) {
  fetch(`/setores/buscarTopSetores?anoInicial=${anoInicial}`, { cache: 'no-store' })
    .then(function (response) {
      if (response.ok) {
        response.json().then(function (resultado) {
          plotarGraficoPie(resultado);
        });
      }
    }).catch(function (error) {
      console.error(`Erro: ${error.message}`);
    });
}

// Gráfico de Rosca (Categorias)
function plotarGraficoPie(dados) {
  var labels = [];
  var valores = [];

  for (var i = 0; i < 4; i++) {
    labels.push(String(dados[i].nome));
    valores.push(dados[i].valor_total);
  }

  var outros = 0;
  for (var i = 4; i < dados.length; i++) {
    outros += dados[i].valor_total;
  }

  labels.push("Outros");
  valores.push(outros);

  chartPie = new Chart(document.getElementById("pieChart"), {
    type: "doughnut",
    data: {
      labels: labels,
      datasets: [{
        data: valores,
        backgroundColor: [
          "#3b82f6", // azul
          "#22c55e", // verde
          "#f59e0b", // amarelo
          "#ef4444", // vermelho
          "#8b5cf6", // roxo
        ],

        hoverBackgroundColor: [
          "#60a5fa",
          "#4ade80",
          "#fbbf24",
          "#f87171",
          "#a78bfa",
        ],

        borderWidth: 5,
        borderColor: "#ffffff",

        hoverOffset: 18,
        spacing: 6,
        cutout: "68%",
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,

      layout: {
        padding: 25,
      },

      animation: {
        animateRotate: true,
        animateScale: true,

        duration: 1800,
        easing: "easeOutQuart",
      },

      plugins: {
        legend: {
          position: "bottom",

          labels: {
            color: "#111827",

            padding: 22,
            usePointStyle: true,
            pointStyle: "circle",

            font: {
              size: 13,
              weight: "bold",
            },
          },
        },

        tooltip: {
          backgroundColor: "#ffffff",

          titleColor: "#111827",
          bodyColor: "#374151",

          borderColor: "#e5e7eb",
          borderWidth: 1,

          padding: 12,

          callbacks: {
            label: function (context) {
              return (
                " " + context.label + ": " + context.parsed + "% da participação"
              );
            },
          },
        },
      },
    }
  });
}

function buscarTopSetoresImportacao(anoInicial) {
  fetch(`/setores/buscarTopSetoresImportacao?anoInicial=${anoInicial}`, { cache: 'no-store' })
    .then(function (response) {
      if (response.ok) {
        response.json().then(function (resultado) {
          plotarGraficoBarImportacao(resultado);
        });
      }
    }).catch(function (error) {
      console.error(`Erro: ${error.message}`);
    });
}

// Gráfico de Barras (Crescimento)
function plotarGraficoBarImportacao(dados) {
  var labels = [];
  var valores = [];

  for (var i = 0; i < 4; i++) {
    labels.push(String(dados[i].nome));
    valores.push(dados[i].valor_total / 1000000).toFixed(2);
  }

  var outros = 0;
  for (var i = 4; i < dados.length; i++) {
    outros += dados[i].valor_total;
  }

  labels.push("Outros");
  valores.push(outros / 1000000).toFixed(2);

  chartBar = new Chart(document.getElementById("barChart"), {
    type: "bar",
    data: {
      labels: labels,
      datasets: [{
        label: "Importação",
        data: valores,
        backgroundColor: [
          "#3b82f6", // azul
          "#22c55e", // verde
          "#f59e0b", // amarelo
          "#ef4444", // vermelho
          "#8b5cf6", // roxo
        ],

        hoverBackgroundColor: [
          "#60a5fa",
          "#4ade80",
          "#fbbf24",
          "#f87171",
          "#a78bfa",
        ],

        borderWidth: 5,
        borderColor: "#ffffff",

        hoverOffset: 18,
        spacing: 6,
      }]
    },
    options: {
      options: {
        responsive: true,
        maintainAspectRatio: false,

        layout: {
          padding: {
            left: 20,
            right: 20,
            top: 10,
            bottom: 10,
          },
        },

        animation: {
          duration: 1800,
          easing: "easeOutQuart",
        },

        plugins: {
          legend: {
            position: "top",

            labels: {
              color: "#111827",

              font: {
                size: 14,
                weight: "bold",
              },

              padding: 20,
              usePointStyle: true,
            },
          },

          tooltip: {
            backgroundColor: "#ffffff",
            titleColor: "#111827",
            bodyColor: "#374151",

            borderColor: "#e5e7eb",
            borderWidth: 1,

            padding: 12,

            callbacks: {
              label: function (context) {
                return " Valor movimentado: $" + context.parsed.y + "M";
              },
            },
          },
        },

        scales: {
          x: {
            offset: true,

            grid: {
              display: false,
            },

            ticks: {
              color: "#374151",

              font: {
                size: 13,
                weight: "bold",
              },
            },
          },

          y: {
            beginAtZero: true,

            grid: {
              color: "rgba(0,0,0,0.05)",
              drawBorder: false,
            },

            ticks: {
              color: "#6b7280",

              callback: function (value) {
                return "$" + value + "M";
              },
            },
          },
        },
      },
    }
  });
}

function buscarTopSetoresExportacao(anoInicial) {
    fetch(`/setores/buscarTopSetoresExportacao?anoInicial=${anoInicial}`, { cache: 'no-store' })
        .then(function (response) {
            if (response.ok) {
                response.json().then(function (resultado) {
                    plotarGraficoBarExportacao(resultado);
                });
            }
        }).catch(function (error) {
            console.error(`Erro: ${error.message}`);
        });
}

function plotarGraficoBarExportacao(dados) {
  var labels = [];
  var valores = [];

  for (var i = 0; i < 4; i++) {
    labels.push(String(dados[i].nome));
    valores.push(dados[i].valor_total / 1000000).toFixed(2);
  }

  var outros = 0;
  for (var i = 4; i < dados.length; i++) {
    outros += dados[i].valor_total;
  }

  labels.push("Outros");
  valores.push(outros / 1000000).toFixed(2);

  chartBar = new Chart(document.getElementById("barChart2"), {
    type: "bar",
    data: {
      labels: labels,
      datasets: [{
        label: "Exportação",
        data: valores,
        backgroundColor: [
          "#3b82f6", // azul
          "#22c55e", // verde
          "#f59e0b", // amarelo
          "#ef4444", // vermelho
          "#8b5cf6", // roxo
        ],

        hoverBackgroundColor: [
          "#60a5fa",
          "#4ade80",
          "#fbbf24",
          "#f87171",
          "#a78bfa",
        ],

        borderWidth: 5,
        borderColor: "#ffffff",

        hoverOffset: 18,
        spacing: 6,
      }]
    },
    options: {
      options: {
        responsive: true,
        maintainAspectRatio: false,

        layout: {
          padding: {
            left: 20,
            right: 20,
            top: 10,
            bottom: 10,
          },
        },

        animation: {
          duration: 1800,
          easing: "easeOutQuart",
        },

        plugins: {
          legend: {
            position: "top",

            labels: {
              color: "#111827",

              font: {
                size: 14,
                weight: "bold",
              },

              padding: 20,
              usePointStyle: true,
            },
          },

          tooltip: {
            backgroundColor: "#ffffff",
            titleColor: "#111827",
            bodyColor: "#374151",

            borderColor: "#e5e7eb",
            borderWidth: 1,

            padding: 12,

            callbacks: {
              label: function (context) {
                return " Valor movimentado: $" + context.parsed.y + "M";
              },
            },
          },
        },

        scales: {
          x: {
            offset: true,

            grid: {
              display: false,
            },

            ticks: {
              color: "#374151",

              font: {
                size: 13,
                weight: "bold",
              },
            },
          },

          y: {
            beginAtZero: true,

            grid: {
              color: "rgba(0,0,0,0.05)",
              drawBorder: false,
            },

            ticks: {
              color: "#6b7280",

              callback: function (value) {
                return "$" + value + "M";
              },
            },
          },
        },
      },
    }
  });
}

buscarSituacaoMercado(2021);
buscarTopSetores(2021);
buscarTopSetoresExportacao(2021);
buscarTopSetoresImportacao(2021);
