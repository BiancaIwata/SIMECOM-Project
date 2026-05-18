new Chart(document.getElementById("graficoLinha"), {
  type: "line",
  data: {
    labels: ["2019", "2020", "2021", "2022", "2023", "2024"],
    datasets: [
      {
        label: "Exportações",
        data: [300, 350, 250, 220, 300, 320],
        borderColor: "#1e3a8a",
        fill: true,
        tension: 0.4,
        borderWidth: 3,
      },
      {
        label: "Importações",
        data: [150, 180, 120, 100, 160, 180],
        borderColor: "#06b6d4",
        fill: true,
        tension: 0.4,
        borderWidth: 3,
      },
    ],
  },
});

new Chart(document.getElementById("graficoPizza"), {
  type: "doughnut",
  data: {
    labels: ["Exportações", "Importações"],
    datasets: [
      {
        data: [56.3, 43.7],
        // Mantendo a lógica de Forte vs Fraco/Vivo
        backgroundColor: ["#1e3a8a", "#06b6d4"],
        borderWidth: 0, // Remove bordas para um look mais limpo
      },
    ],
  },
});
