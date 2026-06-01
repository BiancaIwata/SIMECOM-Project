var express = require("express");
var router = express.Router();

var relatorioController = require("../controllers/relatorioController");

router.post("/", function (req, res) {
  relatorioController.gerar(req, res);
});

module.exports = router;
