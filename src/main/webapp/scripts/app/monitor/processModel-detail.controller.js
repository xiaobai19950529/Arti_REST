'use strict';

angular.module('artirestApp')
    .controller('ProcessModelDetailController2', function ($scope, $rootScope, $state, $stateParams, $timeout, $http, $uibModal, entity, ProcessModel,ArtifactModel,StatisticModelsService,Process) {
        $scope.processModel = entity;
        $scope.instances = {};
        $scope.statisticModels = {};

        $scope.ratio = 1;

        $scope.statisticModels = StatisticModelsService.findAll();
        console.log("statisticModels:");
        console.log($scope.statisticModels);
        $scope.statisticModels.$promise.then(function(data){
            var statisticModel = data[0];
            console.log("processModelId: " + $stateParams.id);
            var processModelId = $stateParams.id;
            var stateNumberOfModels = statisticModel.stateNumberOfModels;
            console.log(stateNumberOfModels);
            $scope.stateNumberOfModel = stateNumberOfModels[processModelId];
             console.log($scope.stateNumberOfModel);

            var instances = $scope.stateNumberOfModel.instance;
            console.log(instances);
            console.log(stateNumberOfModels[processModelId].instances);
            $scope.ratio_instance = 1;
            $scope.ratio_running = stateNumberOfModels[processModelId].running / instances;
            $scope.ratio_pending = stateNumberOfModels[processModelId].pending / instances;
            $scope.ratio_ended = stateNumberOfModels[processModelId].ended / instances;
            console.log($scope.ratio_running / $scope.ratio_pending);
        });

        $scope.load = function (id) {
            ProcessModel.get({id: id}, function(result) {
                $scope.processModel = result; //作用域仅在此函数
                //$timeout($scope.showStatesFlowcharts(), 1000);
                setTimeout(function(){
                    $scope.showStatesFlowcharts();
                }, 10);

                $scope.loadInstances(); //从后台加载实例
            });

        };

        $scope.load($stateParams.id);



        $scope.parseLifeCycle = function(artifact){
            var key = 'myDiagram-'+artifact.id;
            var json = eval('('+myDiagrams[key].model.toJson() + ')');
            console.log(json);
            var states = [];
            var stateKeyMap = {};
            for(var i=0;i<json.nodeDataArray.length;i++){
                var n = json.nodeDataArray[i];
                var state = {
                    name: n.text,
                    type: n.category ? (n.category == 'Start' ? 'START' : 'FINAL') : 'NORMAL',
                    comment: n.text,
                    nextStates: []
                };
                states.push(state);
                stateKeyMap[n.key] = i;
            }

            for(var i=0;i<json.linkDataArray.length;i++){
                var link = json.linkDataArray[i];
                states[stateKeyMap[link.from]].nextStates.push(states[stateKeyMap[link.to]].name);
            }
            return states;
        };

        $scope.saveEditArtifact = function(artifact){
            artifact.states = $scope.parseLifeCycle(artifact);
            $scope.saveArtifact(artifact);
        };

        $scope.loadInstances = function(){
            $http.get('/api/processModels/'+$scope.processModel.id+'/processes')
                .then(function(res){
                    $scope.instances = res.data;
                    // console.log(res.data);
                    // console.log($scope.instances);
                    for (var process_index in $scope.instances) {
                        // console.log("processName: ")
                        // console.log($scope.instances[process_index]);
                        for (var artifact_index in $scope.instances[process_index].artifacts) {
                            // console.log("name = " + artifact_index);
                            if ($scope.instances[process_index].artifacts == null) {
                                $scope.instances[process_index].artifacts = "NULL";
                            }
                        }
                    }
                }, function(res){

                });
        };

        // $scope.instances.$promise.then(function(data) {
        //     console.log("instances : " + $scope.instances);
        //     for (var process in $scope.instances) {
        //         console.log("processName: ")
        //         console.log(process.name);
        //         for (var artifact in process.artifacts) {
        //             console.log("name = " + artifact.name);
        //             if (artifact.currentState == null) {
        //                 artifact.currentState = "NULL";
        //             }
        //         }
        //     }
        // });

        $scope.attrTypes = ['String','Long','Integer',"Double",'Float','Text','Date'];

        $scope.saveArtifact = function(artifact){
            console.log(artifact);
            ArtifactModel.update(artifact, function(res){

            }, function(res){});
        };

        $scope.newAttr = {name: null, type: null, comment: null};
        $scope.addAttr = function(artifact){
            if($scope.newAttr.name && $scope.newAttr.type && $scope.newAttr.comment){
                artifact.attributes.push($scope.newAttr);
               // $scope.saveArtifact(artifact);
                $scope.newAttr = {name: null, type: null, comment: null};
            }
        };


        $scope.removeAttr = function(artifact, attr){
            var idx =artifact.attributes.indexOf(attr);
            if(idx!=-1){
                artifact.attributes.splice(idx,1); //splice() 方法可删除从 index 处开始的零个或多个元素
                //$scope.saveArtifact(artifact);
            }
        };

        $scope.toggleEditAttr = function(artifact, attr){
            var idx = artifact.attributes.indexOf(attr);
            if(idx==-1) return;
            var key = '#artifact-'+artifact.id + ' tr.artifact-attr';
            var attrRow = $(key)[idx];
        };


        //必要的
        var unsubscribe = $rootScope.$on('artirestApp:processModelUpdate', function(event, result) {
            $scope.processModel = result;
        });

        $scope.$on('$destroy', unsubscribe);
        $scope.key = 0;

        $scope.addNode = function(nodes, edges, states, state, x, y, fromKey){ //递归函数
            var num = $scope.stateNumberOfModel.statenumber[state["name"]];
            var node = {
                "key" : $scope.key++,
                "text" : state["comment"] + "(" + num + ")",
                "loc" : "" + (x + 130) + " " + y
            };

            if(state["type"] == 'START')
                node["category"] = "Start";
            else if(state["type"] == 'FINAL')
                node["category"] = "End";

            nodes.push(node);


            if(state["type"] != "START"){
                edges.push({
                               "from" : fromKey,
                               "to" : node.key,
                               "fromPort" : x < 100 && state["type"] != "START" ? "B" : "R",
                               "toPort" : "L",
                               "text": 'r1'
                           });
            }

            var maxWidth = $(".artifact-list").width();

            var fromKey = $scope.key - 1;
            for (var i = 0; i < state.nextStates.length; i++) {　//找出该状态的下一个状态(可能是0或者多个)，需通过遍历找出对应的state数组
                for(var j=0;j<states.length;j++){
                    if(states[j]["name"] == state.nextStates[i]){
                        if(x+280>maxWidth)
                            $scope.addNode(nodes, edges, states, states[j], 20, y + 100 * i + 100, fromKey);
                        else
                            $scope.addNode(nodes, edges, states, states[j], x + 170, y + 100 * i, fromKey);

                    }
                }
            }
        };

        $scope.showStatesFlowcharts = function(){
            $scope.artifacts = $scope.processModel.artifacts;
            for (var i = 0; i < $scope.artifacts.length; i++) {
                var artifact = $scope.artifacts[i];
                var start;
                var states = artifact.states;

                //找到初始状态
                for (var j = 0; j < states.length; j++) {
                    var state = states[j];
                    if(state["type"] == 'START'){
                        start = state;
                    }
                };

                var nodes = [];
                var edges = [];

                //找到了，开始放置图形的位置
                if(start)
                    $scope.addNode(nodes, edges, states, start, 0, 200, 0);

                var json = {
                    "class": "go.GraphLinksModel",
                    "linkFromPortIdProperty": "fromPort",
                    "linkToPortIdProperty": "toPort",
                    "nodeDataArray": nodes,
                    "linkDataArray": edges
                };

                console.log(json);

                if (json.nodeDataArray.length < 8) {
                    $("#myDiagram-"+artifact.id).css("height", "400px");
                };

                initFlowchart("myDiagram-"+artifact.id);
                loadFlowchartFromJson("myDiagram-"+artifact.id, json);
            };
        };


    });
