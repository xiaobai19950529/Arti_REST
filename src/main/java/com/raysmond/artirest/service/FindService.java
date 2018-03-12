package com.raysmond.artirest.service;

import com.codahale.metrics.Gauge;
import com.raysmond.artirest.domain.*;
import com.raysmond.artirest.domain.Process;
import com.raysmond.artirest.domain.enumeration.LogType;
import com.raysmond.artirest.repository.ArtifactModelRepository;
import com.raysmond.artirest.repository.ProcessModelRepository;
import com.raysmond.artirest.repository.ProcessRepository;
import com.raysmond.artirest.repository.StatisticModelRepository;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    StatisticModelRepository statisticModelRepository;

    public void sendmetrics(){
        System.out.println(processModelRepository.findAll().size());
        List<ProcessModel> processModels = processModelRepository.findAll();
        List<Process> processes = processRepository.findAll();


        System.out.println("流程模型个数: " + processModels.size());
        System.out.println("流程实例个数: " + processes.size());

    }

    int j = 0;

    public StatisticModel setStateNumber(){

        StatisticModel statisticModel;
        if(statisticModelRepository.count() > 0){
            statisticModel = statisticModelRepository.findAll().get(0);
            //statisticModelRepository.deleteAll();
        }
        else
            statisticModel = new StatisticModel();


        //单artifact情况　1个流程模型里只有一个ArtifactModel
        List<ProcessModel> processModels = processModelRepository.findAll();
        statisticModel.modelnumber = processModelRepository.findAll().size();
        System.out.println(statisticModel.modelnumber);
        //初始化
        for(ProcessModel processModel : processModels) {
            StateNumberOfModel stateNumberOfModel = new StateNumberOfModel(); //新建一个对象
            statisticModel.stateNumberOfModels.put(processModel.getId(),stateNumberOfModel); //put进去
            stateNumberOfModel.processModelId = processModel.getId(); //设置值
            Map<String,Integer> m = new LinkedHashMap<String,Integer>();
            stateNumberOfModel.processes = new LinkedHashSet<Process>();
            stateNumberOfModel.statenumber = m;
            for(ArtifactModel artifactModel : processModel.artifacts){
                for(StateModel stateModel : artifactModel.states){
                    stateNumberOfModel.statenumber.put(stateModel.name,0);
                }
            }
            stateNumberOfModel.instance = 0;
            stateNumberOfModel.pending = 0;
            stateNumberOfModel.running = 0;
            stateNumberOfModel.ended = 0;

            //statisticModel.stateNumberOfModels.put(processModel.getId(),stateNumberOfModel); //put进去
        }

        List<Process> processes = processRepository.findAll();
        System.out.println(processes.size());
        for(Process process : processes){
            String id = process.getProcessModel().getId();
            if(processModels.contains(process.getProcessModel())){
                statisticModel.stateNumberOfModels.get(id).processes.add(process);
            }
            else continue;
            Set<Artifact> artifacts = process.getArtifacts();
            if(artifacts.size() == 0){
                StateNumberOfModel s = statisticModel.stateNumberOfModels.get(process.getProcessModel().getId());
                //寻找START状态
                String startState = findStartState(process);
                s.statenumber.put(startState, s.statenumber.get(startState) + 1);
                s.pending++;
                s.instance++;
            }
            else {
                for (Artifact artifact : process.getArtifacts()) {
                    String state = artifact.getCurrentState();
                    StateNumberOfModel s = statisticModel.stateNumberOfModels.get(process.getProcessModel().getId());
                    s.instance++;

                    for (StateModel stateModel : artifact.getArtifactModel().states) {
                        if (stateModel.name.equals(state)) {
                            s.statenumber.put(stateModel.name, s.statenumber.get(stateModel.name) + 1);
                            if(stateModel.type == StateModel.StateType.NORMAL){
                                s.running++;
                            }
                            else if(stateModel.type == StateModel.StateType.FINAL){
                                s.ended++;
                            }
                            break;
                        }
                    }
                   // statisticModel.stateNumberOfModels.put(process.getProcessModel().getId(), s);
                }
            }
        }

        System.out.println("当前统计模型数量：" + statisticModelRepository.count());
//        StatisticModel s = new StatisticModel();
        statisticModelRepository.save(statisticModel);
        return statisticModel;
    }

    public String findStartState(Process process){
        //必须从流程模型拿开始状态，如果直接从流程的artifact拿，会出现空的情况，因为流程处于start状态时并无artifact实例
        Set<ArtifactModel> artifacts = process.getProcessModel().artifacts;

        for(ArtifactModel artifactModel : artifacts){
            return artifactModel.getStartState().name;
        }
        return null;
    }

    public List<String> findEndState(Process process){
        Set<ArtifactModel> artifacts = process.getProcessModel().artifacts;
        List<String> endStates = new LinkedList<String>();

        for(ArtifactModel artifactModel : artifacts){
            for(StateModel state : artifactModel.states){
                if(state.type == StateModel.StateType.FINAL ){
                    endStates.add(state.name);
                }
            }
        }
        return endStates;
    }

//    public StatisticModel findStatisticModel(String artifactId, String processName,
//                                             Map<String,StatisticModel> statisticModelcount, List<StateModel> list, Log log){
//        StatisticModel statisticModel;
//        if(!statisticModelcount.containsKey(processName)){
//            statisticModel = new StatisticModel();
//            statisticModel.processName = processName;
//
//            StateCount stateCount = findstatetime(artifactId,statisticModel.statecount,list,log);
//            statisticModelcount.put(processName,statisticModel);
//            statisticModel.statecount.put(artifactId,stateCount);
//            for(StateModel state1:list){
//                StateNumberOfModel s = new StateNumberOfModel();
//                s.name = state1.name;
//                s.number = 0;
//                statisticModel.statenumber.put(state1.name,s);
//            }
//            String start = list.get(0).name;
//            StateNumberOfModel ss = statisticModel.statenumber.get(start);
//            ss.number = 1;
//            statisticModel.statenumber.put(start,ss);
//        }
//        else {
//            statisticModel = statisticModelcount.get(processName);
//            statisticModel = findstatenumber(artifactId,statisticModel,list,log);
//            if(statisticModel.flag == 1) return statisticModel;
//
//            StateCount stateCount = findstatetime(artifactId,statisticModel.statecount,list,log);
//            statisticModel.statecount.put(artifactId,stateCount);
//        }
//        return statisticModel;
//    }

//    public StateCount findstatetime(String artifactId, Map<String,StateCount> statetime, List<StateModel> list, Log log){
//        StateCount stateCount;
//        if(!statetime.containsKey(artifactId)){
//            stateCount = new StateCount();
//            stateCount.artifactId = artifactId;
//            for(StateModel state1:list){
//                State state = new State(state1.name,state1.type,null,null,0);
//                stateCount.stateruntime.put(state1.name,state);
//            }
//            String start = list.get(0).name;
//            stateCount.stateruntime.get(start).entertime = log.getCreatedAt();
//
//            statetime.put(artifactId,stateCount);
//            System.out.println(++j);
//        }
//        else stateCount = statetime.get(artifactId);
//        return stateCount;
//    }

//    public StatisticModel findstatenumber(String artifactId, StatisticModel statisticModel,
//                                          List<StateModel> list, Log log){
//        if(!statisticModel.statecount.containsKey(artifactId)
//            && log.getType() == LogType.STATE_TRANSITION){ //artifact实例第一次出现，判断是否从start开始
//            if(!log.getFromState().equals("start")){
//                System.out.println(artifactId + " " + log.getFromState());
//                System.out.println("流程故障,抛弃此Artifact实例");
//                statisticModel.flag = 1;
//            }
//            else{
//                String start = list.get(0).name;
//                StateNumberOfModel s = statisticModel.statenumber.get(start);
//                s.name = start;
//                s.number += 1;
//                statisticModel.statenumber.put(start,s);
//                statisticModel.flag = 0;
//            }
//        }
//        return statisticModel;
//    }

//    public List<StateModel> pre(){
//        //将状态数组的顺序找到
//        List<ArtifactModel> artifactModels = artifactModelRepository.findAll();
//        ArtifactModel artifactModel = artifactModels.get(0);
//        StateModel s = artifactModel.getStartState();
//        String name = s.name;
//        List<StateModel> list = new LinkedList<>();
//        Queue<String> queue = new LinkedList<String>();
//        queue.add(name);
//        list.add(s);
//
//        while(!queue.isEmpty()){
//            String namesearch = queue.element();
//            //System.out.println("当前队列头: " + namesearch);
//            queue.remove();
//            findnextState(artifactModel,namesearch,queue,list);
//        }
//
//        return list;
//    }
//
//    public void findnextState(ArtifactModel artifactModel, String name, Queue<String> queue, List<StateModel> list){
//        for(StateModel stateModel:artifactModel.states)
//        {
//            if(stateModel.name.equals(name)){
//                if(stateModel.type == StateModel.StateType.FINAL){
//                    break;
//                }
//                for(String s:stateModel.nextStates){
//                    for(StateModel sm:artifactModel.states){
//                        if(sm.name.equals(s)){
//                            list.add(sm);
//                            queue.add(s);
//                            break;
//                        }
//                    }
//
//                }
//                return ;
//            }
//        }
//    }
}
