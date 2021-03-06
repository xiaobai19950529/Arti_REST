'use strict';

angular.module('artirestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('monitor', {
                parent: 'site',
                url: '/monitor',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'Monitor'
                },
                views: {
                    'content@': {
                        //templateUrl: 'scripts/app/entities/processModel/processModels.html',
                        templateUrl: 'scripts/app/monitor/monitor.html',
                        controller: 'MonitorController'
                    }
                },
                resolve: {

                }
            })
            .state('processModel.detail-monitor', {
                parent: 'site',
                url: '/processModel-monitor/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'ProcessModel'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/monitor/processModel-detail2.html',
                        controller: 'ProcessModelDetailController2'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'ProcessModel', function($stateParams, ProcessModel) {
                        //return ProcessModel.get({id : $stateParams.id});
                        return {};
                    }]
                }
            })
            .state('process.detail-monitor', {
                parent: 'site',
                url: '/process-monitor/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Process'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/monitor/process-detail2.html',
                        controller: 'ProcessDetailController2'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Process', function($stateParams, Process) {
                        //return Process.get({id : $stateParams.id});
                        return {};
                    }]
                }
            })
            .state('process.list-monitor', {
                parent: 'site',
                url: '/processModel-monitor/{id}/{type}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Process'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/monitor/process-list.html',
                        controller: 'ProcessLLController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Process', function($stateParams, Process) {
                        //return Process.get({id : $stateParams.id});
                        return {};
                    }]
                }
                // onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                //     $uibModal.open({
                //         templateUrl: 'scripts/app/monitor/process-list.html',
                //         controller: 'ProcessLLController',
                //         size: 'lg',
                //         resolve: {
                //             entity: function () {
                //                 return {};
                //             }
                //         }
                //     }).result.then(function(result) { //成功执行这个函数
                //         console.log("成功执行");
                //         $state.go('processModel.detail-monitor', null, { reload: true });
                //     }, function() { //关闭或取消执行此函数
                //         console.log("失败");
                //         $state.go('processModel.detail-monitor');
                //     })
                // }]
            });


    });
