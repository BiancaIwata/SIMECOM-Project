var express = require("express");
var router = express.Router();

var municipiosController = require("../controllers/municipiosController");

router.get("/buscarSituacaoAnual", function (req, res) {
    municipiosController.buscarSituacaoAnual(req, res);
});

router.get("/buscarTopMunicipios", function (req, res) {
    municipiosController.buscarTopMunicipios(req, res);
});

router.get("/buscarTopMunicipiosImportacao", function (req, res) {
    municipiosController.buscarTopMunicipiosImportacao(req, res);
});

router.get("/buscarTopMunicipiosExportacao", function (req, res) {
    municipiosController.buscarTopMunicipiosExportacao(req, res);
});

module.exports = router;