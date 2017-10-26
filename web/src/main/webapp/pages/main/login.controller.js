(function($) {
    'use strict';
    pinpointApp.controller("loginController", ["$scope", "$rootScope", "$location", "LoginAjaxService", function ($scope, $rootScope, $location, LoginAjaxService) {
        $scope.alert = {
            message : ""
        };
        $scope.loginForm = function () {

            var data = {
                username : $scope.login.username,
                password : $scope.login.password
            }

            LoginAjaxService.login(data, function(result){

                if(result.data.result === "SUCCESS"){
                    $rootScope.userSession.setItem("userId", result.data.userId);
                    $rootScope.userSession.setItem("username", $scope.login.username);
                    $rootScope.userSession.setItem("role", result.data.role);
                    $location.path('/main');
                } else {
                    $scope.alert.message = "  username or password is error!";
                    $("#login-alert").show();
                }

            },function(error){
                $scope.alert.message = error.data;
                $("#login-alert").show();
            });

        };

    }]);
})(jQuery);
