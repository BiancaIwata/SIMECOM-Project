function redirectAuth(req, res) {
    var slackCode = req.query.code;
    
    if(!slackCode) {
        return res.status(400).send("Código de autorização não encontrado.");
    }

    const bodyParams = new URLSearchParams({
        client_id: process.env.CLIENT_ID,
        client_secret: process.env.CLIENT_SECRET,
        code: slackCode,
        redirect_uri: 'https://barricade-designer-guidance.ngrok-free.dev/slackEvents/redirectAuth'
    });

    fetch('https://slack.com/api/oauth.v2.access', {
        method: post,
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
            return res.status(400).send("Erro.");
        }
        
        const userId = slackData.authed_user.id;
        const accessToken = slackData.access_token;
        console.log(`Usuário autorizado: ${userId}`);

        res.status(200).send("Autenticação com Slack realizada!");
    })
    .catch(function (error) {
        console.error("Erro de comunicação com o Slack: ", error)
        res.status(400).send(error);
    })
}

module.exports = {
    redirectAuth
};