(function () {
    'use strict';

    angular
        .module('rtcWebApp.controllers')
        .controller('dashboardController', ['$scope', 'rtcWebApp.services.rtcWebAppService', DashboardController])

    function DashboardController($scope, rtcWebAppService) {
        var self = this;

        $scope.projectAreas = [];

        // $scope.workitems = [];
        //
        // self.getWorkItems = function() {
        //     rtcWebAppService.getWorkItems().then(function(response){
        //         $scope.workitems.push(response.data);
        //     }, function(response) {
        //         console.debug('unable to get work items');
        //     });
        // };

        self.getProjectAreas = function() {
            rtcWebAppService.getProjectAreas().then(function(response){
                $scope.projectAreas = response.data;
            });
        };

        self.getProjectAreas();
    }
})();