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

            $scope.save = function () {
                $scope.isSaving = true;
                if ($scope.process.id != null) {
                    //Process.update($scope.process, onSaveSuccess, onSaveError);
                } else {
                    //Process.save($scope.process, onSaveSuccess, onSaveError);
                    //console.log($scope.processModel);
                    $http.post('/api/processModels/'+$scope.processModel.id+'/processes',{"name":$scope.customerName})
                        .then(function(res){
                            $scope.$emit('artirestApp:processUpdate', res);
                            $uibModalInstance.close(res);
                            $scope.isSaving = false;
                        }, function(res){

                        });
                }
            };

            $scope.clear = function() {
                $uibModalInstance.dismiss('cancel');
            };

        }]);
