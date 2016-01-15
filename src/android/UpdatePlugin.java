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
			String url = optionsArr.getString(0);
			String ver = optionsArr.getString(1);
			String name = optionsArr.getString(2);
			boolean flag = updateVersion(url, ver, name);
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

	private Boolean  updateVersion(String url, String ver, String name) {
		boolean flag = false;
		Update ap = new Update(cordova.getActivity());
		String str = ap.getServerMessage(url, ver, name);
		if (str == "true") {
			ap.doUpdate();
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

}
