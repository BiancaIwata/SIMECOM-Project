var express = require("express");
var router = express.Router();

var usersController = require("../controllers/usersController");

router.post("/cadastrar", function (req, res) {
    usersController.cadastrar(req, res);
});

module.exports = router;