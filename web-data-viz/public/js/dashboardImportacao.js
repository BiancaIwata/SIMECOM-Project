 new Chart(document.getElementById('lineChart'), {
    type: 'line',
    data: {
      labels: ['2019','2020','2021','2022','2023','2024','2025'],
      datasets: [{
        label: 'Importações',
        data: [180, 300, 220, 80, 210, 215, 220],
        fill: true
      }]
    }
  });

  new Chart(document.getElementById('pieChart'), {
    type: 'doughnut',
    data: {
      labels: ['Madeira, Carvão Vegetal e Cortiça', 'Plástico e Borracha', 'Produtos Minerais', 'Material de Transporte'],
      datasets: [{
        data: [30, 25, 20, 15]
      }]
    }
  });

  new Chart(document.getElementById('barChart'), {
  type: 'bar',
  data: {
    labels: ['2019','2020','2021','2022','2023','2024','2025'],
    datasets: [{
      label: 'Crescimento por Ano (%)',
      data: [120, 200, 150, 80, 70, 110, 130],
      borderRadius: 6
    }]
  }
});