(function(){
    'use strict';

    angular
        .module('rtcWebApp.services')
        .service('rtcWebApp.services.rtcWebAppService', ['$http', 'rtcWebApp.api', RTCWebService]);

    function RTCWebService($http, api) {
        var self = this;

        self.getWorkItems = function() {
            var projectWorkArea = '';
            var workItemId = '';
            return $http.get(api.workitem + 'get?projectWorkArea=' + projectWorkArea + '&workItemId=' + workItemId);
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
    }
})();