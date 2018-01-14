package com.raysmond.artirest.service;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.raysmond.artirest.domain.*;
import com.raysmond.artirest.domain.Process;
import com.raysmond.artirest.repository.ArtifactRepository;
import com.raysmond.artirest.repository.ProcessModelRepository;
import com.raysmond.artirest.repository.ProcessRepository;
import com.raysmond.artirest.repository.StatisticModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticModelService {

    @Autowired
    private StatisticModelRepository statisticModelRepository;

    @Autowired
    private ProcessModelRepository processModelRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ProcessModelService processModelService;

    @Autowired
    private ArtifactModelService artifactModelService;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    MetricRegistry registry;

    @Autowired
    MetricAddService metricAddService;

    /**
     *  get all the statisticModels.
     *  @return the list of entities
     */
    public List<StatisticModel> findAll() {
        List<StatisticModel> statisticModels = statisticModelRepository.findAll();
        return statisticModels;
    }

    public StatisticModel findById(String id){
        StatisticModel statisticModel = statisticModelRepository.findOne(id);
        return statisticModel;
    }

    public void add_modelnumber(ProcessModel processModel){
        StatisticModel statisticModel = statisticModelRepository.findAll().get(0);
        statisticModel.modelnumber++;

        StateNumberOfModel stateNumberOfModel = new StateNumberOfModel();
        String processModelId = processModel.getId();

        stateNumberOfModel.ended = 0;
        stateNumberOfModel.instance = 0;
        stateNumberOfModel.pending = 0;
        stateNumberOfModel.running = 0;
        stateNumberOfModel.processModelId = processModelId;
        Map<String,Integer> m = new LinkedHashMap<>();
        stateNumberOfModel.statenumber = m;

        for(ArtifactModel artifact : processModel.artifacts){
            for(StateModel state : artifact.states){
                m.put(state.name,0);
            }
        }

        statisticModel.stateNumberOfModels.put(processModelId,stateNumberOfModel);
        Set<Process> processes = new LinkedHashSet<Process>();
        stateNumberOfModel.processes = processes;


        System.out.println("嘿嘿");
        printStatisticModel(statisticModel);
        statisticModelRepository.save(statisticModel);
        //statisticModelRepository.delete(statisticModel.getId());



        metricAddService.addmetric(stateNumberOfModel,processModelId);
    }

    public void delete_modelnumber(String processModelId){
        StatisticModel statisticModel = statisticModelRepository.findAll().get(0);
        statisticModel.modelnumber--;

        StateNumberOfModel stateNumberOfModel = statisticModel.stateNumberOfModels.get(processModelId);

        //应移除提交相应流程模型的metric
        for(String state : stateNumberOfModel.statenumber.keySet()){
            String name1 = processModelId + "." + state;
            registry.remove(name1);

        }
        String processModel_count = "processModel_count";
        String count = processModelId + ".count";
        String pending = processModelId + ".pending";
        String running = processModelId + ".running";
        String ended = processModelId + ".ended";
        registry.remove(count);
        registry.remove(pending);
        registry.remove(running);
        registry.remove(ended);

        //当流程模型被删除时，还应删除属于其的Artifact模型，将Artifact模型从对应的表中删掉
        //需要先删除artifactModel表里与该流程模型相关的项
        ProcessModel processModel = processModelService.findOne(processModelId);
        for (ArtifactModel artifact : processModel.artifacts){
            artifactModelService.delete(artifact.getId());
        }

        processModelRepository.delete(processModelId); //先从流程模型表里删除该流程模型
        //当流程模型被删除时，也应删除属于其的流程实例
        List<ProcessModel> processModels = processModelRepository.findAll();
        System.out.println(processModels.size());
        List<Process> processes = processRepository.findAll();

        for(Process process : processes){
            //如果流程模型表里找不到该流程对应的流程模型了，那么就在流程表里将该流程删除
            if(!processModels.contains(process.getProcessModel())){
                processRepository.delete(process.getId());
                for(Artifact artifact : process.getArtifacts()){
                    artifactRepository.delete(artifact);
                }
            }
        }



        //还应删除流程实例对应的artifact实例


        //当流程模型被删除时，也应将统计模型中对应的流程模型项删除
        System.out.println("八一： " + processModelId);
        statisticModel.stateNumberOfModels.remove(processModelId,stateNumberOfModel);
//        for(String Id : statisticModel.stateNumberOfModels.keySet()){
//            boolean flag = false;  //当前流程模型不存在于流程模型表里
//            for(ProcessModel processModel : processModels){
//                if(processModel.getId().equals(Id)){ //如果找到了,就标记为1
//                    flag = true;
//                }
//            }
//            if(!flag){
//                statisticModel.stateNumberOfModels.remove(Id);
//            }
//        }

        //statisticModelRepository.delete(statisticModel.getId());
        statisticModelRepository.save(statisticModel);




    }

    //创建一个新的流程模型时，在统计模型里应有其对象

    public void printStatisticModel(StatisticModel statisticModel){
        System.out.println("Id : " + statisticModel.getId());
        System.out.println("Name : " + statisticModel.name);
        System.out.println("模型数量: " + statisticModel.modelnumber);
        System.out.println("每个流程模型的状态数量");

        for(String processModelId : statisticModel.stateNumberOfModels.keySet()){
            StateNumberOfModel stateNumberOfModel = statisticModel.stateNumberOfModels.get(processModelId);
            System.out.println("流程模型id: " + processModelId);
            System.out.println("流程实例总数: " + stateNumberOfModel.instance);
            System.out.println("正在运行的实例数量:" + stateNumberOfModel.running);
            System.out.println("准备状态的实例数量:" + stateNumberOfModel.pending);
            System.out.println("已经结束的实例数量:" + stateNumberOfModel.ended);
            Map<String,Integer> statenumber = stateNumberOfModel.statenumber;
            if(statenumber == null){
                System.out.println();
                continue;
            }
            for (String state : statenumber.keySet()){
                System.out.println(state + ": " + statenumber.get(state));
            }
            System.out.println();

        }
    }


}
