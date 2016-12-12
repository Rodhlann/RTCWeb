(function () {
    'use strict';

    console.debug("toolbarController Startup");

    angular
        .module('rtcWebApp.controllers')
        .controller('toolbarController', ['$scope', 'user', 'rtcWebApp.services.rtcWebAppService', ToolbarController])

    function ToolbarController($scope, user, rtcWebAppService) {
        var self = this;

        $scope.user = user;

        self.openMenu = function($mdOpenMenu, ev) {
            $mdOpenMenu(ev);
        };

        self.logout = function() {
            rtcWebAppService.logout().then(function(response){
                $scope.user.isAuthenticated = false;
                $scope.user.userName = null;
            });
        }
    }
})();