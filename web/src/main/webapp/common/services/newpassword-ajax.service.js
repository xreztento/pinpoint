(function() {
	'use strict';

	pinpointApp.constant( "NewPasswordAjaxServiceConfig", {
		"loginUrl" : "/newPassword.pinpoint"
	});
	
	pinpointApp.service( "NewPasswordAjaxService", [ "NewPasswordAjaxServiceConfig", "$http", function( cfg, $http) {

		this.newPassword = function(data, resolve, reject) {

            $http({
				"url": cfg.loginUrl,
				"method": "POST",
				"headers": {
					"Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
				},
				"data": data,
                transformRequest : function(obj){
                    var str = [];
                    for(var p in obj){
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    }
                    return str.join("&");
                }
			}).then(function(result) {
                if(resolve){
                    resolve( result );
                }

			}, function( error ) {
                if(reject){
                    reject( error );

                }
			});

		};

	}]);
})();