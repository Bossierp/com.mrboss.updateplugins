var exec = require('cordova/exec');
var platform = require('cordova/platform');

module.exports= {
	update:function(apkurl, version, apkName, successCallback, errorCallback) {
    	exec(null,null, "UpdatePlugin", "update", [apkurl, version, apkName, successCallback, errorCallback]);
	}
};

