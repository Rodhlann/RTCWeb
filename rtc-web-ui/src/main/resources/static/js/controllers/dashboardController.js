(function () {
    'use strict';

    angular
        .module('rtcWebApp.controllers')
        .controller('dashboard', ['$scope', '$filter', 'rtcWebApp.services.rtcWebAppService', 'rtcWebApp.services.progressService', DashboardController])

    function DashboardController($scope, $filter, rtcService, progressService) {
        var self = this;

        $scope.projectAreas = [];
        $scope.teamAreas = [];
        $scope.sprints = [];
        $scope.workItems = [];
        $scope.today = new Date();
        $scope.selectedProjectArea = null;
        $scope.selectedSprint = null;
        $scope.selectedTeam = null;
        $scope.RFD = null;
        $scope.InProgress = null;
        $scope.Done = null;
        $scope.IST = null;

        $scope.chartData = [];
        $scope.chartLabels = [];

        self.getWorkItems = function() {
            progressService.showProgressBar();
            rtcService.getWorkItems($scope.selectedProjectArea.name, $scope.selectedSprint, $scope.selectedTeam).then(function(response){
                $scope.workItems = response.data;

                $scope.closed = $filter('filter')($scope.workItems, {status: 'Closed'} || {status: 'Delivered'});
                $scope.open = $filter('filter')($scope.workItems, {status: 'New'} || {status: 'Not Done'} || {status: 'Ready for Sizing'} || {status: 'Ready for Dev'});
                $scope.working = $filter('filter')($scope.workItems, {status: 'In Development'} || {status: 'In Progress'});
                $scope.scrumTest = $filter('filter')($scope.workItems, {status: 'In Scrum Test'});

                $scope.chartData = [];
                $scope.chartData.push($scope.open.length);
                $scope.chartData.push($scope.working.length);
                $scope.chartData.push($scope.scrumTest.length);
                $scope.chartData.push($scope.closed.length);

                $scope.chartLabels = [];
                $scope.chartLabels.push('Ready For Dev');
                $scope.chartLabels.push('In Progress');
                $scope.chartLabels.push('In Scrum Test');
                $scope.chartLabels.push('Done');

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
        self.getTeamAreas();
    }
})();