var exec = require("cordova/exec");

exports.list = function (success, error) {
  exec(success, error, "cordova-plugin-serialport", "list", []);
};
exports.open = function (device, rate, success, error) {
  exec(success, error, "cordova-plugin-serialport", "open", [device, rate]);
};
exports.write = function (device, arrayBuffer, success, error) {
  exec(success, error, "cordova-plugin-serialport", "write", [device, arrayBuffer]);
};
exports.writeText = function (device, text, success, error) {
  exec(success, error, "cordova-plugin-serialport", "writeText", [device, text]);
};
exports.close = function (device, success, error) {
  exec(success, error, "cordova-plugin-serialport", "close", [device]);
};
exports.register = function (device, success, error) {
  exec(success, error, "cordova-plugin-serialport", "register", [device]);
};
