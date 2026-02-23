require("dotenv").config({ path: '.env' });

var express = require("express");
var cors = require("cors");
var path = require("path");
var PORTA_APP = process.env.APP_PORT;
var HOST_APP = process.env.APP_HOST;

var app = express();

var indexRouter = require("./src/routes/index");
var usersRouter = require("./src/routes/users");

app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, "public")));

app.use("/", indexRouter);
app.use("/users", usersRouter);

app.listen(PORTA_APP, function () {
    console.log(`Servidor Rodando Em: http://${HOST_APP}:${PORTA_APP} :`);
});