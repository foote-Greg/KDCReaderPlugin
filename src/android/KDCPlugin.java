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
    public static final String ACTION_ENABLE = "enableKDC";

    private CallbackContext connectionCallbackContext;

    private boolean isEnabled = false;
    private KDCPlugin me;
            
    private KDCReader _kdcReader;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        me = this;

        try {

            if (ACTION_LISTEN.equals(action)) {

                this.connectionCallbackContext = callbackContext;
                
                ConnectKDC();
                    
                this.isEnabled = true;
                              
                
                return true;

            }
            
            if(ACTION_DISABLE.equals(action)){
                
                this.connectionCallbackContext = callbackContext;
                this.isEnabled = false;
                
                disableKDC();
                
                return true;
            }
            
            if(ACTION_ENABLE.equals(action)){
                
                this.connectionCallbackContext = callbackContext;
                this.isEnabled = false;
               
                enableKDC();
                
                return true;
            }
            
            // Otherwise --
            callbackContext.error("Invalid action");
            return false;

        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        }


    }

    public void ConnectKDC(){

        
      JSONObject parameter = new JSONObject();
      parameter.put("ConnectKDCCalled", "CONNECT");

      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, parameter);
      pluginResult.setKeepCallback(true);
      connectionCallbackContext.sendPluginResult(pluginResult);
        

        ArrayList<BluetoothDevice> _btDevices = KDCReader.GetAvailableDeviceList();
        
        
        Thread t = new Thread() {

            @Override
            public void run() {

                _kdcReader = new KDCReader(null,me,me,me,me,me,me,false);

            };
        };
        t.start();

    }




    /**
     * Stop listening to KDC.
     */
    public void onDestroy() {
       
    }


    private void disableKDC(){
        
        // Turn the NFC power OFF
        _kdcReader.EnableNFCPower(false) ;
        
       JSONObject parameter = new JSONObject();
       parameter.put("KDCDisable", _kdcReader.IsNFCPowerEnabled());

       PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, parameter);
       pluginResult.setKeepCallback(true);
       connectionCallbackContext.sendPluginResult(pluginResult);        
        
    }

    private void enableKDC(){
        
        // Turn the NFC power ON
        _kdcReader.EnableNFCPower(true) ;
        
       JSONObject parameter = new JSONObject();
       parameter.put("KDCEnable", _kdcReader.IsNFCPowerEnabled());

       PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, parameter);
       pluginResult.setKeepCallback(true);
       connectionCallbackContext.sendPluginResult(pluginResult);        
        
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
            
                if(!_kdcReader.IsNFCSupported()){
                    // Problem !
                    sendPluginBadResult("NFC Not Supported !");
                }

                // Configure NFC Power
                _kdcReader.EnableDuplicateCheck(true);

                // Turn the NFC power off initially
                _kdcReader.EnableNFCPower(false) ;
            
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
            
    
    private void sendPluginBadResult(String msg){
        
        
         JSONObject parameter = new JSONObject();
            parameter.put("KDCErrorMessage", msg);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, parameter);
            pluginResult.setKeepCallback(true);
            connectionCallbackContext.sendPluginResult(pluginResult);

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