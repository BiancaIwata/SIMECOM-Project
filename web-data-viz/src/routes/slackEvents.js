var express = require("express");
var router = express.Router();

var slackController = require("../controllers/slackController");

router.post("/challenge", function (req, res) {
    var challenge = req.body.challenge;
    res.json({ "challenge": challenge });
});

router.all("/redirectAuth", function (req, res) {
    slackController.redirectAuth(req, res);
});

module.exports = router;