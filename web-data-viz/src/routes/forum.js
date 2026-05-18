var express = require("express");
var router = express.Router();

var forumController = require("../controllers/forumController");

router.get("/forumGetter", function (req, res) {
  forumController.forumGetter(req, res);
});

router.get("/forumTop5", function (req, res) {
  forumController.forumTop5(req, res);
});

router.post("/postComment", function (req, res) {
  forumController.postComment(req, res);
});

router.post("/givaLike", function (req, res) {
  forumController.givaLike(req, res);
});

module.exports = router;
