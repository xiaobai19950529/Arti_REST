package com.raysmond.artirest.service;

import com.raysmond.artirest.domain.*;
import com.raysmond.artirest.domain.Process;
import com.raysmond.artirest.domain.enumeration.LogType;
import com.raysmond.artirest.repository.ArtifactModelRepository;
import com.raysmond.artirest.repository.ProcessModelRepository;
import com.raysmond.artirest.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by dashen on 17-6-1.
 */
@Service
public class FindService {

    @Autowired
    ArtifactModelRepository artifactModelRepository;

    @Autowired
    ProcessModelRepository processModelRepository;

    @Autowired
    ProcessRepository processRepository;



    public void sendmetrics(){
        System.out.println(processModelRepository.findAll().size());
        List<ProcessModel> processModels = processModelRepository.findAll();
        List<Process> processes = processRepository.findAll();


        System.out.println("流程模型个数: " + processModels.size());
        System.out.println("流程实例个数: " + processes.size());

    }

    int j = 0;

    public StatisticModel findStatisticModel(String artifactId, String processName,
                                             Map<String,StatisticModel> statisticModelcount, List<StateModel> list, Log log){
        StatisticModel statisticModel;
        if(!statisticModelcount.containsKey(processName)){
            statisticModel = new StatisticModel();
            statisticModel.processName = processName;

            StateCount stateCount = findstatetime(artifactId,statisticModel.statecount,list,log);
            statisticModelcount.put(processName,statisticModel);
            statisticModel.statecount.put(artifactId,stateCount);
            for(StateModel state1:list){
                StateNumber s = new StateNumber();
                s.name = state1.name;
                s.number = 0;
                statisticModel.statenumber.put(state1.name,s);
            }
            String start = list.get(0).name;
            StateNumber ss = statisticModel.statenumber.get(start);
            ss.number = 1;
            statisticModel.statenumber.put(start,ss);
        }
        else {
            statisticModel = statisticModelcount.get(processName);
            statisticModel = findstatenumber(artifactId,statisticModel,list,log);
            if(statisticModel.flag == 1) return statisticModel;

            StateCount stateCount = findstatetime(artifactId,statisticModel.statecount,list,log);
            statisticModel.statecount.put(artifactId,stateCount);
        }
        return statisticModel;
    }

    public StateCount findstatetime(String artifactId, Map<String,StateCount> statetime, List<StateModel> list, Log log){
        StateCount stateCount;
        if(!statetime.containsKey(artifactId)){
            stateCount = new StateCount();
            stateCount.artifactId = artifactId;
            for(StateModel state1:list){
                State state = new State(state1.name,state1.type,null,null,0);
                stateCount.stateruntime.put(state1.name,state);
            }
            String start = list.get(0).name;
            stateCount.stateruntime.get(start).entertime = log.getCreatedAt();

            statetime.put(artifactId,stateCount);
            System.out.println(++j);
        }
        else stateCount = statetime.get(artifactId);
        return stateCount;
    }

    public StatisticModel findstatenumber(String artifactId, StatisticModel statisticModel,
                                          List<StateModel> list, Log log){
        if(!statisticModel.statecount.containsKey(artifactId)
            && log.getType() == LogType.STATE_TRANSITION){ //artifact实例第一次出现，判断是否从start开始
            if(!log.getFromState().equals("start")){
                System.out.println(artifactId + " " + log.getFromState());
                System.out.println("流程故障,抛弃此Artifact实例");
                statisticModel.flag = 1;
            }
            else{
                String start = list.get(0).name;
                StateNumber s = statisticModel.statenumber.get(start);
                s.name = start;
                s.number += 1;
                statisticModel.statenumber.put(start,s);
                statisticModel.flag = 0;
            }
        }
        return statisticModel;
    }

    public List<StateModel> pre(){
        //将状态数组的顺序找到
        List<ArtifactModel> artifactModels = artifactModelRepository.findAll();
        ArtifactModel artifactModel = artifactModels.get(0);
        StateModel s = artifactModel.getStartState();
        String name = s.name;
        List<StateModel> list = new LinkedList<>();
        Queue<String> queue = new LinkedList<String>();
        queue.add(name);
        list.add(s);

        while(!queue.isEmpty()){
            String namesearch = queue.element();
            //System.out.println("当前队列头: " + namesearch);
            queue.remove();
            findnextState(artifactModel,namesearch,queue,list);
        }

        return list;
    }

    public void findnextState(ArtifactModel artifactModel, String name, Queue<String> queue, List<StateModel> list){
        for(StateModel stateModel:artifactModel.states)
        {
            if(stateModel.name.equals(name)){
                if(stateModel.type == StateModel.StateType.FINAL){
                    break;
                }
                for(String s:stateModel.nextStates){
                    for(StateModel sm:artifactModel.states){
                        if(sm.name.equals(s)){
                            list.add(sm);
                            queue.add(s);
                            break;
                        }
                    }

                }
                return ;
            }
        }
    }
}
