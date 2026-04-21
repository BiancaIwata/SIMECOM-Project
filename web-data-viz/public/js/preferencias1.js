let todasCidades = [];

async function carregarCidades() {
  const res = await fetch(
    "https://servicodados.ibge.gov.br/api/v1/localidades/estados/SP/municipios",
  );
  todasCidades = await res.json();
}

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

function registrar_pref() {
  var id = sessionStorage.ID_USUARIO;
  var uf = document.getElementById("select_estado").value;
  var setor = document.getElementById("select_setor").value;
  var municipio = cidadeInput.value;
  //  var municipio =

  if (id == "" || uf == "" || setor == "" || municipio == "") {
    alert("Campos precisam estar preenchidos");
    return false;
  }

  fetch("/preference/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      id: id,
      uf: uf,
      setor: setor,
      municipio: municipio,
    }),
  }).then(function (resposta) {
    console.log("resposta: ", resposta);

    if (resposta.ok) {
      alert("Preferencia cadastrada com sucesso!");
    } else {
      alert("Erro ao cadastrar preferencia!");
    }
  });

  return false;
}
