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
public class MetricAddService {

    @Autowired
    private StatisticModelRepository statisticModelRepository;

    @Autowired
    private MetricRegistry registry;

    public void addmetric(StateNumberOfModel stateNumberOfModel,String processModelId){

        for(String state : stateNumberOfModel.statenumber.keySet()){
            String name1 = processModelId + "." + state;
//                System.out.println("孙八一"); //只会输出一次
            registry.register(name1,new Gauge<Integer>(){
                @Override
                public Integer getValue() {
//                        System.out.println("name1:"  + name1 + "  processModelId" + processModelId); //每次都会输出
                    return statisticModelRepository.findAll().get(0).stateNumberOfModels.get(processModelId).statenumber.get(state);
                }
            });
        }
        String count = processModelId + ".count";
        registry.register(count, new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return statisticModelRepository.findAll().get(0).stateNumberOfModels.get(processModelId).instance;
            }
        });
        String pending = processModelId + ".pending";
        registry.register(pending, new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return statisticModelRepository.findAll().get(0).stateNumberOfModels.get(processModelId).pending;
            }
        });
        String running = processModelId + ".running";
        registry.register(running, new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return statisticModelRepository.findAll().get(0).stateNumberOfModels.get(processModelId).running;
            }
        });
        String ended = processModelId + ".ended";
        registry.register(ended, new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return statisticModelRepository.findAll().get(0).stateNumberOfModels.get(processModelId).instance;
            }
        });
    }
}
