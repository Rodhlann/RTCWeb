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
        $scope.categories = [];
        $scope.workItems = [];

        $scope.today = new Date();
        $scope.selectedProjectArea = null;
        $scope.selectedSprint = null;
        $scope.selectedTeam = null;
        $scope.selectedTags = [];

        $scope.chartData = [];
        $scope.chartLabels = [];

        $scope.defectChartData = [];


        self.getWorkItems = function() {
            progressService.showProgressBar();
            rtcService.getWorkItems($scope.selectedProjectArea.name, $scope.selectedSprint, $scope.selectedTeam).then(function(response){
                $scope.workItems = response.data;

                $scope.doneWorkItems = $filter('filter')($scope.workItems, {status: 'DONE', type: '!DEFECT'});
                $scope.defectsDone = $filter('filter')($scope.workItems, {status: 'DONE', type: 'DEFECT'});

                $scope.rfdWorkItems = $filter('filter')($scope.workItems, {status: 'READY_FOR_DEVELOPMENT', type: '!DEFECT'});
                $scope.defectsRFD = $filter('filter')($scope.workItems, {status: 'READY_FOR_DEVELOPMENT', type: 'DEFECT'});

                $scope.inProgressWorkItems = $filter('filter')($scope.workItems, {status: 'IN_PROGRESS', type: '!DEFECT'});
                $scope.defectsInProgress = $filter('filter')($scope.workItems, {status: 'IN_PROGRESS', type: 'DEFECT'});

                $scope.inScrumTestWorkItems = $filter('filter')($scope.workItems, {status: 'IN_SCRUM_TEST', type: '!DEFECT'});
                $scope.defectsInScrumTest = $filter('filter')($scope.workItems, {status: 'IN_SCRUM_TEST', type: 'DEFECT'});

                $scope.unknownWorkItems = $filter('filter')($scope.workItems, {status: 'UNKNOWN', type: '!DEFECT'});
                $scope.defectsUnknown = $filter('filter')($scope.workItems, {status: 'UNKNOWN', type: 'DEFECT'});

                $scope.storyCount = $filter('filter')($scope.workItems, {type: 'STORY'}).length;
                $scope.taskCount = $filter('filter')($scope.workItems, {type: 'TASK'}).length;
                $scope.epicCount = $filter('filter')($scope.workItems, {type: 'EPIC'}).length;
                $scope.defectCount = $filter('filter')($scope.workItems, {type: 'DEFECT'}).length;
                $scope.unknownCount = $filter('filter')($scope.workItems, {type: 'UNKNOWN'}).length;

                $scope.chartData = [];
                $scope.chartData.push($scope.rfdWorkItems.length);
                $scope.chartData.push($scope.inProgressWorkItems.length);
                $scope.chartData.push($scope.inScrumTestWorkItems.length);
                $scope.chartData.push($scope.doneWorkItems.length);
                $scope.chartData.push($scope.unknownWorkItems.length);

                $scope.chartLabels = [];
                $scope.chartLabels.push('Ready For Dev');
                $scope.chartLabels.push('In Progress');
                $scope.chartLabels.push('In Scrum Test');
                $scope.chartLabels.push('Done');
                $scope.chartLabels.push('Unknown');

                $scope.defectChartData = [];
                $scope.defectChartData.push($scope.defectsRFD.length);
                $scope.defectChartData.push($scope.defectsInProgress.length);
                $scope.defectChartData.push($scope.defectsInScrumTest.length);
                $scope.defectChartData.push($scope.defectsDone.length);
                $scope.defectChartData.push($scope.defectsUnknown.length);

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
                self.getCategories();
            });
        };

        self.getTeamAreas = function() {
            progressService.showProgressBar();
            rtcService.getTeamAreas().then(function(response) {
                $scope.teamAreas = response.data;
                progressService.hideProgressBar();
            });
        };

        self.getCategories = function() {
            progressService.showProgressBar();
            rtcService.getCategories($scope.selectedProjectArea.name).then(function(response) {
                $scope.categories = response.data;
                progressService.hideProgressBar();
            });
        }

        self.getProjectAreas();
        self.getTeamAreas();
    }
})();