const express = require("express");
const socket = require("socket.io");
const fs = require("fs");

const app = express();
var port = process.env.port || 3000;

const server = app.listen(port);

//Playing variables:
app.use(express.static("public")); //show static files in 'public' directory
console.log("Server is running");
const io = socket(server);

var count = 0;

//Socket.io Connection------------------
io.on("connection", (socket) => {
  console.log("New socket connection: " + socket.id);

  socket.on("counter", () => {
    count++;
    console.log(count);
    io.emit("counter", count);
  });
});
