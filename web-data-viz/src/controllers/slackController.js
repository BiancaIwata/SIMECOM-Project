var slackModel = require("../models/slackModel");

function redirectAuth(req, res) {
    var slackCode = req.query.code;
    var simecomUserId = req.query.state;
    
    if(!slackCode) {
        return res.status(400).send("Código de autorização não encontrado.");
    }

    if(!simecomUserId) {
        return res.status(400).send("ID do usuário Simecom (state) não encontrado.");
    }

    const bodyParams = new URLSearchParams({
        client_id: process.env.CLIENT_ID,
        client_secret: process.env.CLIENT_SECRET,
        code: slackCode,
        redirect_uri: 'https://barricade-designer-guidance.ngrok-free.dev/slackEvents/redirectAuth'
    });

    fetch('https://slack.com/api/oauth.v2.access', {
        method: "POST",
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: bodyParams.toString()
    })
    .then(function (slackResponse) {
        return slackResponse.json();
    })
    .then(function (slackData) {
        if(!slackData.ok) {
            console.error("Erro retornado pelo Slack: ", slackData.error);
            return res.status(400).send("Erro na autenticação com o Slack.");
        }
        
        const userId = slackData.authed_user.id;
        const accessToken = slackData.access_token;

        console.log(`Usuário do Slack autorizado: ${userId} para o usuário Simecom: ${simecomUserId}`);

        slackModel
            .atualizar(accessToken, userId, simecomUserId)
            .then(function () {
              res.redirect("http://simecom.duckdns.org/dashboardSetor.html");
            })
            .catch(function (erro) {
              console.log(erro);
              console.log("\nHouve um erro ao realizar a atualização!");
              res.status(500).json(erro.sqlMessage);
            });
    })
    .catch(function (error) {
        console.error("Erro no processo de autenticação/banco:   ", error)
        res.status(400).send(error);
    })
}

module.exports = {
    redirectAuth
};