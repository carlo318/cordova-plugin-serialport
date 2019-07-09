var exec = require("cordova/exec");

exports.list = function (success, error) {
  exec(success, error, "cordova-plugin-serialport", "list", []);
};

function open(device, rate, success, error) {
  exec(success, error, "cordova-plugin-serialport", "open", [device, rate]);
}

exports.open = open;

function write(device, arrayBuffer, success, error) {
  exec(success, error, "cordova-plugin-serialport", "write", [device, arrayBuffer]);
}

exports.write = write;

function writeText(device, text, success, error) {
  exec(success, error, "cordova-plugin-serialport", "writeText", [device, text]);
}

exports.writeText = writeText;

function close(device, success, error) {
  exec(success, error, "cordova-plugin-serialport", "close", [device]);
}

exports.close = close;

function register(device, success, error) {
  exec(success, error, "cordova-plugin-serialport", "register", [device]);
}

exports.register = register;

exports.bind = function (device, rate) {

  this.open = function (success, error) {
    open(device, rate, success, error);
  };

  this.write = function (arrayBuffer, success, error) {
    write(device, arrayBuffer, success, error);
  };

  this.writeText = function (text, success, error) {
    writeText(device, text, success, error);
  };

  this.writeHex = function (hex, success, error) {
    write(device, new Uint8Array(
      hex.match(/[\da-f]{2}/gi).map(function (h) {
        return parseInt(h, 16);
      })
    ).buffer, success, error);
  };

  this.close = function (success, error) {
    close(device, success, error);
  };

  this.register = function (success, error, type) {
    if (type === 'ascii') {
      register(device, function (arrayBuffer) {
        success(Array.prototype.map
          .call(new Uint8Array(arrayBuffer), function (bit) {
            return String.fromCharCode(bit);
          })
          .join("")
          .replace(/\r/g, "")
          .replace(/\n/g, ""));
      }, error)
    } else if (type === 'hex') {
      register(device, function (arrayBuffer) {
        success(Array.prototype.map.call(new Uint8Array(arrayBuffer), function (x) {
          return ('00' + x.toString(16)).slice(-2);
        }).join('').toUpperCase())
      }, error)
    } else {
      register(device, success, error);
    }
  }
};
