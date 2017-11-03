(function( $ ) {
    "use strict";

    pinpointApp.directive( "applicationGroupDirective", [ "CommonAjaxService", "ApplicationAjaxService", "$rootScope", "$timeout",
        function ( CommonAjaxService , ApplicationAjaxService, $rootScope, $timeout) {
            return {
                restrict: "EA",
                replace: true,
                templateUrl: "features/configuration/application/applicationGroup.html?v=" + G_BUILD_TIME,
                scope: true,
                link: function( scope, element, attr ) {
                    var $element =$(element);
                    var bLoaded = false;
                    var $previous = null;
                    scope.applicationList = [];

                    init();

                    function init() {
                        $element.find("ul").on("click", function($event) {
                            var $target = $( $event.toElement || $event.target );
                            var tagName = $target.get(0).tagName.toLowerCase();

                            if ( tagName === "span" || tagName === "img" ) {
                                selectApplication( $target.parents("li") );
                            } else if ( tagName === "li" ) {
                                selectApplication( $target );
                            }
                        });
                    }
                    function selectApplication( $ele ) {
                        if ( $previous !== null ) {
                            $previous.removeClass( "selected" );
                        }
                        $ele.addClass( "selected" );
                        $previous = $ele;
                        scope.$emit( "applicationGroup.selectApp", $ele.attr("id") );
                    }
                    function getApplicationList() {
                        CommonAjaxService.getApplicationList( function( data ) {
                            //console.log(data);
                            if ( angular.isArray( data ) === false || data.length === 0 ) {

                                scope.applicationList = [];

                            } else {
                                scope.applicationList = data;
                            }
                        }, function() {
                        });
                    }

                    scope.onRemoveApplicationName = function(applicationName){
                        ApplicationAjaxService.removeApplicationName(applicationName, function(result){
                            $timeout(function(){
                                bLoaded === false;
                                getApplicationList();
                                $rootScope.$broadcast("applicationList.reload");
                            }, 500);


                        }, function(result){

                        });
                    }

                    scope.getAppId = function( oApp ) {
                        return oApp.applicationName + "@" + oApp.serviceType;
                    };
                    scope.$on( "applicationGroup.load", function() {
                        if ( bLoaded === false ) {
                            getApplicationList();
                            bLoaded = true;
                        }
                    });
                }
            };
        }
    ]);

})( jQuery );