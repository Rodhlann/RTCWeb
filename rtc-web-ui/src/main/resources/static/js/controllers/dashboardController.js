(function () {
    'use strict';

    angular
        .module('rtcWebApp.controllers')
        .controller('dashboard', ['$scope', 'rtcWebApp.services.rtcWebAppService', 'rtcWebApp.services.progressService', DashboardController])

    function DashboardController($scope, rtcService, progressService) {
        var self = this;

        $scope.projectAreas = [];
        $scope.teamAreas = [];
        $scope.sprints = [];

        // $scope.workitems = [];
        //
        // self.getWorkItems = function() {
        //     rtcWebAppService.getWorkItems().then(function(response){
        //         $scope.workitems.push(response.data);
        //     }, function(response) {
        //         console.debug('unable to get work items');
        //     });
        // };

        self.getSprints = function(projectAreaName) {
            progressService.showProgressBar();
            rtcService.getSprints(projectAreaName)
                .then(function(response) {
                    $scope.sprints = response.data;
                    progressService.hideProgressBar();
                });
        };

        self.getProjectAreas = function() {
            progressService.showProgressBar();
            rtcService.getProjectAreas().then(function(response){
                $scope.projectAreas = response.data;
                progressService.hideProgressBar();
            });
        };

        self.getTeamAreas = function() {
            progressService.showProgressBar();
            rtcService.getTeamAreas().then(function(response) {
                $scope.teamAreas = response.data;
                progressService.hideProgressBar();
            })
        };

        self.getProjectAreas();
        self.getTeamAreas();
    }
})();