(function () {
    'use strict';

    angular
        .module('rtcWebApp.controllers')
        .controller('appController', ['$scope', '$location', 'user', 'rtcWebApp.services.rtcWebAppService', AppController])

    function AppController($scope, $location, user, rtcWebAppService) {
        var self = this;
        $scope.user = user;

        $scope.$watch('user.isAuthenticated', function(newValue, oldValue) {
            if($scope.user.isAuthenticated) {
                $location.path("/dashboard");
            } else {
                $location.path("/login");
            }
        });
    }
})();
