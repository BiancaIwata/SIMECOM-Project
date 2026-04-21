var express = require("express");
var router = express.Router();

var preferenciasController = require("../controllers/preferenciasController");

router.get("/:idUsuario", function (req, res) {
    preferenciasController.mostrar(req, res);
});

router.put("/:id", function (req, res) {
    preferenciasController.atualizar(req, res);
});

router.delete("/:id", function (req, res) {
    preferenciasController.deletar(req, res);
});

module.exports = router;