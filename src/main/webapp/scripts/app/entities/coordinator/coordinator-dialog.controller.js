(function() {
    'use strict';

    angular
        .module('artirestApp')
        .controller('CoordinatorDialogController', CoordinatorDialogController);

    CoordinatorDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Coordinator'];

    function CoordinatorDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Coordinator) {
        var vm = this;

        vm.coordinator = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.coordinator.id !== null) {
                Coordinator.update(vm.coordinator, onSaveSuccess, onSaveError);
            } else {
                Coordinator.save(vm.coordinator, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('artirestApp:coordinatorUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
