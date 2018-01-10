'use strict';

angular.module('artirestApp')
    .factory('StatisticModelService', function ($resource) {
        return $resource('api/statisticModels/:id', {}, {
            'findById': {method: 'GET'},
        });
    });
