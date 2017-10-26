(function() {
	'use strict';

	pinpointApp.constant( "LogoutAjaxServiceConfig", {
		"logoutUrl" : "/logout.pinpoint"
	});
	
	pinpointApp.service( "LogoutAjaxService", [ "LogoutAjaxServiceConfig", "$http", function( cfg, $http) {

		this.logout = function() {
            $http({
				"url": cfg.logoutUrl,
				"method": "GET"
			});

		};

	}]);
})();