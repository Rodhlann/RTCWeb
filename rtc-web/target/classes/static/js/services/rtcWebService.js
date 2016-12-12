(function(){
    'use strict';

    angular
        .module('rtcWebApp.services')
        .service('rtcWebApp.services.rtcWebAppService', ['$http', 'rtcWebApp.api', RTCWebService]);

    function RTCWebService($http, api) {
        var self = this;

        self.login = function(rtcURL, userName, password) {
            return $http.get(api.authenticate + 'login?url=' + rtcURL + '&userName=' + userName + '&password=' + password);
        };

        self.logout = function() {
            return $http.get(api.authenticate + 'logout');
        }

        self.getWorkItems = function() {
            var projectWorkArea = '';
            var workItemId = '';
            return $http.get(api.workitem + 'get?projectWorkArea=' + projectWorkArea + '&workItemId=' + workItemId);
        };

        self.getProjectAreas = function() {
            return $http.get(api.dashboard + 'getProjectAreas');
        };
    }
})();