package com.raysmond.artirest.service;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.raysmond.artirest.MetricImp.GaugeImp;
import com.raysmond.artirest.domain.StateNumberOfModel;
import com.raysmond.artirest.domain.StatisticModel;
import com.raysmond.artirest.repository.StatisticModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class MetricService {

    @Autowired
    private StatisticModelRepository statisticModelRepository;

    @Autowired
    private MetricRegistry registry;

    public void addMetric(StateNumberOfModel stateNumberOfModel,String processModelId){

        for(String state : stateNumberOfModel.statenumber.keySet()){
            String name1 = processModelId + ".states." + state;
            registry.register(name1,new Gauge<Integer>(){
                @Override
                public Integer getValue() {
//                        System.out.println("name1:"  + name1 + "  processModelId" + processModelId); //每次都会输出
                    return statisticModelRepository.findAll().get(0).stateNumberOfModels.get(processModelId).statenumber.get(state);
                }
            });
        }
        String statistics = "statistics";
        String count = processModelId + "." + statistics + ".count";
        registry.register(count, new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return statisticModelRepository.findAll().get(0).stateNumberOfModels.get(processModelId).instance;
            }
        });
        String pending = processModelId + "." + statistics + ".pending";
        registry.register(pending, new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return statisticModelRepository.findAll().get(0).stateNumberOfModels.get(processModelId).pending;
            }
        });
        String running = processModelId + "." + statistics + ".running";
        registry.register(running, new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return statisticModelRepository.findAll().get(0).stateNumberOfModels.get(processModelId).running;
            }
        });
        String ended = processModelId + "." + statistics + ".ended";
        registry.register(ended, new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return statisticModelRepository.findAll().get(0).stateNumberOfModels.get(processModelId).instance;
            }
        });
    }

    public void removeMetric(StateNumberOfModel stateNumberOfModel,String processModelId){
        for(String state : stateNumberOfModel.statenumber.keySet()){
            String name1 = processModelId + ".states." + state;
            registry.remove(name1);
        }

//        String processModel_count = "processModel_count";
        String statistics = "statistics";
        String count = processModelId + "." + statistics + ".count";
        String pending = processModelId + "." + statistics + ".pending";
        String running = processModelId + "." + statistics + ".running";
        String ended = processModelId + "." + statistics + ".ended";
        registry.remove(count);
        registry.remove(pending);
        registry.remove(running);
        registry.remove(ended);
    }
}
