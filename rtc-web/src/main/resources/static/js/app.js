(function(){

    'use strict';

    console.debug("RTC Web App Starting")

    angular.module('rtcWebApp', [
        'ngRoute',
        'ngResource',
        'ngMaterial',
        'ngAnimate',
        'ngMdIcons',

        'rtcWebApp.controllers',
        'rtcWebApp.services'
    ])
    .config(['$routeProvider', function($routeProvider) {
        $routeProvider
            .when('/login', {
                templateUrl: '/views/login.html'
            })
            .when('/dashboard', {
                templateUrl: '/views/dashboard.html'
            });
    }])
    .config(['$mdThemingProvider', function($mdThemingProvider) {
        $mdThemingProvider.theme('default')
            .dark();
    }])
    .config(['$mdIconProvider', function($mdIconProvider) {
        $mdIconProvider
            .defaultIconSet('/images/materialdesignicons-webfont.svg');
    }])
    .value('rtcWebApp.api', {
        authenticate: 'authenticate/',
        dashboard: 'dashboard/',
        workitem: 'workitem/'
    })
    .value('user', {
        isAuthenticated: false,
        rtcURL: null,
        userName: null
    });

    angular.module('rtcWebApp.controllers', []);
    angular.module('rtcWebApp.services', []);
})();