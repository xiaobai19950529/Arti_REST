(function() {
    'use strict';

    angular
        .module('artirestApp')
        .controller('CoordinatorDetailController', CoordinatorDetailController);

    CoordinatorDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Coordinator'];

    function CoordinatorDetailController($scope, $rootScope, $stateParams, previousState, entity, Coordinator) {
        var vm = this;

        vm.coordinator = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('artirestApp:coordinatorUpdate', function(event, result) {
            vm.coordinator = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
