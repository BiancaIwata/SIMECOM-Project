var express = require("express");
var router = express.Router();

var usersController = require("../controllers/usersController");

router.post("/register", function (req, res) {
  usersController.register(req, res);
});

router.post("/auth", function (req, res) {
  usersController.auth(req, res);
});

router.post("/getter", function (req, res) {
  usersController.getter(req, res);
});

router.post("/setter", function (req, res) {
  usersController.setter(req, res);
});

router.post("/deleter", function (req, res) {
  usersController.deleter(req, res);
});

module.exports = router;
