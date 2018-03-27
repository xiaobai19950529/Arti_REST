angular.module('artirestApp')
    .controller('CoordinatorDoMatchController', function ($scope, $state, $stateParams, $timeout, $window, Process, ParseLinks, Coordinator) {
        $scope.coordinator = {};
        $scope.process1 = null;
        $scope.process2 = null;
        $scope.artifact1 = null;
        $scope.artifact2 = null;
        $scope.mappedAttrs = [];  // [{attr1:{name:'xxx',comment:'yyy'}, attr2:{name:'xxx',comment:'yyy'}}]
        $scope.selectedAttr1 = null;
        $scope.selectedAttr2 = null;

        $scope._onResize = function() {
            var el_col1 = angular.element('#map-col-1')[0];
            var el_col2 = angular.element('#map-col-2')[0];
            $scope._svg.style("height", Math.max(el_col1.offsetHeight, el_col2.offsetHeight));
        }

        var appWindow = angular.element($window);

        appWindow.bind('resize', $scope._onResize);

        $scope.init = function() {
            Process.get({id : $stateParams.id1}, function(result) {
                $scope.process1 = result;
                // $scope.process1.artifacts size check
                $scope.artifact1 = $scope.process1.processModel.artifacts[0];
                $scope.artifact1.unmappedAttrs = $scope.artifact1.attributes.slice();
                $timeout($scope._onResize); // trick, fix later

                $scope.coordinator.firstProcessId = $scope.process1.id;
                $scope.coordinator.firstProcessName = $scope.process1.name;
            });
            Process.get({id : $stateParams.id2}, function(result) {
                $scope.process2 = result;
                $scope.artifact2 = $scope.process2.processModel.artifacts[0];
                $scope.artifact2.unmappedAttrs =$scope.artifact2.attributes.slice();
                $timeout($scope._onResize);

                $scope.coordinator.secondProcessId = $scope.process2.id;
                $scope.coordinator.secondProcessName = $scope.process2.name;
            });


            $scope._svg = d3.select("#mappingPanel").append("svg:svg")
                .attr("id","svg_mapping_tool")
                .style("width","100%");
            $scope._dot1 = this._svg.append("svg:g").attr("id","dot1");
            $scope._dot2 = this._svg.append("svg:g").attr("id","dot2");
            $scope._line = this._svg.append("svg:g").attr("id","line");

        };

        $scope.init();



        $scope.onSelectAttr = function(attr, num) {
            $scope['selectedAttr'+num] = attr;
            $scope['selectedAttr'+num].name = angular.element(attr.target).parent().children("td.name").text();
            $scope['selectedAttr'+num].comment = angular.element(attr.target).parent().children("td.comment").text();
            $scope._updateLine();
        }

        $scope._updateLine = function () {
            for (var num = 1; num <= 2; num++) {
                var attr = $scope['selectedAttr' + num];
                if (attr) {
                    var offsetY =
                        angular.element(attr.target).offset().top
                        -angular.element(attr.target).parent().parent().offset().top
                        + angular.element(attr.target).height()/2
                        +8;

                    if($scope['_dot'+num].select("circle").size()==0) {
                        $scope['_dot'+num].append("svg:circle")
                            .attr("cx", num==1?4:$scope._svg.node().getBoundingClientRect().width-4)
                            .attr("cy", offsetY)
                            .attr("r", 4)
                            .attr("fill", "#ccc");
                    }else {
                        $scope['_dot'+num].select("circle")
                            .attr("cy", offsetY);
                    }
                }
            }

            if($scope['selectedAttr1'] && $scope['selectedAttr2']) {
                var path_data = [{
                    source: {
                        x: $scope._dot1.select("circle").attr('cx'),
                        y: $scope._dot1.select("circle").attr('cy')
                    },
                    target: {
                        x: $scope._dot2.select("circle").attr('cx'),
                        y: $scope._dot2.select("circle").attr('cy')
                    },
                }];

                $scope._line.select("path").remove();
                $scope._line
                    .append("path")
                    .data(path_data)
                    .attr("d", d3.linkHorizontal()
                        .x(function(d) { return d.x; })
                        .y(function(d) { return d.y; }))
                    .attr("stroke-width", "1px")
                    .attr("stroke", "#ccc")
                    .attr("fill", "none");
            }
        }

        $scope.onConfirm = function () {
            var rule = {
                attr1: $scope.selectedAttr1,
                attr2: $scope.selectedAttr2
            };

            for (var i = 0; i < $scope.mappedAttrs.length; i++) {
                r = $scope.mappedAttrs[i];
                if (r.attr1 == rule.attr1 && r.attr2 == rule.attr2) {
                    //s
                    return;
                }
            }

            $scope.mappedAttrs.push(rule);
            $scope.artifact1.unmappedAttrs = $.grep($scope.artifact1.unmappedAttrs, function (d) {
                return d.name != rule.attr1.name;
            });

            $scope.artifact2.unmappedAttrs = $.grep($scope.artifact2.unmappedAttrs, function (d) {
                return d.name != rule.attr2.name;
            });

            $scope.selectedAttr1 = null;
            $scope.selectedAttr2 = null;
            $scope._line.select("path").remove();
            $scope._dot1.select("circle").remove();
            $scope._dot2.select("circle").remove();

        }

        var onSaveSuccess = function (result) {
            $scope.$emit('artirestApp:processModelUpdate', result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.saveCoordinator = function () {
            for(var i = 0; i < $scope.mappedAttrs.length; i++){
                var new_item = {};
                new_item.firstProcessAttr = $scope.mappedAttrs[i].attr1.name;
                new_item.secondProcessAttr = $scope.mappedAttrs[i].attr2.name;
                new_item.firstProcessId = $scope.coordinator.firstProcessId;
                new_item.firstProcessName = $scope.coordinator.firstProcessName;
                new_item.secondProcessId = $scope.coordinator.secondProcessId;
                new_item.secondProcessName = $scope.coordinator.secondProcessName;
                Coordinator.save(new_item, onSaveSuccess, onSaveError);
            }

        }

    });

