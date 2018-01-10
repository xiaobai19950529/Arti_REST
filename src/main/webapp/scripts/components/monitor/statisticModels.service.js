'use strict';

angular.module('artirestApp')
    .factory('StatisticModelsService', function ($resource) {
        return $resource('api/statisticModels', {}, {
            'query': { method: 'GET', isArray: true},
            'findAll': {method: 'GET', isArray: true},
            'update': { method:'PUT' },
            'findById': {method: 'GET', isArray: false},
        });
    });
