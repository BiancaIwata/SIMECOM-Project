function entrar() {

    var email = email_input.value;
    var senha = senha_input.value;

    if (email == "" || senha == "") {
        alert('Preencha todos os campos');
        return;
    }

    fetch("/users/auth", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: email,   
            password: senha
        })
    })
    .then(res => {
        if (!res.ok) throw new Error("Erro login");

        return res.json();
    })
    .then(json => {

        sessionStorage.ID_USUARIO = json.id;

        window.location.href = "./dashboard/dashboard.html";

    })
    .catch(err => {
        console.log(err);
        alert("Erro no login");
    });

}
