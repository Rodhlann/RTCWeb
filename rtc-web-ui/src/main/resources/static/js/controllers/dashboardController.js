(function () {
    'use strict';

    angular
        .module('rtcWebApp.controllers')
        .controller('dashboard', ['$scope', 'rtcWebApp.services.rtcWebAppService', DashboardController])

    function DashboardController($scope, rtcService) {
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
            rtcService.getProjectAreas().then(function(response){
                $scope.projectAreas = response.data;
            });
        };

        self.getProjectAreas();
    }
})();