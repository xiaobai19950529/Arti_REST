package com.raysmond.artirest.MetricImp;

import com.codahale.metrics.Gauge;
import com.raysmond.artirest.domain.Artifact;
import com.raysmond.artirest.domain.ArtifactModel;
import com.raysmond.artirest.domain.StateModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class StateMetric implements Gauge<Integer> {
    private String state;
    private ArtifactModel artifactModel;

    public StateMetric(ArtifactModel artifactModel,String state){
        this.state = state;
        this.artifactModel = artifactModel;
    }

    @Override
    public Integer getValue() {
        return metricValue(artifactModel,state);
    }

    public Integer metricValue(ArtifactModel artifactModel,String state){
        List<StateModel> states = new ArrayList<>();
        Queue<StateModel> queue = new LinkedList<StateModel>();
        //找到开始状态
        StateModel startState = new StateModel();
        for(StateModel stateModel : artifactModel.states){
            if(stateModel.type == StateModel.StateType.START){
                startState.name = stateModel.name;
                startState.type = stateModel.type;
                startState.nextStates = stateModel.nextStates;
                startState.comment = stateModel.comment;
                break;
            }
        }

        states.add(startState);
        //将开始状态添加进队列中
        queue.add(startState);
        while(!queue.isEmpty()){
            StateModel statesearch = queue.remove(); //移除并返回队头元素

            System.out.println(statesearch.name);
            if(statesearch.type == StateModel.StateType.FINAL) continue;

            //将当前状态的nextStates都加入队列
            for(String name : statesearch.nextStates){
                for(StateModel s : artifactModel.states)
                {
                    if(s.name.equals(name)){ //如果找到了当前的状态
                        states.add(s);
                        queue.add(s);
                        break;
                    }
                }
            }
        }

        System.out.println("状态的数量：" + states.size());
        int i = 0;

        for(StateModel stateModel : states){
            i++;
            if(stateModel.name.equals(state)){
                return i;
            }
        }
        return -1;
    }
}
