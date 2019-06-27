package me.izee.cordova.plugin;

import org.apache.cordova.CallbackContext;

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
}
