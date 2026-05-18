// Gráfico de Linhas (Evolução das Importações)
// Usando o Ciano (#06b6d4) como destaque principal
new Chart(document.getElementById('lineChart'), {
  type: 'line',
  data: {
    labels: ['2019', '2020', '2021', '2022', '2023', '2024', '2025'],
    datasets: [{
      label: 'Importações (Milhões US$)',
      data: [180, 300, 220, 80, 210, 215, 220],
      borderColor: '#06b6d4', // Ciano da sua imagem
      backgroundColor: 'rgba(6, 182, 212, 0.2)', // Fundo suave
      fill: true,
      tension: 0.4,
      borderWidth: 3
    }]
  },
  options: {
    responsive: true,
    plugins: {
      tooltip: {
        callbacks: {
          label: function(context) {
            return ' ' + context.dataset.label + ': $' + context.parsed.y + 'M';
          }
        }
      }
    }
  }
});

// Gráfico de Rosca (Categorias)
// Cores de alto contraste para facilitar a leitura rápida
new Chart(document.getElementById('pieChart'), {
  type: 'doughnut',
  data: {
    labels: ['Madeira, Carvão e Cortiça', 'Plástico e Borracha', 'Produtos Minerais', 'Material de Transporte'],
    datasets: [{
      data: [30, 25, 20, 15],
      backgroundColor: [
        '#06b6d4', // Ciano
        '#1e3a8a', // Azul Escuro (Contraste)
        '#7c3aed', // Roxo (Para diferenciar bem os setores)
        '#f43f5e'  // Rosa/Vermelho (Destaque visual)
      ],
      borderWidth: 2,
      borderColor: '#ffffff'
    }]
  },
  options: {
    responsive: true,
    plugins: {
      tooltip: {
        callbacks: {
          label: function(context) {
            return ' ' + context.label + ': ' + context.parsed + '% da participação';
          }
        }
      }
    }
  }
});

// Gráfico de Barras (Crescimento)
// Usando o azul escuro para contrastar com o gráfico de linhas ciano acima
new Chart(document.getElementById('barChart'), {
  type: 'bar',
  data: {
    labels: ['2019', '2020', '2021', '2022', '2023', '2024', '2025'],
    datasets: [{
      label: 'Variação Anual (%)',
      data: [120, 200, 150, 80, 70, 110, 130],
      backgroundColor: '#1e3a8a', // Azul Escuro
      borderRadius: 6,
      hoverBackgroundColor: '#06b6d4' // Muda para ciano no hover para interatividade
    }]
  },
  options: {
    responsive: true,
    plugins: {
      tooltip: {
        callbacks: {
          label: function(context) {
            return ' Variação: ' + context.parsed.y + '% em relação ao período anterior';
          }
        }
      }
    }
  }
});