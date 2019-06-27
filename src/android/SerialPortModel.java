package me.izee.cordova.plugin;

import org.apache.cordova.CallbackContext;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import android_serialport_api.SerialPort;

public class SerialPortModel {

    private Future futureWatch;

    private List<CallbackContext> watchers = new LinkedList<CallbackContext>();

    private SerialPort port;

    public SerialPortModel(SerialPort port) {
        this.port = port;
    }

    public Future getFutureWatch() {
        return futureWatch;
    }

    public void setFutureWatch(Future futureWatch) {
        this.futureWatch = futureWatch;
    }

    public List<CallbackContext> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<CallbackContext> watchers) {
        this.watchers = watchers;
    }

    public SerialPort getPort() {
        return port;
    }

    public void setPort(SerialPort port) {
        this.port = port;
    }
}
