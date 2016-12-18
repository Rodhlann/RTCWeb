(function () {
    'use strict';

    angular
        .module('rtcWebApp.controllers')
        .controller('navigation', ['$scope', '$location', 'rtcWebApp.services.authService', NavigationController])

    function NavigationController($scope, $location, authService) {
        var self = this;

        $scope.credentials = {};

        authService.registerCallback(function(){
            $scope.authenticated = authService.isAuthenticated();
            $scope.username = authService.getUserName();
        });

        authService.authenticate();

        $scope.login = function() {
            authService.authenticate($scope.credentials, function() {
                $scope.authenticated = authService.isAuthenticated();

                if($scope.authenticated) {
                    $scope.error = false;
                    $location.path('/');
                } else {
                    $scope.error = true;
                    $location.path('/login');
                }
            })
        };

        $scope.logout = function() {
            authService.logout(function() {
                $scope.authenticated = authService.isAuthenticated();
                $location.path("/");
            });
        };
    }
})();