window.onload = function () {
  fetch("/forum/forumGetter")
    .then((res) => {
      if (!res.ok) throw new Error("Erro ao buscar posts");
      return res.json();
    })
    .then((posts) => {
      const container = document.getElementById("allPosts");
      container.innerHTML = "";

      for (let i = 0; i < posts.length; i++) {
        const post = posts[i];

        const card = document.createElement("div");
        card.className = "card";

        card.innerHTML = `
        <span class="icon">👤</span>
        <div>
        <strong>${post.titulo}</strong><br>
        <small>por ${post.autor} - 
        ${new Date(post.created_at).toLocaleString()}</small>
        <p>${post.conteudo}</p>
        <button class="btn_givalike" onclick="giva_like(${post.id})">Like!</button>
        </div>
        `;
        container.appendChild(card);
      }
    })
    .catch((err) => {
      console.error(err);
      alert("Erro ao carregar posts");
    });

  fetch("/forum/forumTop5")
    .then((res) => {
      if (!res.ok) throw new Error("Erro ao buscar top 5");
      return res.json();
    })
    .then((posts) => {
      const container = document.getElementById("topiccontent");
      container.innerHTML = "";

      for (let i = 0; i < posts.length; i++) {
        const post = posts[i];

        const li = document.createElement("li");

        li.innerHTML = `
        <div class="topicos-list">
        <strong>${post.titulo}</strong>
        <p>${post.conteudo}</p>
        <p>👍 ${post.total_likes}</p>
        </div>
         `;

        container.appendChild(li);
      }
    })
    .catch((err) => {
      console.error(err);
      alert("Erro ao carregar top posts");
    });
};

function postar_comentario() {
  const title = document.getElementById("theme").value;
  const content = document.getElementById("comment_content").value;
  const id = sessionStorage.ID_USUARIO;

  if (!title || !content) {
    alert("Preencha todos os campos");
    return;
  }

  fetch("/forum/postComment", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      title: title,
      content: content,
      id: id,
    }),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Erro ao postar");
      return res.json();
    })
    .then(() => {
      alert("Post criado com sucesso!");
      document.getElementById("theme").value = "";
      document.getElementById("comment_content").value = "";
    })
    .catch((err) => {
      console.error(err);
      alert("Erro ao postar comentário");
    });
}

function giva_like(postId) {
  const usuarioId = sessionStorage.ID_USUARIO;

  if (!usuarioId) {
    alert("Você precisa estar logado");
    return;
  }

  fetch("/forum/givaLike", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      post_id: postId,
      usuario_id: usuarioId,
    }),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Erro ao dar like");
      return res.json();
    })
    .then(() => {
      alert("Like registrado!");
    })
    .catch((err) => {
      console.error(err);
      alert("Erro ao dar like");
    });
}
