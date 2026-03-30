function cadastrar() {
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
         alert("Campos precisam estar preenchidos");
         return false;
    }

    if (senha != confirmacao) {
         alert("As senhas não estão iguais!");
         return false;
    }

    fetch("/users/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            name: nome,
            surname: sobrenome,
            email: email,
            password: senha
        })
    })
        .then(function (resposta) {
          console.log("resposta: ", resposta);

            if (resposta.ok) {

                alert("Cadastro realizado com sucesso! Redirecionando...");

                    window.location = "login.html";                

            } else {
                alert("Erro ao cadastrar");
            }

        })

    return false;
}
