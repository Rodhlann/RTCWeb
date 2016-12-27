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
        $scope.workItems = [];
        $scope.today = new Date();
        $scope.selectedProjectArea = null;
        $scope.selectedSprint = null;

        self.getWorkItems = function() {
            progressService.showProgressBar();
            rtcService.getWorkItems($scope.selectedProjectArea.name, $scope.selectedSprint).then(function(response){
                $scope.workItems = response.data;
                progressService.hideProgressBar();
            }, function(response) {
                console.debug('unable to get work items');
            });
        };

        self.getSprints = function() {
            progressService.showProgressBar();
            rtcService.getSprints($scope.selectedProjectArea.name)
                .then(function(response) {
                    $scope.sprints = response.data;
                    progressService.hideProgressBar();
                });
        };

        self.getProjectAreas = function() {
            progressService.showProgressBar();
            rtcService.getProjectAreas().then(function(response){
                $scope.projectAreas = response.data;
                $scope.selectedProjectArea = $scope.projectAreas[0];
                progressService.hideProgressBar();
                self.getSprints();
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
        // self.getTeamAreas();
    }
})();