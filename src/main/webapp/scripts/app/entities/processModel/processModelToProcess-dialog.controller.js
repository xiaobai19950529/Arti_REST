'use strict';

angular.module('artirestApp').controller('ProcessModelToProcessDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', '$http', 'entity', 'Process', 'ProcessModel',
        function($scope, $stateParams, $uibModalInstance, $http, entity, Process, ProcessModel) {
            $scope.process = entity;
            $scope.processModel = entity;
            $scope.instances = {};

            $scope.load = function(id) {
                ProcessModel.get({id: id}, function(result) {
                    $scope.processModel = result;

                    //$timeout($scope.showStatesFlowcharts(), 1000);

                    //$scope.loadInstances(); //从后台加载实例
                });
            };

            $scope.load($stateParams.id);

            var onSaveSuccess = function (result) {
                $scope.$emit('artirestApp:processUpdate', result);
                $uibModalInstance.close(result);
                $scope.isSaving = false;
            };

            var onSaveError = function (result) {
                $scope.isSaving = false;
            };

            // $scope.loadInstances = function(){
            //     $http.get('/api/processModels/'+$scope.processModel.id+'/processes') //从后台ProcessResource拿数据
            //         .then(function(res){
            //             $scope.instances = res.data;
            //         }, function(res){
            //
            //         });
            // };


            $scope.save = function () {
                $scope.isSaving = true;
                if ($scope.process.id != null) {
                    Process.update($scope.process, onSaveSuccess, onSaveError);
                } else {
                    Process.save($scope.process, onSaveSuccess, onSaveError);
                    console.log($scope.processModel);
                    // $http.post('/api/processModels/'+$scope.processModel.id+'/processes', {})
                    //     .then(function(res){
                    //         $scope.loadInstances();
                    //     }, function(res){
                    //
                    //     });
                    // $scope.loadInstances();
                }
            };

            $scope.clear = function() {
                $uibModalInstance.dismiss('cancel');
            };

        }]);
