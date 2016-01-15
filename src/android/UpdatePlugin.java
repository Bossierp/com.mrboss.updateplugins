package com.mrboss.updateplugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Context;
import android.app.AlertDialog;

import android.util.Log;



public class UpdatePlugin extends CordovaPlugin  {

	@Override
	public boolean execute(String action, JSONArray optionsArr, CallbackContext callbackContext) throws JSONException {
		if (action.equals("update")) {
			boolean flag = updateVersion();
			return flag;
		}
		return false;
	}

    private void Alert(String msg) {
        Dialog alertDialog = new AlertDialog.Builder(this.cordova.getActivity()).
        setTitle("对话框的标题").
        setMessage(msg).
        setCancelable(false).
        setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        }).
        create();
        alertDialog.show();
    }

	private Boolean  updateVersion() {
		boolean flag = false;
		Update ap = new Update(cordova.getActivity());
		String str = ap.getServerMessage("http://192.168.1.31:555/Make5_0_Phone/Download/MrBossErp.apk", "1.0.0", "MrBossErp");
		if (str == "true") {
			ap.doUpdate();
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

}
