'use strict';

angular.module('artirestApp')
    .controller('CoordinatorController', function ($scope, $state, Coordinator, ParseLinks) {
        $scope.coordinators = [];
        $scope.predicate = 'id';
        $scope.reverse = false;
        $scope.page = 1;
        $scope.loadAll = function() {
            Coordinator.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc')]}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.coordinators = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.coordinators = {
                firstProcessId: null,
                firstProcessName: null,
                firstProcessAttr: null,
                secondProcessId: null,
                secondProcessName: null,
                secondProcessAttr: null,
                id: null
            };
        };
    });
