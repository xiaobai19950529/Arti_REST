'use strict';

angular.module('artirestApp')
    .controller('ProcessModelController', function ($scope, $state, ProcessModel, ParseLinks) {
        $scope.processModels = [];
        $scope.predicate = 'createdAt';
        $scope.reverse = false;
        $scope.page = 1;
        $scope.itemsperpage = 10;
        $scope.loadAll = function() {
            ProcessModel.query({page: $scope.page - 1, size: $scope.itemsperpage, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc')]}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.processModels = result;
                console.log(result);
                console.log($scope.links);
                console.log("哈哈");
                console.log($scope.totalItems);
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
            $scope.processModel = {
                name: null,
                comment: null,
                status: null,
                createdAt: null,
                updatedAt: null,
                id: null
            };
        };

        $scope.getDisplayName = function(model) {
        };
    });
