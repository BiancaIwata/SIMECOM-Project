// Gráfico de Linhas (Evolução das Importações)
// Usando o Ciano (#06b6d4) como destaque principal
const ctx = document.getElementById("lineChart");

new Chart(ctx, {
  type: "line",

  data: {
    labels: ["2020", "2021", "2022", "2023", "2024", "2025"],

    datasets: [
      {
        label: "Importações (Milhões USD$)",
        data: [180, 300, 220, 80, 210, 215],

        borderColor: "#00c96b",
        backgroundColor: "rgba(0,201,107,0.10)",

        fill: true,
        tension: 0.45,
        borderWidth: 4,

        pointBackgroundColor: "#00c96b",
        pointBorderColor: "#ffffff",
        pointBorderWidth: 2,

        pointRadius: 5,
        pointHoverRadius: 8,
        pointHoverBackgroundColor: "#00c96b",
      },

      {
        label: "Exportações (Milhões USD$)",
        data: [80, 100, 200, 100, 250, 215],

        borderColor: "#ef4444",
        backgroundColor: "rgba(239,68,68,0.10)",

        fill: true,
        tension: 0.45,
        borderWidth: 4,

        pointBackgroundColor: "#ef4444",
        pointBorderColor: "#ffffff",
        pointBorderWidth: 2,

        pointRadius: 5,
        pointHoverRadius: 8,
        pointHoverBackgroundColor: "#ef4444",
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
  },
});

// Gráfico de Rosca (Categorias)
// Cores de alto contraste para facilitar a leitura rápida
new Chart(document.getElementById("pieChart"), {
  type: "doughnut",

  data: {
    labels: [
      "Madeira, Carvão e Cortiça",
      "Plástico e Borracha",
      "Produtos Minerais",
      "Material de Transporte",
      "Outros",
    ],

    datasets: [
      {
        data: [34, 22, 18, 16, 10],

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
      },
    ],
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
  },
});

// Gráfico de Barras (Crescimento)
// Usando o azul escuro para contrastar com o gráfico de linhas ciano acima
new Chart(document.getElementById("barChart"), {
  type: "bar",

  data: {
    labels: [""],

    datasets: [
      {
        label: "Tecnologia",
        data: [185],
        backgroundColor: "#3b82f6",
        hoverBackgroundColor: "#60a5fa",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "Soja",
        data: [260],
        backgroundColor: "#22c55e",
        hoverBackgroundColor: "#4ade80",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "Óleos",
        data: [140],
        backgroundColor: "#f59e0b",
        hoverBackgroundColor: "#fbbf24",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "Carros",
        data: [260],
        backgroundColor: "#ef4444",
        hoverBackgroundColor: "#f87171",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "Outros",
        data: [95],
        backgroundColor: "#8b5cf6",
        hoverBackgroundColor: "#a78bfa",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },
    ],
  },

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
});

new Chart(document.getElementById("barChart2"), {
  type: "bar",

  data: {
    labels: [""],

    datasets: [
      {
        label: "Tecnologia",
        data: [185],
        backgroundColor: "#3b82f6",
        hoverBackgroundColor: "#60a5fa",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "Soja",
        data: [260],
        backgroundColor: "#22c55e",
        hoverBackgroundColor: "#4ade80",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "Óleos",
        data: [140],
        backgroundColor: "#f59e0b",
        hoverBackgroundColor: "#fbbf24",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "Carros",
        data: [260],
        backgroundColor: "#ef4444",
        hoverBackgroundColor: "#f87171",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "Outros",
        data: [95],
        backgroundColor: "#8b5cf6",
        hoverBackgroundColor: "#a78bfa",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },
    ],
  },

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
});
