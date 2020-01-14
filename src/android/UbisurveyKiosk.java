package com.cordova.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import com.cordova.plugin.KioskActivity;

/**
 * This class echoes a string called from JavaScript.
 */
public class UbisurveyKiosk extends CordovaPlugin {

    public static final String SET_KIOSK_ENABLED = "setKioskEnabled";
    public static final String SWITCH_LAUNCHER = "switchLauncher";
    public static final String IS_IN_KIOSK = "isInKiosk";
    public static final String IS_SET_AS_LAUNCHER = "isSetAsLauncher";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        try {
            if (IS_IN_KIOSK.equals(action)) {

                callbackContext.success(Boolean.toString(KioskActivity.kioskModeEnabled));
                return true;

            } else if (IS_SET_AS_LAUNCHER.equals(action)) {

                String myPackage = cordova.getActivity().getApplicationContext().getPackageName();
                callbackContext.success(Boolean.toString(myPackage.equals(findLauncherPackageName())));
                return true;
            } else if (SET_KIOSK_ENABLED.equals(action)) {
                KioskActivity.kioskModeEnabled = args.getBoolean(0);
                callbackContext.success();
                return true;
            } else if (SWITCH_LAUNCHER.equals(action)) {
                KioskActivity.kioskModeEnabled = false;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                Intent chooser = Intent.createChooser(intent, "Select destination...");
                if (intent.resolveActivity(cordova.getActivity().getPackageManager()) != null) {
                    cordova.getActivity().startActivity(chooser);
                }

                callbackContext.success();
                return true;
            } else if (action.equals("show")) {
                String message;
                String duration;
                try {
                    JSONObject options = args.getJSONObject(0);
                    message = options.getString("message");
                    duration = options.getString("duration");
                } catch (JSONException e) {
                    callbackContext.error("Error encountered: " + e.getMessage());
                    return false;
                }
                // Create the toast
                Toast toast = Toast.makeText(cordova.getActivity(), message,
                    DURATION_LONG.equals(duration) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                // Display toast
                toast.show();
                // Send a positive result to the callbackContext
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
                callbackContext.sendPluginResult(pluginResult);
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

    private String findLauncherPackageName() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = this.cordova.getActivity().getPackageManager().resolveActivity(intent, 0);
        return res.activityInfo.packageName;
    }

}
