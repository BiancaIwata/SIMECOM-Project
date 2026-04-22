// gráfico 1:(pizza)
const ctxSetores = document.getElementById('graficoSetores').getContext('2d');
new Chart(ctxSetores, {
    type: 'doughnut',
    data: {
        labels: ['Agropecuário', 'Automotivo', 'Químico', 'Farmacêutico'],
        datasets: [{
            data: [25, 25, 10, 40],
            // Cores baseadas na sua segunda imagem
            backgroundColor: ['#8b5cf6', '#ff8a8a', '#22d3ee', '#fbbf24'], 
            borderWidth: 0
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '60%', // tamanho do circulo central
        plugins: {
            legend: {
                position: 'top', // legenda no topo
                labels: {
                    usePointStyle: true, // bolinhas na legenda em vez de quadrados
                    boxWidth: 8,
                    color: '#64748b',
                    font: { size: 11 }
                }
            }
        }
    }
});

// gráfico 2(barras)
const ctxBarras = document.getElementById('graficoBarras').getContext('2d');
new Chart(ctxBarras, {
    type: 'bar',
    data: {
        labels: ['2012', '2013', '2014', '2015', '2016'],
        datasets: [
            {
                label: 'Exportação',
                data: [20, 28, 42, 31, 27],
                backgroundColor: '#0b2a59',
                borderRadius: 4
            },
            {
                label: 'Importação',
                data: [27, 27, 43, 53, 0],
                backgroundColor: '#3b82f6', 
                borderRadius: 4
            }
        ]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            y: {
                beginAtZero: true,
                max: 60,
                ticks: { color: '#94a3b8', font: { size: 11 } },
                grid: { color: '#f1f5f9' } 
            },
            x: {
                ticks: { color: '#94a3b8', font: { size: 11 } },
                grid: { display: false } 
            }
        },
        plugins: {
            legend: {
                position: 'top',
                align: 'end',
                labels: {
                    usePointStyle: true,
                    boxWidth: 8,
                    color: '#64748b',
                    font: { size: 11 }
                }
            }
        }
    }
});