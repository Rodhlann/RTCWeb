(function () {
    'use strict';

    angular
        .module('rtcWebApp.controllers')
        .controller('authenticationController', ['$scope', 'user','rtcWebApp.services.rtcWebAppService', AuthenticationController])

    function AuthenticationController($scope, user, rtcWebAppService) {
        var self = this;

        $scope.user = user;
        $scope.password = null;

        self.login = function() {
            rtcWebAppService.login($scope.user.rtcURL, $scope.user.userName, $scope.password).then(function(response){
                $scope.user.isAuthenticated = true;
            }, function(response){
                $scope.user.isAuthenticated = false;
            });
        };
    }
})();