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

        borderColor: "#1f7ae0 ",
        backgroundColor: "#1f79e052 ",

        fill: true,
        tension: 0.45,
        borderWidth: 4,

        pointBackgroundColor: "#1f7ae0 ",
        pointBorderColor: "#ffffff",
        pointBorderWidth: 2,

        pointRadius: 5,
        pointHoverRadius: 8,
        pointHoverBackgroundColor: "#1f7ae0 ",
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

// Gráfico de Barras (Crescimento)
// Usando o azul escuro para contrastar com o gráfico de linhas ciano acima
new Chart(document.getElementById("barChart"), {
  type: "bar",

  data: {
    labels: [""],

    datasets: [
      {
        label: "São Paulo",
        data: [185],
        backgroundColor: "#00C853",
        hoverBackgroundColor: "#80E4A9",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "São Paulo",
        data: [260],
        backgroundColor: "#00A8E8",
        hoverBackgroundColor: "#7FD4F4",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "São Paulo",
        data: [140],
        backgroundColor: "#707070",
        hoverBackgroundColor: "#B8B8B8",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "São Paulo",
        data: [260],
        backgroundColor: "#ff2fba",
        hoverBackgroundColor: "#FF97DC",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "Outros",
        data: [95],
        backgroundColor: "#fbbf24",
        hoverBackgroundColor: "#FDE08D",

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
        label: "São Paulo",
        data: [185],
        backgroundColor: "#00C853",
        hoverBackgroundColor: "#80E4A9",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "São Paulo",
        data: [260],
        backgroundColor: "#00A8E8",
        hoverBackgroundColor: "#7FD4F4",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "São Paulo",
        data: [140],
        backgroundColor: "#707070",
        hoverBackgroundColor: "#B8B8B8",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "São Paulo",
        data: [260],
        backgroundColor: "#ff2fba",
        hoverBackgroundColor: "#FF97DC",
        borderRadius: 14,
        borderSkipped: false,

        categoryPercentage: 0.7,
        barPercentage: 0.8,
      },

      {
        label: "Outros",
        data: [95],
        backgroundColor: "#fbbf24",
        hoverBackgroundColor: "#FDE08D",

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

carregarCidades();

const input = document.getElementById("cidadeInput");
const suggestions = document.getElementById("suggestions");

input.addEventListener("input", () => {
  const valor = input.value.toLowerCase();
  suggestions.innerHTML = "";

  if (!valor) return;

  const filtradas = todasCidades
    .filter((c) => c.nome.toLowerCase().includes(valor))
    .slice(0, 10); // limita a 10 resultados

  filtradas.forEach((cidade) => {
    const div = document.createElement("div");
    div.textContent = cidade.nome;
    div.classList.add("suggestion-item");

    div.onclick = () => {
      input.value = cidade.nome;
      suggestions.innerHTML = "";
    };

    suggestions.appendChild(div);
  });
});

let todasCidades = [];

async function carregarCidades() {
  const res = await fetch(
    "https://servicodados.ibge.gov.br/api/v1/localidades/estados/SP/municipios",
  );
  todasCidades = await res.json();
}
