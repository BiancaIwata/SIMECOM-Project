window.onload = function () {
  //pegando as informações quando a pagina carrega
  var id = sessionStorage.ID_USUARIO;

  fetch("/users/getter", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      id: id,
    }),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Erro login");

      return res.json();
    })
    .then((json) => {
      senha_configurar.textContent = json.senha;
      email_configurar.textContent = json.email;
      nome_configurar.textContent = json.nome;
      usuario_nome.textContent = "Olá, " + json.nome + "!";
    })
    .catch((err) => {
      console.log(err);
      alert("Erro no login");
    });
};

//--------------------preparação para editar-------------------

function editar() {
  var password = senha_configurar.textContent;
  var email = email_configurar.textContent;
  var name = nome_configurar.textContent;

  senha_input.placeholder = password;
  email_input.placeholder = email;
  nome_input.placeholder = name;

  senha_configurar.style.display = "none";
  email_configurar.style.display = "none";
  nome_configurar.style.display = "none";
  btn_confirmar_info.style.display = "inline";

  senha_input.style.display = "inline";
  email_input.style.display = "inline";
  nome_input.style.display = "inline";
}

//--------------------Confirmação e alteração do bd-------------------

function confirminfo() {
  var id = sessionStorage.ID_USUARIO;
  var password = senha_input.value;
  var email = email_input.value;
  var name = nome_input.value;

  if (name == "" || email == "" || password == "" || id == "") {
    alert("Campos precisam estar preenchidos");
    return false;
  }

  fetch("/users/setter", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      name: name,
      id: id,
      email: email,
      password: password,
    }),
  }).then(function (resposta) {
    console.log("resposta: ", resposta);

    if (resposta.ok) {
      alert("Alteração realizado com sucesso!");
      senha_configurar.textContent = password;
      email_configurar.textContent = email;
      nome_configurar.textContent = name;
    } else {
      alert("Erro ao cadastrar");
    }
  });

  senha_configurar.style.display = "inline";
  email_configurar.style.display = "inline";
  nome_configurar.style.display = "inline";

  senha_input.style.display = "none";
  email_input.style.display = "none";
  nome_input.style.display = "none";

  btn_confirmar_info.style.display = "none";

  return false;
}

//Processo de deletar a conta

function check_del() {
  btn_confirmar_del.style.display = "inline";
}

function confirm_del() {
  var id = sessionStorage.ID_USUARIO;

  if (id == "") {
    alert("Id não encontrado...");
    return false;
  }

  fetch("/users/deleter", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      id: id,
    }),
  }).then(function (resposta) {
    console.log("resposta: ", resposta);

    if (resposta.ok) {
      alert("Alteração realizado com sucesso!");
      sessionStorage.ID_USUARIO = "";
      window.location.href = "./index.html";
    } else {
      alert("Erro ao cadastrar");
    }
  });
  return false;
}
