var express = require("express");
var router = express.Router();


router.post("/challenge", function (req, res) {
    var challenge = req.body.challenge;
    res.json({ "challenge": challenge });
});

module.exports = router;