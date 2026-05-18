// Gráfico de Linhas (Evolução do valor exportado)
// Utilizando as cores e estilos do print enviado
new Chart(document.getElementById('lineChart'), {
  type: 'line',
  data: {
    labels: ['2019', '2020', '2021', '2022', '2023', '2024', '2025'],
    datasets: [{
      label: 'Exportações (Milhões US$)',
      data: [180, 300, 220, 80, 210, 215, 220],
      borderColor: '#1e3a8a', // Azul escuro da sua imagem
      backgroundColor: 'rgba(32, 30, 138, 0.2)', // Transparência da sua imagem
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
            // Relacionando o gráfico com contexto financeiro
            return ' ' + context.dataset.label + ': $' + context.parsed.y + 'M';
          }
        }
      }
    }
  }
});

// Gráfico de Rosca (Top Setores)
// Adicionando cores de contraste baseadas na sua paleta de azul e ciano
new Chart(document.getElementById('pieChart'), {
  type: 'doughnut',
  data: {
    labels: ['Madeira, Carvão e Cortiça', 'Plástico e Borracha', 'Produtos Minerais', 'Material de Transporte'],
    datasets: [{
      data: [20, 35, 40, 10],
      backgroundColor: [
        '#1e3a8a', // Azul principal (Exportações)
        '#06b6d4', // Ciano (Importações da sua imagem, usado aqui para contraste)
        '#f59e0b', // Âmbar/Laranja (Para dar um contraste forte e chamar atenção)
        '#10b981'  // Verde esmeralda (Traz leveza e diferencia os setores)
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
            // Detalhando que esse valor é em porcentagem
            return ' ' + context.label + ': ' + context.parsed + '%';
          }
        }
      }
    }
  }
});

// Gráfico de Barras (Crescimento)
new Chart(document.getElementById('barChart'), {
  type: 'bar',
  data: {
    labels: ['2019', '2020', '2021', '2022', '2023', '2024', '2025'],
    datasets: [{
      label: 'Crescimento por Ano (%)',
      data: [120, 200, 150, 80, 70, 110, 130],
      backgroundColor: 'rgba(6, 182, 212, 0.8)', // Usando o ciano da sua imagem com menos transparência
      borderColor: '#06b6d4',
      borderWidth: 2,
      borderRadius: 6
    }]
  },
  options: {
    responsive: true,
    plugins: {
      tooltip: {
        callbacks: {
          label: function(context) {
            return ' Crescimento: ' + context.parsed.y + '% em relação ao ano base';
          }
        }
      }
    }
  }
});