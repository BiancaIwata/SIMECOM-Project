var express = require("express");
var router = express.Router();

var preferenciasController = require("../controllers/preferenciasController");

router.get("/:id", function (req, res) {
    preferenciasController.mostrar(req, res);
});

router.post("/", function (req, res) {
    preferenciasController.atualizar(req, res);
});

router.post("/", function (req, res) {
    preferenciasController.criar(req, res);
});

router.delete("/", function (req, res) {
    preferenciasController.deletar(req, res);
});

module.exports = router;