package me.izee.cordova.plugin;

import android.util.Base64;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android_serialport_api.SerialPortFinder;

/**
 * This class echoes a string called from JavaScript.
 */
public class NativeSerial extends CordovaPlugin {
    private static final String LOG_TAG = "NativeSerial";

    private Map<String, SerialPortModel> portMap = new ConcurrentHashMap<>();
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
                }
            });
            return true;
        }
        Log.d(LOG_TAG, "unknown action:" + action);
        return false;
    }

    private void openPort(String device, int rate, final CallbackContext callbackContext) {
        if (!portMap.containsKey(device)) {
            portMap.put(device, new SerialPortModel(device, rate));
        }
        callbackContext.success();
    }

    private void closePort(String device, final CallbackContext callbackContext) {
        closePort(device);
        callbackContext.success();
    }

    private void closePort(String device) {
        SerialPortModel serialPortModel = portMap.get(device);
        if (serialPortModel != null) {
            serialPortModel.close();
        }
    }

    private void writeBytes(String device, final byte[] bytes, final CallbackContext callbackContext) {
        SerialPortModel serialPortModel = portMap.get(device);
        if (serialPortModel != null) {
            serialPortModel.send(bytes);
            callbackContext.success();
        }
    }

    private void writeText(String device, final String data, final CallbackContext callbackContext) {
        writeBytes(device, data.getBytes(), callbackContext);
    }
}
