var exec = require('cordova/exec');

var UpdatePlugin = {
	update:function(apkurl, version, apkName, successCallback, errorCallback) {
    	exec(null,null, "UpdatePlugin", "update", [apkurl, version, apkName, successCallback, errorCallback]);
	}
};

module.exports= UpdatePlugin;

