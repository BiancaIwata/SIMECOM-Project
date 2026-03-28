function cadastrar() {

    aguardar(); 
    var nome = nome_input.value;
    var sobrenome = sobrenome_input.value;
    var email = email_input.value;
    var senha = senha_input.value;
    var confirmacao = confirmacao_input.value;

    if (
        nome == "" ||
        sobrenome == "" ||
        email == "" ||
        senha == "" ||
        confirmacao == ""
    ) {
        finalizarAguardar();
        mostrarErro("Preencha todos os campos!");
        return false;
    }

    if (senha != confirmacao) {
        finalizarAguardar();
        mostrarErro("As senhas não coincidem!");
        return false;
    }

    fetch("/usuarios/cadastrar", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            nameServer: nome,
            surnameServer: sobrenome,
            emailServer: email,
            passwordServer: senha
        })
    })
    .then(function(resposta) {

        if (resposta.ok) {

            mostrarErro("Cadastro realizado com sucesso! Redirecionando...");

            setTimeout(() => {
                window.location = "login.html";
            }, 2000);

        } else {
            throw "Erro ao cadastrar";
        }

        finalizarAguardar();
    })
    .catch(function(erro) {
        console.log(erro);
        finalizarAguardar();
        mostrarErro("Erro ao cadastrar!");
    });

    return false;
}