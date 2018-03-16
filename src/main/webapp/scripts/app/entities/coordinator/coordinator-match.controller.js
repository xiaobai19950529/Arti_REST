angular.module('artirestApp')
    .controller('CoordinatorMatchController', function ($scope, $state, Process, ParseLinks) {
        $scope.processes = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.firstSelectedProcess = null;
        $scope.secondSelectedProcess = null;
        $scope.firstSelectedArtifacts = [];


        $scope.loadAll = function() {
            Process.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.processes = result;
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
            $scope.process = {
                name: null,
                isRunning: null,
                createdAt: null,
                updatedAt: null,
                id: null
            };
        };

        $scope.rowClicked = function (row, num) {
            if(num == 1){
                $scope.firstSelectedProcess = row;
            }else {
                $scope.secondSelectedProcess = row;
            }

        }

        $scope.onConfirm = function () {
            console.log(123123123);
            $state.go("coordinator.doMatch",
                {
                    id1: $scope.firstSelectedProcess.id,
                    id2: $scope.secondSelectedProcess.id
                });
            console.log(456);
        }


        this.see = function(){
            console.log(firstSelectedProcess);
            console.log('aaa');
        }

        this.onchange = function(){
            console.log('bbb');
            return function (processes, name, firstSelectedProcess) {
                angular.forEach($scope.processes, function (item) {
                    //过滤数组中值与指定值相同的元素
                    if(item[name]!=firstSelectedProcess){
                        output.push(item);
                        console.log(item);
                    }
                });
                return firstSelectedArtifacts;
            }
        }
    });

