package me.izee.cordova.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import java.io.IOException;
import java.util.concurrent.Future;

import android_serialport_api.SerialPort;

public class SerialPortModel {

    private Future futureWatch;

    private SerialPort port;

    private CallbackContext watcher;

    public SerialPortModel(SerialPort port) {
        this.port = port;
    }

    public Future getFutureWatch() {
        return futureWatch;
    }

    public void setFutureWatch(Future futureWatch) {
        this.futureWatch = futureWatch;
    }

    public SerialPort getPort() {
        return port;
    }

    public void setPort(SerialPort port) {
        this.port = port;
    }

    public CallbackContext getWatcher() {
        return watcher;
    }

    public void setWatcher(CallbackContext watcher) {
        this.watcher = watcher;
    }

    public void write(byte[] bytes) throws IOException {
        if (port != null) {
            port.getOutputStream().write(bytes);
        }
    }

    public void close() {
        if (port != null) {
            port.close();
        }
    }

    public void sendPluginResult(PluginResult pluginResult) {
        if (watcher != null) {
            watcher.sendPluginResult(pluginResult);
        }
    }
}
