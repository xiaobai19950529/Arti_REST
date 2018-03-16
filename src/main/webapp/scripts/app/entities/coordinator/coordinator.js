(function () {
    'use strict';

    angular
        .module('artirestApp')
        .config(
            function ($stateProvider) {
                $stateProvider
                    .state('coordinator', {
                        parent: 'entity',
                        url: '/coordinator',
                        data: {
                            authorities: ['ROLE_USER'],
                            pageTitle: 'Coordinators'
                        },
                        views: {
                            'content@': {
                                templateUrl: 'scripts/app/entities/coordinator/coordinators.html',
                                controller: 'CoordinatorController'
                            }
                        },
                        resolve: {
                        }
                    })
                    .state('coordinator-detail', {
                        parent: 'coordinator',
                        url: '/coordinator/{id}',
                        data: {
                            authorities: ['ROLE_USER'],
                            pageTitle: 'Coordinator'
                        },
                        views: {
                            'content@': {
                                templateUrl: 'scripts/app/entities/coordinator/coordinator-detail.html',
                                controller: 'CoordinatorDetailController',
                                controllerAs: 'vm'
                            }
                        },
                        resolve: {
                            entity: ['$stateParams', 'Coordinator', function ($stateParams, Coordinator) {
                                return Coordinator.get({id: $stateParams.id}).$promise;
                            }],
                            previousState: ["$state", function ($state) {
                                var currentStateData = {
                                    name: $state.current.name || 'coordinator',
                                    params: $state.params,
                                    url: $state.href($state.current.name, $state.params)
                                };
                                return currentStateData;
                            }]
                        }
                    })
                    .state('coordinator-detail.edit', {
                        parent: 'coordinator-detail',
                        url: '/detail/edit',
                        data: {
                            authorities: ['ROLE_USER']
                        },
                        onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                            $uibModal.open({
                                templateUrl: 'scripts/app/entities/coordinator/coordinator-dialog.html',
                                controller: 'CoordinatorDialogController',
                                controllerAs: 'vm',
                                backdrop: 'static',
                                size: 'lg',
                                resolve: {
                                    entity: ['Coordinator', function (Coordinator) {
                                        return Coordinator.get({id: $stateParams.id}).$promise;
                                    }]
                                }
                            }).result.then(function () {
                                $state.go('^', {}, {reload: false});
                            }, function () {
                                $state.go('^');
                            });
                        }]
                    })
                    .state('coordinator.match', {
                        parent: 'entity',
                        url: '/coordinator-match',
                        data: {
                            authorities: ['ROLE_USER'],
                            pageTitle: 'Coordinator Match'
                        },
                        views: {
                            'content@': {
                                templateUrl: 'scripts/app/entities/coordinator/coordinator-match.html',
                                controller: 'CoordinatorMatchController'
                            }
                        },
                        resolve: {
                        }
                    })
                    .state('coordinator.doMatch', {
                        parent: 'entity',
                        url: '/coordinator-do-match/{id1}/{id2}',
                        data: {
                            authorities: ['ROLE_USER'],
                            pageTitle: 'Coordinator Match'
                        },
                        views: {
                            'content@': {
                                templateUrl: 'scripts/app/entities/coordinator/coordinator-do-match.html',
                                controller: 'CoordinatorDoMatchController'
                            }
                        },
                        resolve: {
                            loadPlugin: function($ocLazyLoad) {
                                return $ocLazyLoad.load([{
                                    serie: true,
                                    files: ['https://d3js.org/d3.v4.min.js']
                                }]);
                            }
                        }
                    })
                    .state('coordinator.edit', {
                        parent: 'coordinator',
                        url: '/{id}/edit',
                        data: {
                            authorities: ['ROLE_USER']
                        },
                        onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                            $uibModal.open({
                                templateUrl: 'scripts/app/entities/coordinator/coordinator-dialog.html',
                                controller: 'CoordinatorDialogController',
                                controllerAs: 'vm',
                                backdrop: 'static',
                                size: 'lg',
                                resolve: {
                                    entity: ['Coordinator', function (Coordinator) {
                                        return Coordinator.get({id: $stateParams.id}).$promise;
                                    }]
                                }
                            }).result.then(function () {
                                $state.go('coordinator', null, {reload: 'coordinator'});
                            }, function () {
                                $state.go('^');
                            });
                        }]
                    })
                    .state('coordinator.delete', {
                        parent: 'coordinator',
                        url: '/{id}/delete',
                        data: {
                            authorities: ['ROLE_USER']
                        },
                        onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                            $uibModal.open({
                                templateUrl: 'scripts/app/entities/coordinator/coordinator-delete-dialog.html',
                                controller: 'CoordinatorDeleteController',
                                controllerAs: 'vm',
                                size: 'md',
                                resolve: {
                                    entity: ['Coordinator', function (Coordinator) {
                                        return Coordinator.get({id: $stateParams.id}).$promise;
                                    }]
                                }
                            }).result.then(function () {
                                $state.go('coordinator', null, {reload: 'coordinator'});
                            }, function () {
                                $state.go('^');
                            });
                        }]
                    });
            }
        );
})();
