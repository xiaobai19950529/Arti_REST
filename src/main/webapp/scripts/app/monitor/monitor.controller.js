angular.module('artirestApp')
    .controller('MonitorController', function ($scope, $state, ProcessModel, ParseLinks, StatisticModelsService) {

        $scope.processModels = [];
        $scope.predicate = 'id';
        $scope.reverse = false;
        $scope.page = 1;

        $scope.ratio_instance = 0;
        $scope.ratio_running = 0;
        $scope.ratio_pending = 0;
        $scope.ratio_ended = 0;

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

            $scope.ratio_instance = 1;
            $scope.ratio_running = running / instance;
            console.log($scope.ratio_running);
            $scope.ratio_pending = pending / instance;
            console.log($scope.ratio_pending);
            $scope.ratio_ended = ended / instance;
            console.log($scope.ratio_ended);

            console.log($scope.processModels.id);
        });

        function compareCreatedAt(a,b) {
            return a.createdAt > b.createdAt;
        }

        $scope.loadAll = function () {
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

        function addKeyFrame(deg) {

        }
    });

'use strict';
