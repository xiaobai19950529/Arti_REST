'use strict';

angular.module('artirestApp')
    .controller('MonitorController', function ($scope, $state, ProcessModel, ParseLinks, StatisticModelsService) {

        $scope.processModels = [];
        $scope.predicate = 'id';
        $scope.reverse = false;
        $scope.page = 1;
        $scope.ratio = 190;

        $scope.statisticModels = {};

        $scope.statisticModels = StatisticModelsService.findAll();

        $scope.statisticModels.$promise.then(function(data){
            $scope.statisticModel = data[0];

            console.log($scope.statisticModel);
            var stateNumberOfModels = $scope.statisticModel.stateNumberOfModels;
            var instance = 0;
            var running = 0;
            var pending = 0;
            var ended = 0;

            for (var processModelId in stateNumberOfModels){
                var stateNumber = stateNumberOfModels[processModelId];
                instance += stateNumber.instance;
                running += stateNumber.running;
                pending += stateNumber.pending;
                ended += stateNumber.ended;
            }
            $scope.instance = instance;
            $scope.running = running;
            $scope.pending = pending;
            $scope.ended = ended;

            $scope.processModelNumber = $scope.statisticModel.modelnumber;
            console.log($scope.processModelNumber);
        });

        $scope.loadAll = function() {
            ProcessModel.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc')]}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.processModels = result;
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

    });
