var idUsuario = JSON.parse(sessionStorage.getItem("usuario_id"));

var preferencias = [];

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
        tbody.innerHTML = `<tr><td class="vazio">Nenhuma preferência cadastrada.</td></tr>`;
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
    var estado = document.getElementById("edit-estado").value;
    var municipio = document.getElementById("edit-municipio").value;
    var setor = document.getElementById("edit-setor").value;

    fetch("/preferencias/" + id, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            usuario_id: idUsuario,
            estado: estado,
            municipio: municipio,
            setor: setor
        })
    })
        .then(function (res) {
            return res.json();
        })
        .then(function () {
            fecharModal();
            mostrar();
        })
        .catch(function (erro) {
            console.error("Erro ao editar preferência:", erro);
        });
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
    fetch("/preferencias/" + id, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ usuario_id: idUsuario })
    }).then(function (res) {
        return res.json();
    }).then(function () {
        fecharModal();
        mostrar();
    }).catch(function (erro) {
        console.error("Erro ao excluir preferência:", erro);
    });
    fecharModal();
    gerarTabela();
}

function fecharModal() {
    document.getElementById("overlay").style.display = "none";
}

mostrar();