window.onload = function () {
  fetch("/admin/admin")
    .then((res) => {
      if (!res.ok) throw new Error("Erro ao buscar usuários");
      return res.json();
    })
    .then((usuarios) => {
      const container = document.getElementById("allUsers");
      container.innerHTML = "";

      usuarios.forEach((user, i) => {
        const card = document.createElement("div");
        card.className = "card";
        card.id = `card_user${user.id}`;

        card.innerHTML = `
    <div class="user-card">
      <div class="info">
        <div class="campo">
          <span class="label">Nome</span><br>
          <span class="valor">${user.nome} ${user.sobrenome}</span>
        </div>

        <div class="campo">
          <span class="label">E-mail atual</span><br>
          <span class="valor">${user.email}</span>
        </div>
      </div>
      <div>
        <button class="btn-delete" onclick="deletarUsuario(${user.id})">
          Excluir Usuário
        </button>
      </div>
    </div>
  `;

        container.appendChild(card);
      });
    })
    .catch((err) => {
      console.error(err);
      alert("Erro ao carregar usuários");
    });
};

function deletarUsuario(id) {
  if (!confirm("Tem certeza que deseja excluir este usuário?")) return;

  fetch("/admin/deletarUsuario", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ id }),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Erro ao deletar usuário");

      const card = document.querySelector(`#card_user${id}`);
      if (card) card.remove();
    })
    .catch((err) => {
      console.error(err);
      alert("Erro ao excluir usuário");
    });
}
