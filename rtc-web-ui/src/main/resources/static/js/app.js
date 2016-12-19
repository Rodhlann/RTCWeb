(function(){

    'use strict';

    angular.module('rtcWebApp', [
        'ngRoute',
        'ngResource',
        'ngMaterial',
        'ngAnimate',
        'ngMdIcons',
        'ngMessages',

        'rtcWebApp.controllers',
        'rtcWebApp.services'
    ])
    .config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
        $routeProvider
            .when('/', {
                templateUrl : '/views/dashboard.html',
                controller : 'dashboard',
                controllerAs: 'ctrl'
            })
            .when('/login', {
                templateUrl : 'login.html',
                controller : 'navigation'
            })
            .when('/dashboard', {
                templateUrl : '/views/dashboard.html',
                controller : 'dashboard',
                controllerAs: 'ctrl'
            })
            .otherwise('/');

        $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

        $httpProvider.interceptors.push(function($q, $injector, $location) {
            return {
                responseError: function(rejection) {
                    if (rejection.status === 401) {
                        $location.path("/login");
                    }

                    /* If not a 401, do nothing with this error.
                     * This is necessary to make a `responseError`
                     * interceptor a no-op. */
                    return $q.reject(rejection);
                }
            };
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
        authenticate: 'auth',
        dashboard: 'dashboard/',
        workitem: 'workitem/'
    });

    angular.module('rtcWebApp.controllers', []);
    angular.module('rtcWebApp.services', []);
})();