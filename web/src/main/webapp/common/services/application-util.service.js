(function($) {
	'use strict';

	pinpointApp.constant('ApplicationUtilServiceConfig', {
		"hideClass": "hide-me"
	});
	
	pinpointApp.service( "ApplicationUtilService", [ "ApplicationUtilServiceConfig", "$timeout", "SystemConfigurationService", function ( $config, $timeout, SystemConfigService ) {
		var self = this;
		this.show = function( $el ) {
			$el.removeClass( $config.hideClass );
		};
		this.hide = function() {
			for( var i = 0 ; i < arguments.length ; i++ ) {
				arguments[i].addClass( $config.hideClass );
			}
		};

		this.setTotal = function( $elTotal, n ) {
			$elTotal.html( "(" + n + ")");
		};
		this.hasDuplicateItem = function( list, func ) {
			var len = list.length;
			var has = false;
			for( var i = 0 ; i < len ; i++ ) {
				if ( func( list[i] ) ) {
					has = true;
					break;
				}
			}
			return has;
		};

		this.extractID = function( $el ) {
			return $el.prop("id").split("@")[0];
		};
		this.getNode = function( $event, tagName ) {
			return $( $event.toElement || $event.target ).parents( tagName );
		};
	}]);
})(jQuery);