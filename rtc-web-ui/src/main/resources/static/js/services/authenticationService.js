(function(){
    'use strict';

    angular
        .module('rtcWebApp.services')
        .service('rtcWebApp.services.authService', ['$http', 'rtcWebApp.api', AuthService]);

    function AuthService($http, api) {
        var self = this;

        self.authenticated = null;
        self.username = null;
        self.callbacks = [];

        self.isAuthenticated = function() {
            return (self.authenticated !== null) ? self.authenticated : false;
        };

        self.getUserName = function() {
            return self.username;
        };

        self.authenticate = function(credentials, callback) {
            var headers = credentials ? {authorization: "Basic " + btoa(credentials.username + ":" + credentials.password) } : {};
            $http.get(api.authenticate, { headers : headers })
                .then(function(response) {
                    if(response.data.name) {
                        self.authenticated = true;
                        self.username = response.data.name;
                    } else {
                        self.setLoggedOut();
                    }
                })
                .catch(function(response) {
                    self.setLoggedOut();
                })
                .finally(function(){
                    self.notifyCallbacks();
                    callback && callback();
                });
        };

        self.logout = function(callback) {
            $http.post('logout', {})
                .finally(function() {
                    self.setLoggedOut();
                    self.notifyCallbacks();
                    callback && callback();
                });
        };

        self.setLoggedOut = function() {
            self.authenticated = false;
            self.username = null;
        };

        self.registerCallback = function(callback) {
            self.callbacks.push(callback);
        };

        self.notifyCallbacks = function() {
            self.callbacks.forEach(function(callback) {
                callback();
            });
        };
    }
})();