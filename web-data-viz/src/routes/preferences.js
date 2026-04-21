var express = require("express");
var router = express.Router();

var preferencesController = require("../controllers/preferencesController");

router.post("/register", function (req, res) {
  preferencesController.register(req, res);
});

module.exports = router;
