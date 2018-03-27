(function() {
    'use strict';
    angular
        .module('artirestApp')
        .factory('Coordinator', Coordinator);

    Coordinator.$inject = ['$resource'];

    function Coordinator ($resource) {
        var resourceUrl =  'api/coordinators/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
