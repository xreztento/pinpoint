(function( $ ) {
    "use strict";

    pinpointApp.directive( "applicationGroupDirective", [ "CommonAjaxService", "ApplicationUtilService", "ApplicationAjaxService", "$rootScope", "$timeout",
        function ( CommonAjaxService , ApplicationUtilService, ApplicationAjaxService, $rootScope, $timeout) {
            return {
                restrict: "EA",
                replace: true,
                templateUrl: "features/configuration/application/applicationGroup.html?v=" + G_BUILD_TIME,
                scope: true,
                link: function( scope, element, attr ) {
                    var $element =$(element);
                    var bLoaded = false;
                    var $workingNode = null;
                    var $previous = null;
                    var $elAlert = $element.find(".some-alert");
                    var $elLoading = $element.find(".some-loading");
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
                        ApplicationUtilService.hide( $elLoading );
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

                            if ( angular.isArray( data ) === false || data.length === 0 ) {

                                scope.applicationList = [];

                            } else {
                                scope.applicationList = data;
                            }
                        }, function() {
                        });
                    }
                    function isSameNode( $current ) {
                        return ApplicationUtilService.extractID( $workingNode ) === ApplicationUtilService.extractID( $current );
                    }
                    function cancelPreviousWork() {
                        RemoveApplicationName.cancelAction( ApplicationUtilService, $workingNode );
                    }
                    function showAlert( oServerError ) {
                        $elAlert.find( ".message" ).html( oServerError.errorMessage );
                        ApplicationUtilService.hide( $elLoading );
                        ApplicationUtilService.show( $elAlert );
                    }
                    scope.onCloseAlert = function() {
                        ApplicationUtilService.hide( $elAlert );
                    };
                    scope.onRemoveApplicationName = function($event){
                        var $node = ApplicationUtilService.getNode( $event, "li" );
                        if ( $workingNode !== null && isSameNode( $node ) === false ) {
                            cancelPreviousWork( $node );
                        }
                        $workingNode = $node;
                        RemoveApplicationName.onAction( ApplicationUtilService, $workingNode );
                    }

                    scope.onCancelRemoveApplicationName = function(){
                        RemoveApplicationName.cancelAction( ApplicationUtilService, $workingNode );
                    }


                    scope.onApplyRemoveApplicationName = function(applicationName){
                        RemoveApplicationName.applyAction( ApplicationUtilService, ApplicationAjaxService, $workingNode, $elLoading, applicationName, function(result) {
                            $timeout(function(){
                                bLoaded === false;
                                getApplicationList();
                                $rootScope.$broadcast("applicationList.reload");
                                ApplicationUtilService.hide( $elLoading );

                            }, 500);
                        }, showAlert );
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

    var CONSTS = {
        DIV_NORMAL: "div._normal",
        DIV_REMOVE: "div._remove"
    };

    var RemoveApplicationName = {
        _bIng: false,
        onAction: function( ApplicationUtilService, $node ) {
            this._bIng = true;
            $node.addClass("remove");
            ApplicationUtilService.hide( $node.find( CONSTS.DIV_NORMAL ) );
            ApplicationUtilService.show( $node.find( CONSTS.DIV_REMOVE ) );
        },
        cancelAction: function( ApplicationUtilService, $node ) {
            if ( this._bIng === true ) {
                $node.removeClass("remove");
                ApplicationUtilService.hide($node.find( CONSTS.DIV_REMOVE ));
                ApplicationUtilService.show($node.find( CONSTS.DIV_NORMAL ));
                this._bIng = false;
            }
        },
        applyAction: function( ApplicationUtilService, ApplicationAjaxService, $node, $elLoading, applicationName, cbSuccess, cbFail ) {
            ApplicationUtilService.show( $elLoading );
            ApplicationAjaxService.removeApplicationName(applicationName, function(result){
                if(cbSuccess){
                    cbSuccess(result);
                }

            }, function(result){
                if(cbFail){
                    cbFail(result);
                }
            });

        }
    };

})( jQuery );