var express = require("express");
var router = express.Router();

var setoresController = require("../controllers/setoresController");

router.get("/buscarSituacaoMercado", function (req, res) {
    setoresController.buscarSituacaoMercado(req, res);
});

router.get("/buscarTopSetores", function (req, res) {
    setoresController.buscarTopSetores(req, res);
});

router.get("/buscarTopSetoresExportacao", function (req, res) {
    setoresController.buscarTopSetoresExportacao(req, res);
});

router.get("/buscarTopSetoresImportacao", function (req, res) {
    setoresController.buscarTopSetoresImportacao(req, res);
});

module.exports = router;