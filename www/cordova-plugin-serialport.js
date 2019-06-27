var exec = require("cordova/exec");

exports.list = function (success, error) {
  exec(success, error, "cordova-plugin-serialport", "list", []);
};

var open = function (device, rate, success, error) {
  exec(success, error, "cordova-plugin-serialport", "open", [device, rate]);
};
exports.open = open;

var write = function (device, arrayBuffer, success, error) {
  exec(success, error, "cordova-plugin-serialport", "write", [device, arrayBuffer]);
};
exports.write = write;

var writeText = function (device, text, success, error) {
  exec(success, error, "cordova-plugin-serialport", "writeText", [device, text]);
};
exports.writeText = writeText;

var close = function (device, success, error) {
  exec(success, error, "cordova-plugin-serialport", "close", [device]);
};
exports.close = close;

var register = function (device, success, error) {
  exec(success, error, "cordova-plugin-serialport", "register", [device]);
};
exports.register = register;

exports.bind = function (device) {

  this.open = function (rate, success, error) {
    open(device, rate, success, error);
  };

  this.write = function (arrayBuffer, success, error) {
    write(device, arrayBuffer, success, error);
  };

  this.writeText = function (text, success, error) {
    writeText(device, text, success, error);
  };

  this.close = function (success, error) {
    close(device, success, error);
  };

  this.register = function (success, error) {
    register(device, success, error);
  }
};
