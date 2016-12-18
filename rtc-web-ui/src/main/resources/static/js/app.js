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
    .config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
        $routeProvider
            .when('/', {
                templateUrl : 'home.html',
                controller : 'home'
            })
            .when('/login', {
                templateUrl : 'login.html',
                controller : 'navigation'
            })
            .otherwise('/');

        $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
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
        authenticate: 'auth',
        dashboard: 'dashboard/',
        workitem: 'workitem/'
    });

    angular.module('rtcWebApp.controllers', []);
    angular.module('rtcWebApp.services', []);
})();