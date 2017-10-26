(function( $ ) {
	"use strict";

	pinpointApp.directive( "newPasswordDirective", [ "$rootScope", "NewPasswordAjaxService",
		function ( $rootScope, NewPasswordAjaxService ) {
			return {
				restrict: "EA",
				replace: true,
				templateUrl: "features/myself/newPassword/newPassword.html?v=" + G_BUILD_TIME,
				scope: {
					namespace: "@"
				},
				link: function( scope, element, attr ) {
					var $element = element;
                    scope.alert = {
                        message : ""
                    };

                    scope.$on( "newpassword.open", function() {
                        $element.modal( "show" );
                    });

                    scope.newPasswordForm = function(){
                        var oldPassword = scope.newpassword.oldpassword;
                        var newPassword = scope.newpassword.newpassword;
                        var reNewPassword = scope.newpassword.renewpassword;
                        if(newPassword !== reNewPassword){
                            scope.alert.message = "two input new password is not match!";
                            $("#newpassword-alert").show();
                            return;
                        }

                        var data = {
                            "userId" : $rootScope.userSession.getItem("userId"),
                            "oldPassword" : oldPassword,
                            "newPassword" : newPassword
                        }

                        NewPasswordAjaxService.newPassword(data, function(result){
                            if(result.data.result === "SUCCESS"){
                                $("#newpassword-success").show();
                            } else if(result.data.result === "FAIL"){
                                scope.alert.message = "old password is error!";
                                $("#newpassword-alert").show();
                            } else {
                                scope.alert.message = result.data;
                                $("#newpassword-alert").show();
                            }
                        });
                    }

				}
			};
		}
	]);
})( jQuery );