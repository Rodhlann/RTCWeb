(function(){
    'use strict';

    angular
        .module('rtcWebApp.services')
        .service('rtcWebApp.services.progressService', [ProgressService]);

    function ProgressService() {
        var self = this;

        self.processing = false;
        self.callbacks = [];

        self.isProcessing = function() {
            return self.processing;
        };

        self.registerCallback = function(callback) {
            self.callbacks.push(callback);
        };

        self.showProgressBar = function() {
            self.processing = true;
            self.notifyCallbacks();
        };

        self.hideProgressBar = function() {
            self.processing = false;
            self.notifyCallbacks();
        };

        self.toggleProcessIndicator = function() {
            self.processing = !self.processing;
            self.notifyCallbacks();
        };

        self.notifyCallbacks = function() {
            self.callbacks.forEach(function(callback) {
                callback();
            });
        };
    }
})();