var express = require("express");
var router = express.Router();

var adminController = require("../controllers/adminController");

router.get("/admin", function (req, res) {
  adminController.listarUsuarios(req, res);
});

router.post("/deletarUsuario", function (req, res) {
  adminController.deletarUsuario(req, res);
});

module.exports = router;
