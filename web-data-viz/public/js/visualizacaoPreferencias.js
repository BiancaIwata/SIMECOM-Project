function mostrar() {
    fetch(`/preferencias/${idUsuario}`)
        .then(res => res.json())
        .then(function (resultado) {
            preferencias = resultado;
            gerarTabela();
        })
        .catch(function (erro) {
            console.error("Erro ao buscar preferências:", erro);
        });
}

function gerarTabela() {
    const tbody = document.getElementById("tabela-body");

    tbody.innerHTML = "";

    if (preferencias.length == 0) {
        tabelaBody.innerHTML = `<tr><td class="vazio">Nenhuma preferência cadastrada.</td></tr>`;
        return;
    }

    for (let i = 0; i < preferencias.length; i++) {
        const item = preferencias[i];
        const tr = document.createElement("tr");

        tr.innerHTML = `
      <td>${item.id}</td>
      <td>${item.estado}</td>
      <td>${item.municipio}</td>
      <td>${item.setor}</td>
      <td class="acoes">
        <img class="btn-editar"
        src="./assets/edit_24dp_0B3C5D_FILL0_wght400_GRAD0_opsz24 1.png"
        onclick="abrirModalEditar(${item.id})">
        <img class="btn-excluir"
        src="./assets/delete_24dp_0B3C5D_FILL0_wght400_GRAD0_opsz24 1.png"
        onclick="abrirModalExcluir(${item.id})">
        </td>
        `;

        tbody.appendChild(tr);
    }
}


function abrirModalEditar(id) {
    let item = null;

    for (let i = 0; i < preferencias.length; i++) {
        if (preferencias[i].id == id) {
            item = preferencias[i];
        }
    }

    const modal = document.getElementById("modal");
    modal.innerHTML = "";

    const titulo = document.createElement("h2");
    titulo.textContent = "Editar preferência";

    const campoEstado = document.createElement("label");
    campoEstado.innerHTML = `Estado<br><select id="edit-estado"><option value="">Selecione...</option></select>`;

    const campoMunicipio = document.createElement("label");
    campoMunicipio.innerHTML = `Município<br><select id="edit-municipio"><option value="">Selecione...</option></select>`;

    const campoSetor = document.createElement("label");
    campoSetor.innerHTML = `Setor<br><select id="edit-setor"><option value="">Selecione...</option></select>`;

    const btnCancelar = document.createElement("button");
    btnCancelar.textContent = "Cancelar";
    btnCancelar.onclick = fecharModal;

    const btnSalvar = document.createElement("button");
    btnSalvar.textContent = "Salvar";
    btnSalvar.onclick = function () { salvarEdicao(id); };

    modal.appendChild(titulo);
    modal.appendChild(campoEstado);
    modal.appendChild(campoMunicipio);
    modal.appendChild(campoSetor);
    modal.appendChild(btnCancelar);
    modal.appendChild(btnSalvar);

    document.getElementById("overlay").style.display = "flex";
}

function salvarEdicao(id) {
    for (let i = 0; i < preferencias.length; i++) {
        if (preferencias[i].id === id) {
            preferencias[i].estado = document.getElementById("edit-estado").value;
            preferencias[i].municipio = document.getElementById("edit-municipio").value;
            preferencias[i].setor = document.getElementById("edit-setor").value;
        }
    }

    fecharModal();
    gerarTabela();
}


function abrirModalExcluir(id) {
    let item = null;

    for (let i = 0; i < preferencias.length; i++) {
        if (preferencias[i].id == id) {
            item = preferencias[i];
        }
    }

    const modal = document.getElementById("modal");
    modal.innerHTML = "";

    const titulo = document.createElement("h2");
    titulo.textContent = "Excluir preferência";

    const texto = document.createElement("p");
    texto.textContent = `Tem certeza que deseja excluir Preferência ${id}?`;

    const btnCancelar = document.createElement("button");
    btnCancelar.textContent = "Cancelar";
    btnCancelar.onclick = fecharModal;

    const btnExcluir = document.createElement("button");
    btnExcluir.textContent = "Excluir";
    btnExcluir.onclick = function () { confirmarExclusao(id); };

    modal.appendChild(titulo);
    modal.appendChild(texto);
    modal.appendChild(btnCancelar);
    modal.appendChild(btnExcluir);

    document.getElementById("overlay").style.display = "flex";
}

function confirmarExclusao(id) {
    const novaLista = [];

    for (let i = 0; i < preferencias.length; i++) {
        if (preferencias[i].id !== id) {
            novaLista.push(preferencias[i]);
        }
    }

    preferencias = novaLista;
    fecharModal();
    gerarTabela();
}

function fecharModal() {
    document.getElementById("overlay").style.display = "none";
}

