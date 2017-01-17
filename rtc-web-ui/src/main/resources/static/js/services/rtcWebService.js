(function(){
    'use strict';

    angular
        .module('rtcWebApp.services')
        .service('rtcWebApp.services.rtcWebAppService', ['$http', 'rtcWebApp.api', RTCWebService]);

    function RTCWebService($http, api) {
        var self = this;

        self.getWorkItems = function(projectArea, sprint, team) {
            return $http.get(api.workitem + 'findBySprint?projectArea=' + projectArea + '&sprint=' + sprint + '&team=' + team);
        };

        self.getProjectAreas = function() {
            return $http.get(api.dashboard + 'getProjectAreas');
        };

        self.getTeamAreas = function () {
            return $http.get(api.dashboard + 'getTeamAreas');
        };

        self.getSprints = function(projectAreaName) {
            return $http.get(api.dashboard + 'getSprints?projectAreaName=' + projectAreaName);
        };

        self.getCategories = function(projectAreaName) {
            return $http.get(api.dashboard + 'getCategories?projectAreaName=' + projectAreaName);
        }
    }
})();