(function() {
	'use strict';

	pinpointApp.constant( "ApplicationAjaxServiceConfig", {
		"removeApplicationNameUrl" : "/admin/removeApplicationName.pinpoint"
	});
	
	pinpointApp.service( "ApplicationAjaxService", [ "ApplicationAjaxServiceConfig", "$http", function(cfg, $http) {

		this.removeApplicationName = function(applicationName, resolve, reject) {

            $http({
				"url": cfg.removeApplicationNameUrl,
				"method": "GET",
				"params": {
                    "applicationName" : applicationName
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