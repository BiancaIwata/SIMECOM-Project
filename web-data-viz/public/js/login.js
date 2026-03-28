function aguardar() {
    div_aguardar.style.display = "block";
}

function finalizarAguardar() {
    div_aguardar.style.display = "none";
}

function mostrarErro(msg) {
    cardErro.style.display = "block";
    mensagem_erro.innerHTML = msg;

    setTimeout(() => {
        cardErro.style.display = "none";
    }, 4000);
}

function entrar() {

    aguardar();

    var email = email_input.value;
    var senha = senha_input.value;

    if (email == "" || senha == "") {
        finalizarAguardar();
        mostrarErro("Preencha todos os campos!");
        return false;
    }

    fetch("/usuarios/auth", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: email,
            password: senha
        })
    })
    .then(function(resposta) {

        if (resposta.ok) {

            resposta.json().then(json => {

                // sessão
                sessionStorage.EMAIL_USUARIO = json.email;
                sessionStorage.NOME_USUARIO = json.name;
                sessionStorage.ID_USUARIO = json.id;

                setTimeout(() => {
                    window.location = "dashboard.html";
                }, 1000);

            });

        } else {
            throw "Erro no login";
        }

        finalizarAguardar();
    })
    .catch(function(erro) {
        console.log(erro);
        finalizarAguardar();
        mostrarErro("Email ou senha inválidos!");
    });

    return false;
}
