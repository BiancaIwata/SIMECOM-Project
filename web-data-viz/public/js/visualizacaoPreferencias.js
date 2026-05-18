var idUsuario = JSON.parse(sessionStorage.getItem("ID_USUARIO"));

var preferencias = [];

function mostrar() {
    fetch(`/preferencias/${idUsuario}`)
        .then(res => {
            console.log(res)

            if (res.status == 200) {
                res.json().then(json => {
                    console.log(json);
                    console.log(JSON.stringify(json));

                    preferencias = json;
                    gerarTabela();
                });
            } else if (res.status == 204) {
                gerarTabela();
            }
        })
        .catch(function (erro) {
            console.error("Erro ao buscar preferências:", erro);
        });
}

function gerarTabela() {
    const tbody = document.getElementById("tabela-body");

    tbody.innerHTML = "";

    console.log(preferencias)

    if (preferencias.length == 0) {
        tbody.innerHTML = `<tr><td colspan="4" class="vazio">Nenhuma preferência cadastrada.</td></tr>`;
        return;
    }


    for (let i = 0; i < preferencias.length && i <= 3; i++) {
        const item = preferencias[i];
        const tr = document.createElement("tr");

        tr.innerHTML = `
      <td>${i + 1}</td>
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

let municipios = [];

function carregarMunicipios(estado) {
    fetch(
        `https://servicodados.ibge.gov.br/api/v1/localidades/estados/${estado}/municipios`,
    )
        .then(res => {
            res.json().then(data => {
                municipios = data;
                let select = document.getElementById("edit-municipio");
                select.innerHTML = "";

                let optionDefault = document.createElement("option");
                optionDefault.innerHTML = "Selecione...";
                select.appendChild(optionDefault);

                for (let i = 0; i < municipios.length; i++) {
                    let option = document.createElement("option");

                    option.setAttribute("value", municipios[i].nome);
                    option.innerHTML = municipios[i].nome;
                    select.appendChild(option);
                }
                console.log(select)
            })
        });

}

function abrirModalEditar(id) {
    console.log()
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
    campoEstado.innerHTML = `Estado<br><select onchange="carregarMunicipios(this.value)" id="edit-estado">
    <option value="">Selecione...</option>
     <optgroup label="Norte">
        <option value="AC">Acre</option>
        <option value="AP">Amapá</option>
        <option value="AM">Amazonas</option>
        <option value="PA">Pará</option>
        <option value="RO">Rondônia</option>
        <option value="RR">Roraima</option>
        <option value="TO">Tocantins</option>
    </optgroup>

    <optgroup label="Nordeste">
        <option value="AL">Alagoas</option>
        <option value="BA">Bahia</option>
        <option value="CE">Ceará</option>
        <option value="MA">Maranhão</option>
        <option value="PB">Paraíba</option>
        <option value="PE">Pernambuco</option>
        <option value="PI">Piauí</option>
        <option value="RN">Rio Grande do Norte</option>
        <option value="SE">Sergipe</option>
    </optgroup>

    <optgroup label="Centro-Oeste">
        <option value="DF">Distrito Federal</option>
        <option value="GO">Goiás</option>
        <option value="MT">Mato Grosso</option>
        <option value="MS">Mato Grosso do Sul</option>
    </optgroup>

    <optgroup label="Sudeste">
        <option value="ES">Espírito Santo</option>
        <option value="MG">Minas Gerais</option>
        <option value="RJ">Rio de Janeiro</option>
        <option value="SP">São Paulo</option>
    </optgroup>

    <optgroup label="Sul">
        <option value="PR">Paraná</option>
        <option value="RS">Rio Grande do Sul</option>
        <option value="SC">Santa Catarina</option>
    </optgroup>
</select>`;

    const campoMunicipio = document.createElement("label");

    campoMunicipio.innerHTML = `   
        Município<br>
        <select id="edit-municipio">
            <option value="">Selecione...</option>
        </select>`;

    const campoSetor = document.createElement("label");
    campoSetor.innerHTML = `Setor<br>
        <select id="edit-setor">
            <option value="Animais vivos e produtos do reino animal">Animais Vivos e Produtos do Reino Animal</option>
            <option value="Produtos do Reino Vegetal">Produtos do Reino Vegetal</option>
            <option value="Gorduras e Óleos Animais ou Vegetais">Gorduras e Óleos Animais ou Vegetais</option>
            <option value="Produtos das Indústrias Alimentares; Bebidas e Tabaco">Produtos das Indústrias Alimentares; Bebidas e Tabaco</option>
            <option value="Produtos Minerais">Produtos Minerais</option>
            <option value="Produtos das Indústrias Químicas">Produtos das Indústrias Químicas</option>
            <option value="Plástico e Borracha">Plástico e Borracha</option>
            <option value="Peles, Couro e Obras">Peles, Couro e Obras</option>
            <option value="Madeira, Carvão Vegetal e Cortiça">Madeira, Carvão Vegetal e Cortiça</option>
            <option value="Pasta de Madeira, Papel e Cartão">Pasta de Madeira, Papel e Cartão</option>
            <option value="Materiais Têxteis e Suas Obras">Materiais Têxteis e Suas Obras</option>
            <option value="Calçados, Chapéus e Semelhantes">Calçados, Chapéus e Semelhantes</option>
            <option value="Obras de Pedra, Cerâmica e Vidro">Obras de Pedra, Cerâmica e Vidro</option>
            <option value="Pérolas, Pedras Preciosas e Metais Preciosos">Pérolas, Pedras Preciosas e Metais Preciosos</option>
            <option value="Metais Comuns e Suas Obras">Metais Comuns e Suas Obras</option>
            <option value="Máquina e Aparelhos, Material Elétrico">Máquina e Aparelhos, Material Elétrico</option>
            <option value="Material de Transporte">Material de Transporte</option>
            <option value="Instrumento e Aparelhos de Óptica, Cinematografia e Fotografia">Instrumento e Aparelhos de Óptica, Cinematografia e Fotografia</option>
            <option value="Armas e Munições">Armas e Munições</option>
            <option value="Mercadorias e Produtos Diversos">Mercadorias e Produtos Diversos</option>
            <option value="Objetos de Arte e Antiguidades">Objetos de Arte e Antiguidades</option>
        </select>`;

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

    if (estado == '' || municipio == '' || setor == '') {
        alert('Preencha todos os campos para editar!');
        return
    }

    fetch(`/preferencias/${id}`, {
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
            console.log(res)
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

let index = 0;

function abrirModalExcluir(id) {
    let item = null;

    for (let i = 0; i < preferencias.length; i++) {
        if (preferencias[i].id == id) {
            item = preferencias[i];
            index = i + 1;
        }
    }

    const modal = document.getElementById("modal");
    modal.innerHTML = "";

    const titulo = document.createElement("h2");
    titulo.textContent = "Excluir preferência";

    const texto = document.createElement("p");
    texto.textContent = `Tem certeza que deseja excluir Preferência ${index}?`;

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
    fetch(`/preferencias/${id}`, {
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