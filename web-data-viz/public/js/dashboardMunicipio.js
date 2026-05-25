  var chartLine = null;
  var chartBar = null;
  var chartBar2 = null;

  var municipioSelecionado = "";

  document.getElementById("select-periodo").addEventListener("change", function () { 
    atualizarDashboard(); 
  });

  window.addEventListener("load", function () {

      municipioSelecionado =
          document.getElementById("cidadeInput").value;

      atualizarDashboard();
  });

  document.getElementById("cidadeInput")
  .addEventListener("input", function () {

      municipioSelecionado = this.value;

      console.log("MUNICÍPIO SELECIONADO:", municipioSelecionado);

      atualizarDashboard();
  });

  function atualizarDashboard() {

    var anoInicial = Number(document.getElementById("select-periodo").value);

    if (!anoInicial || !municipioSelecionado) {
      return;
    }

    var anoFinal = anoInicial + 5;

    document.getElementById("periodoSelecionado").innerHTML =`${anoInicial} - ${anoFinal}`;

    buscarSituacaoAnual(anoInicial, municipioSelecionado);

    buscarTopMunicipios(anoInicial);

    buscarTopMunicipiosImportacao(anoInicial);

    buscarTopMunicipiosExportacao(anoInicial);
  }

  function buscarSituacaoAnual(anoInicial, municipio) {

    fetch(
      `/municipios/buscarSituacaoAnual?anoInicial=${anoInicial}&municipio=${encodeURIComponent(municipio)}`,
      {
        cache: "no-store"
      }
    )

      .then(function (response) {

        if (response.ok) {

          response.json().then(function (resultado) {

            console.log(resultado);

            plotarGraficoLinhas(resultado);
          });

        } else {

          console.error("Nenhum dado encontrado.");
        }
      })

      .catch(function (error) {

        console.error(error);
      });
  }

  function plotarGraficoLinhas(dados) {

    if (!dados || dados.length === 0) {
      console.warn("Sem dados para o gráfico");
      return;
  }

    console.log("DADOS DO GRÁFICO:", dados);

    if (chartLine) {
      chartLine.destroy();
    }

    var labels = [];
    var importacoes = [];
    var exportacoes = [];

    for (var i = 0; i < dados.length; i++) {

      labels.push(String(dados[i].ano));

      importacoes.push(
        dados[i].importacoes_milhoes_usd
      );

      exportacoes.push(
        dados[i].exportacoes_milhoes_usd
      );
    }

    chartLine = new Chart(
      document.getElementById("lineChart"),
      {
        type: "line",

        data: {

          labels: labels,

          datasets: [

            {
              label: "Importações (Milhões USD$)",

              data: importacoes,

              borderColor: "#0a9317",

              backgroundColor:
                "rgba(34,197,94,0.12)",

              fill: true,

              tension: 0.45,

              borderWidth: 4,

              pointRadius: 6,

              pointHoverRadius: 8,

              pointBackgroundColor: "#0a9317",

              pointBorderColor: "#fff",

              pointBorderWidth: 2,
            },

            {
              label: "Exportações (Milhões USD$)",

              data: exportacoes,

              borderColor: "#0036c9",

              backgroundColor:
                "rgba(0, 77, 201, 0.1)",

              fill: true,

              tension: 0.45,

              borderWidth: 4,

              pointRadius: 6,

              pointHoverRadius: 8,

              pointBackgroundColor: "#0036c9",

              pointBorderColor: "#fff",

              pointBorderWidth: 2,
            }
          ]
        },

        options: {

          responsive: true,

          maintainAspectRatio: false,

          plugins: {

            legend: {

              position: "top",

              labels: {

                usePointStyle: true,

                pointStyle: "circle",

                padding: 30,

                color: "#1f2937",

                font: {
                  size: 14,
                  weight: "bold",
                }
              }
            }
          },

          scales: {

            x: {

              grid: {
                color: "rgba(0,0,0,0.05)",
                drawBorder: false,
              },

              ticks: {
                color: "#6b7280",
              }
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
                }
              }
            }
          }
        }
      }
    );
  }


  function buscarTopMunicipios(anoInicial) {

    fetch(
      `/municipios/buscarTopMunicipios?anoInicial=${anoInicial}`,
      {
        cache: "no-store"
      }
    )

      .then(function (response) {

        if (response.ok) {

          response.json().then(function (resultado) {

            preencherTabela(resultado);
          });
        }
      })

      .catch(function (error) {

        console.error(error);
      });
  }

  function preencherTabela(dados) {

    var tbody = document.querySelector("table tbody");

    tbody.innerHTML = "";

    for (var i = 0; i < 10 && i < dados.length; i++) {

      tbody.innerHTML += `
        <tr>
            <td>${i + 1}</td>
            <td>${dados[i].municipio}</td>
        </tr>
      `;
    }
  }

  function buscarTopMunicipiosImportacao(anoInicial) {

    fetch(
      `/municipios/buscarTopMunicipiosImportacao?anoInicial=${anoInicial}`,
      {
        cache: "no-store"
      }
    )

      .then(function (response) {

        if (response.ok) {

          response.json().then(function (resultado) {

            plotarGraficoBarImportacao(resultado);
          });
        }
      })

      .catch(function (error) {

        console.error(error);
      });
  }

  function plotarGraficoBarImportacao(dados) {

    chartBar = criarGraficoBarra(
      "barChart",
      dados,
      chartBar
    );
  }

  function buscarTopMunicipiosExportacao(anoInicial) {

    fetch(
      `/municipios/buscarTopMunicipiosExportacao?anoInicial=${anoInicial}`,
      {
        cache: "no-store"
      }
    )

      .then(function (response) {

        if (response.ok) {

          response.json().then(function (resultado) {

            plotarGraficoBarExportacao(resultado);
          });
        }
      })

      .catch(function (error) {

        console.error(error);
      });
  }

  function plotarGraficoBarExportacao(dados) {

    chartBar2 = criarGraficoBarra(
      "barChart2",
      dados,
      chartBar2
    );
  }


  function criarGraficoBarra(
    idCanvas,
    dados,
    chartAtual
  ) {

    if (chartAtual) {
      chartAtual.destroy();
    }

    var labels = [];
    var valores = [];

    for (var i = 0; i < 4 && i < dados.length; i++) {

      labels.push(
        dados[i].municipio
      );

      valores.push(
        (dados[i].valor_total / 1000000).toFixed(0)
      );
    }

    var outros = 0;

    for (var i = 4; i < dados.length; i++) {

      outros += dados[i].valor_total;
    }

    labels.push("Outros");

    valores.push(
      (outros / 1000000).toFixed(0)
    );

    return new Chart(
      document.getElementById(idCanvas),
      {
        type: "bar",

        data: {

          labels: labels,

          datasets: [
            {
              data: valores,

              backgroundColor: [
                "#00C853",
                "#ff2fba",
                "#00A8E8",
                "#707070",
                "#fbbf24",
              ],

              hoverBackgroundColor: [
                "#80E4A9",
                "#FF97DC",
                "#7FD4F4",
                "#B8B8B8",
                "#FDE08D",
              ],

              borderRadius: 18,

              borderSkipped: false,

              barThickness: 65,
            }
          ]
        },

        options: {

          responsive: true,

          maintainAspectRatio: false,

          plugins: {

            legend: {

              display: true,

              position: "top",

              labels: {

                generateLabels(chart) {

                  return labels.map(
                    (label, i) => ({
                      text: label,

                      fillStyle:
                        chart.data.datasets[0]
                          .backgroundColor[i],

                      strokeStyle:
                        chart.data.datasets[0]
                          .backgroundColor[i],

                      hidden: false,

                      index: i,

                      pointStyle: "circle"
                    })
                  );
                },

                usePointStyle: true,

                color: "#111827",

                padding: 25,

                font: {
                  size: 14,
                  weight: "bold",
                }
              }
            },

            tooltip: {

              callbacks: {

                label: function (context) {

                  return (
                    "$" +
                    context.parsed.y +
                    "M"
                  );
                }
              }
            }
          },

          scales: {

            x: {

              grid: {
                display: false,
              },

              ticks: {
                display: false
              }
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
                }
              }
            }
          }
        }
      }
    );
  }

  document.getElementById("select-periodo").value = "2021";

  municipioSelecionado = "São Paulo";

  document.getElementById("cidadeInput").value = municipioSelecionado;

  atualizarDashboard();