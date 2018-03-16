(function() {
    'use strict';

    angular
        .module('artirestApp')
        .controller('CoordinatorDeleteController',CoordinatorDeleteController);

    CoordinatorDeleteController.$inject = ['$uibModalInstance', 'entity', 'Coordinator'];

    function CoordinatorDeleteController($uibModalInstance, entity, Coordinator) {
        var vm = this;

        vm.coordinator = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Coordinator.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
