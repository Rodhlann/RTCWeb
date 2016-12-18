(function () {
    'use strict';

    angular
        .module('rtcWebApp.controllers')
        .controller('home', ['$scope', '$http', 'rtcWebApp.services.authService', HomeController])

    function HomeController($scope, $http, authService) {
        var self = this;

        authService.registerCallback(function(){
            $scope.authenticated = authService.isAuthenticated();
        });

        $scope.authenticated = authService.isAuthenticated();

        $http.get('/resource/').then(function(response) {
            $scope.greeting = response.data;
        });
    }
})();