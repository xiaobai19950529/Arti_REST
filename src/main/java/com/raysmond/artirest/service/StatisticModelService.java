package com.raysmond.artirest.service;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.raysmond.artirest.domain.*;
import com.raysmond.artirest.domain.Process;
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
        statisticModel.stateNumberOfModels.put(processModelId,stateNumberOfModel);
        stateNumberOfModel.ended = 0;
        stateNumberOfModel.instance = 0;
        stateNumberOfModel.pending = 0;
        stateNumberOfModel.running = 0;
        stateNumberOfModel.processModelId = processModelId;
        Map<String,Integer> m = new LinkedHashMap<String, Integer>();
        stateNumberOfModel.statenumber = m;

        for(ArtifactModel artifactModel : processModel.artifacts){
            for(StateModel state : artifactModel.states){
                stateNumberOfModel.statenumber.put(state.name,0);
            }
        }
        Set<Process> processes = new LinkedHashSet<Process>();
        stateNumberOfModel.processes = processes;

        statisticModelRepository.delete(statisticModel.getId());
        statisticModelRepository.save(statisticModel);

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

        processModelRepository.delete(processModelId); //先从流程模型表里删除该流程模型
        //当流程模型被删除时，也应删除属于其的流程实例
        List<ProcessModel> processModels = processModelRepository.findAll();
        List<Process> processes = processRepository.findAll();

        for(Process process : processes){
            //如果流程模型表里找不到该流程对应的流程模型了，那么就在流程表里将该流程删除
            if(!processModels.contains(process.getProcessModel())){
                processRepository.delete(process.getId());
            }
        }

        //当流程模型被删除时，也应将统计模型中对应的流程模型项删除
        for(String Id : statisticModel.stateNumberOfModels.keySet()){
            boolean flag = false;  //当前流程模型不存在于流程模型表里
            for(ProcessModel processModel : processModels){
                if(processModel.getId().equals(Id)){ //如果找到了,就标记为1
                    flag = true;
                }
            }
            if(!flag){
                statisticModel.stateNumberOfModels.remove(Id);
            }
        }

        statisticModelRepository.delete(statisticModel.getId());
        statisticModelRepository.save(statisticModel);




    }

    //创建一个新的流程模型时，在统计模型里应有其对象

}
