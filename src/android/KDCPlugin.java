package com.meyerre.kdcplugin;
 

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaWebView;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


import koamtac.kdc.sdk.KDCBarcodeDataReceivedListener;
import koamtac.kdc.sdk.KDCConnectionListener;
import koamtac.kdc.sdk.KDCConstants;
import koamtac.kdc.sdk.KDCData;
import koamtac.kdc.sdk.KDCDataReceivedListener;
import koamtac.kdc.sdk.KDCGPSDataReceivedListener;
import koamtac.kdc.sdk.KDCMSRDataReceivedListener;
import koamtac.kdc.sdk.KDCNFCDataReceivedListener;
import koamtac.kdc.sdk.KDCReader;




public class KDCPlugin extends CordovaPlugin implements
        KDCDataReceivedListener,
        KDCBarcodeDataReceivedListener,
        KDCGPSDataReceivedListener,
        KDCMSRDataReceivedListener,
        KDCNFCDataReceivedListener,
        KDCConnectionListener {

    public static final String ACTION_LISTEN = "listenForKDC";
    public static final String ACTION_DISABLE = "disableKDC";

    private CallbackContext connectionCallbackContext;

    private boolean isEnabled = false;
    private KDCPlugin me;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        me = this;

        try {

            if (ACTION_LISTEN.equals(action)) {
                this.connectionCallbackContext = callbackContext;
                if(ConnectKDC()){
                    this.isEnabled = true;
                    callbackContext.success();
                    return true;
                }

            }
            if(ACTION_DISABLE.equals(action)){
                this.connectionCallbackContext = callbackContext;
                this.isEnabled = false;
                callbackContext.success();
                return true;
            }
            callbackContext.error("Invalid action");
            return false;

        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        }


    }

    public boolean ConnectKDC(){


        Thread t = new Thread() {

            @Override
            public void run() {

                KDCReader _kdcReader = new KDCReader(null,me,me,me,me,me,me,false);

            };
        };
        t.start();

    }




    /**
     * Stop listening to KDC.
     */
    public void onDestroy() {
        if (this.receiver != null && this.registered) {
            try {
                this.cordova.getActivity().unregisterReceiver(this.receiver);
                this.registered = false;
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error unregistering network receiver: " + e.getMessage(), e);
            }
        }
    }





    @Override
    public void BarcodeDataReceived(KDCData kdcData) {

        try{

        if(this.isEnabled){

            JSONObject parameter = new JSONObject();
            parameter.put("BarcodeData", kdcData.GetData());

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, parameter);
            pluginResult.setKeepCallback(true);
            connectionCallbackContext.sendPluginResult(pluginResult);

        }

        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            connectionCallbackContext.error(e.getMessage());
            return ;
        }

    }

    @Override
    public void DataReceived(KDCData kdcData) {

    }

    @Override
    public void GPSDataReceived(KDCData kdcData) {

    }

    @Override
    public void ConnectionChanged(BluetoothDevice bluetoothDevice, int state) {

        String currentStatus = "Unknown Status";

        // TODO Auto-generated method stub
        switch(state) {
            case KDCConstants.CONNECTION_STATE_CONNECTED:
                currentStatus = "Connected";
                break;
            case KDCConstants.CONNECTION_STATE_CONNECTING:
                currentStatus = "Connecting...";
                break;
            case KDCConstants.CONNECTION_STATE_LOST:
                currentStatus = "Connection Lost !";
                break;
            case KDCConstants.CONNECTION_STATE_FAILED:
                currentStatus = "Connection Failed !";
                break;
            case KDCConstants.CONNECTION_STATE_LISTEN:
                currentStatus = "Listening for Connection";
                break;
        }


        try {

            JSONObject parameter = new JSONObject();
            parameter.put("KDCStatusEnumValue", state);
            parameter.put("KDCStatus", currentStatus);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, parameter);
            pluginResult.setKeepCallback(true);
            connectionCallbackContext.sendPluginResult(pluginResult);

        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            connectionCallbackContext.error(e.getMessage());
            return ;
        }

    }

    @Override
    public void MSRDataReceived(KDCData kdcData) {

    }

    @Override
    public void NFCDataReceived(KDCData kdcData) {

        try{

        if(this.isEnabled){

            JSONObject parameter = new JSONObject();
            parameter.put("NFCData", kdcData.GetData());

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, parameter);
            pluginResult.setKeepCallback(true);
            connectionCallbackContext.sendPluginResult(pluginResult);

        }

        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            connectionCallbackContext.error(e.getMessage());
            return ;
        }

    }





}