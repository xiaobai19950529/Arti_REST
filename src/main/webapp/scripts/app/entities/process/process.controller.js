'use strict';

angular.module('artirestApp')
    .controller('ProcessController', function ($scope, $state, Process, ParseLinks) {

        $scope.processs = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.itemsperpage = 10;
        $scope.loadAll = function() {
            Process.query({page: $scope.page - 1, size: $scope.itemsperpage, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']},"SSS", function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.processs = result;
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
            $scope.process = {
                name: null,
                isRunning: null,
                createdAt: null,
                updatedAt: null,
                id: null
            };
        };
    });
