(function () {
    'use strict';

    angular
        .module('rtcWebApp.controllers')
        .controller('navigation', ['$scope', '$location', 'rtcWebApp.services.authService', 'rtcWebApp.services.progressService', NavigationController])

    function NavigationController($scope, $location, authService, progressService) {
        var self = this;

        $scope.credentials = {};
        $scope.processing = false;

        progressService.registerCallback(function() {
            $scope.processing = progressService.processing;
        });

        authService.registerCallback(function(){
            $scope.authenticated = authService.isAuthenticated();
            $scope.username = authService.getUserName();
        });

        authService.authenticate();

        self.showLogin = function() {
            $location.path('/login');
        };

        $scope.login = function() {
            authService.authenticate($scope.credentials, function() {
                $scope.authenticated = authService.isAuthenticated();

                if($scope.authenticated) {
                    $scope.error = false;
                    $location.path('/dashboard');
                } else {
                    $scope.error = true;
                    $location.path('/login');
                }
            })
        };

        self.openMenu = function($mdOpenMenu, ev) {
            $mdOpenMenu(ev);
        };

        self.logout = function() {
            authService.logout(function() {
                $scope.authenticated = authService.isAuthenticated();
                $location.path("/");
            });
        };
    }
})();