package me.izee.cordova.plugin;

import android.util.Base64;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

/**
 * This class echoes a string called from JavaScript.
 */
public class NativeSerial extends CordovaPlugin {
    private static final String LOG_TAG = "NativeSerial";

    private Map<String, SerialPortModel> portMap = new LinkedHashMap<String, SerialPortModel>();
    private SerialPortFinder mSerialPortFinder = new SerialPortFinder();

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("list")) {
            JSONArray resArr = new JSONArray();
            String[] entryValues = mSerialPortFinder.getAllDevicesPath();
            for (int i = 0; i < entryValues.length; i++) {
                resArr.put(i, entryValues[i]);
            }
            callbackContext.success(resArr);
            return true;
        } else if (action.equals("open")) {
            Log.d(LOG_TAG, "execute open");
            final String device = args.getString(0);
            final int rate = args.getInt(1);
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    NativeSerial.this.openPort(device, rate, callbackContext);
                    NativeSerial.this.startWatch(device);
                }
            });
            return true;
        } else if (action.equals("close")) {
            final String device = args.getString(0);
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    NativeSerial.this.closePort(device, callbackContext);
                }
            });
            return true;
        } else if (action.equals("write")) {
            final String device = args.getString(0);
            final String data = args.getString(1);
            Log.d(LOG_TAG, "execute write:" + data);
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    byte[] decode = Base64.decode(data, Base64.NO_WRAP);
                    Log.d(LOG_TAG, "write bytes:" + decode);
                    NativeSerial.this.writeBytes(device, decode, callbackContext);
                }
            });
            return true;
        } else if (action.equals("writeText")) {
            final String device = args.getString(0);
            final String data = args.getString(1);
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    NativeSerial.this.writeText(device, data, callbackContext);
                }
            });
            return true;
        } else if (action.equals("register")) {
            final String device = args.getString(0);

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    SerialPortModel serialPortModel = portMap.get(device);
                    if (serialPortModel != null) {
                        serialPortModel.setWatcher(callbackContext);
                    }
                    NativeSerial.this.startWatch(device);
                }
            });
            return true;
        }
        Log.d(LOG_TAG, "unknown action:" + action);
        return false;
    }

    private synchronized void startWatch(String device) {
        SerialPortModel serialPortModel = portMap.get(device);
        if (serialPortModel == null) {
            return;
        }
        Future futureWatch = serialPortModel.getFutureWatch();

        if (futureWatch != null && !(futureWatch.isDone() || futureWatch.isCancelled())) {
            return;
        }

        futureWatch = cordova.getThreadPool().submit(new Runnable() {
            public void run() {
                Log.d(LOG_TAG, "watch start run");
                while (!Thread.currentThread().isInterrupted()) {
                    SerialPort serialPortModelPort = serialPortModel.getPort();

                    if (serialPortModelPort == null) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    InputStream inputStream = serialPortModelPort.getInputStream();

                    if (inputStream == null) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    CallbackContext watcher = serialPortModel.getWatcher();

                    try {
                        byte[] buffer = new byte[64];
                        int size = inputStream.read(buffer);
                        if (size > 0) {
                            Log.d(LOG_TAG, String.format("%s,got input:%s", System.currentTimeMillis(), size));
//              PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, new String(buffer, 0, size));
                            byte[] data = Arrays.copyOf(buffer, size);
                            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, data);
                            pluginResult.setKeepCallback(true);

                            if (watcher != null) {
                                watcher.sendPluginResult(pluginResult);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        PluginResult error = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
                        error.setKeepCallback(true);
                        if (watcher != null) {
                            watcher.sendPluginResult(error);
                        }
                    }
                }
            }
        });

        serialPortModel.setFutureWatch(futureWatch);
    }

    private void openPort(String device, int rate, CallbackContext callbackContext) {
        try {
            if (!portMap.containsKey(device)) {
                SerialPort port = new SerialPort(new File(device), rate, 0);
                SerialPortModel serialPortModel = new SerialPortModel(port);
                portMap.put(device, serialPortModel);
            }
        } catch (IOException e) {
            SerialPortModel serialPortModel = portMap.get(device);
            if (serialPortModel != null) {
                SerialPort serialPortModelPort = serialPortModel.getPort();
                if (serialPortModelPort != null) {
                    serialPortModelPort.close();
                    portMap.remove(device);
                }
            }
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
        callbackContext.success();
    }

    private void closePort(String device, final CallbackContext callbackContext) {
        SerialPortModel serialPortModel = portMap.get(device);
        if (serialPortModel != null) {
            SerialPort serialPortModelPort = serialPortModel.getPort();
            if (serialPortModelPort != null) {
                serialPortModelPort.close();
                portMap.remove(device);
            }
        }
        callbackContext.success();
    }

    private void writeBytes(String device, final byte[] bytes, final CallbackContext callbackContext) {
        try {
            SerialPortModel serialPortModel = portMap.get(device);
            if (serialPortModel != null) {
                SerialPort serialPortModelPort = serialPortModel.getPort();
                if (serialPortModelPort != null) {
                    OutputStream outputStream = serialPortModelPort.getOutputStream();
                    outputStream.write(bytes);
                    callbackContext.success();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }

    private void writeText(String device, final String data, final CallbackContext callbackContext) {
        try {
            SerialPortModel serialPortModel = portMap.get(device);
            if (serialPortModel != null) {
                SerialPort serialPortModelPort = serialPortModel.getPort();
                if (serialPortModelPort != null) {
                    serialPortModelPort.getOutputStream().write(data.getBytes());
                    callbackContext.success();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }
}
