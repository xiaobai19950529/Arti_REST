'use strict';

angular.module('artirestApp')
    .controller('ProcessLLController', function ($scope, $rootScope, $stateParams, $http, entity, ProcessModel,StatisticModelsService,Process) {
        // $scope.processModel = entity;
        console.log($stateParams.id);
        console.log($stateParams.type);
        console.log(typeof $stateParams.type);
        var instances_running = []; //Object类型
        var instances_pending = []; //Object类型
        var instances_ended = {};   //Object类型
        $scope.instances = [];
        $scope.statisticModels = {};


        $scope.statisticModels = StatisticModelsService.findAll();
        $scope.statisticModels.$promise.then(function (data) {
            var statisticModel = data[0];
            // console.log("processModelId: " + $stateParams.id);
            var processModelId = $stateParams.id;
            var stateNumberOfModels = statisticModel.stateNumberOfModels;
            $scope.stateNumberOfModel = stateNumberOfModels[processModelId];

        });

        $scope.load = function (id) {
            ProcessModel.get({id: id}, function(result) {
                $scope.processModel = result;

                $scope.loadInstances(); //从后台加载实例
            });
        };

        $scope.loadInstances = function(){
            $http.get('/api/processModels/'+$scope.processModel.id+'/processes') //从后台ProcessResource拿数据
                .then(function(res){
                    $scope.instances = res.data;
                    var instance_id = 0;
                    var running_id = 0;
                    var pending_id = 0;
                    var ended_id = 0;
                    for(var i in $scope.instances){
                        if($scope.instances[i].artifacts.length === 0){ //判断是否为初始状态，初始状态无artifact
                            instances_pending[pending_id++] = $scope.instances[i];
                        }
                        else{ //判断currentState
                            var currentState = $scope.instances[i].artifacts[0].currentState;
                            var artifactModel = $scope.instances[i].artifacts[0].artifactModel;
                            var startState = artifactModel.startState.name;
                            if(currentState === startState){
                                instances_pending[pending_id++] = $scope.instances[ii];
                            }
                            else{
                                var flag = false;
                                for(var endState in artifactModel.endStates) {
                                    if (currentState === artifactModel.endStates[endState].name) {
                                        instances_ended[ended_id++] = $scope.instances[i];
                                        flag = true; //是结束状态中的一个，直接添加进ended，然后跳出循环
                                        break;
                                    }
                                }
                                if(!flag){
                                    instances_running[running_id++] = $scope.instances[i];
                                }
                            }
                        }
                    }
                    //判断要查询的类型
                    var type = $stateParams.type;
                    if(type === "instance"){
                        $scope.instances_type = $scope.instances;
                    }
                    else if(type === "running"){
                        $scope.instances_type = instances_running;
                    }
                    else if(type === "pending"){
                        $scope.instances_type = instances_pending;
                    }
                    else if(type === "ended"){
                        $scope.instances_type = instances_ended;
                    }
                    console.log($scope.instances_type);
                }, function(res){

                });
        };

        $scope.load($stateParams.id);

        $scope.onload = function () {
            console.log("哈哈哈");
        };
    });
