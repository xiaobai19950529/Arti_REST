package com.raysmond.artirest.service;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.raysmond.artirest.config.MetricsConfiguration;
import com.raysmond.artirest.domain.*;
import com.raysmond.artirest.domain.Process;
import com.raysmond.artirest.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static com.codahale.metrics.graphite.GraphiteReporter.*;

@Service
public class InitialService {

    @Autowired
    ProcessRepository processRepository;

    @Autowired
    ProcessModelRepository processModelRepository;

    @Autowired
    ArtifactModelRepository artifactModelRepository;

    @Autowired
    StatisticModelRepository statisticModelRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    FindService findService;

    @Autowired
    MetricsConfiguration metricsConfiguration;

    @Autowired
    MetricService metricService;

    @PostConstruct
    public void initialmonitor(){
        MetricRegistry registry = metricsConfiguration.getMetricRegistry();
        Graphite graphite = new Graphite(new InetSocketAddress("10.141.209.192",2003));

        //发送metric: 流程模型数量
        registry.register(name(InitialService.class,"ProcessModelNumberImp")
                        , new Gauge<Integer>(){
                @Override
                public Integer getValue(){
                    return processModelRepository.findAll().size();  //从数据库取，数据库变了自然会跟着变
                }
            });
        //System.out.println(number); //2

        List<ProcessModel> processModels = processModelRepository.findAll();
        List<Process> processes = processRepository.findAll();


        //计算每个流程模型有多少流程实例，根据ID
//        for (ProcessModel processModel : processModels) {
//            processNumberofModel.put(processModel.getId(),0);
//        }
//
//        for(Process process : processes) {
//            if(process.getProcessModel() != null){
//                String name = process.getProcessModel().getId();
//                processNumberofModel.put(name,processNumberofModel.get(name)+1);
//            }
//        }
//
//        for(ProcessModel processModel : processModels){
//            String name = processModel.getId();
//            registry.register(name,new Gauge<Integer>(){
//                    @Override
//                    public Integer getValue(){
//                        System.out.println(processNumberofModel.get(name));
//                        return processNumberofModel.get(name);
//                    }
//            });
//        }

        dealwithConcurrent(); //处理并发问题，如果统计模型中有多余项，需先清除 （流程模型表里已经删除但统计模型中的StateNumberOfModels里还有此项，需删除）

		List<ArtifactModel> artifactModels = artifactModelRepository.findAll();

		//计算每个流程模型中各个状态的流程实例数
        StatisticModel statisticModel1 = findService.setStateNumber();
        StatisticModel statisticModel = statisticModelRepository.findAll().get(0);
        System.out.println(statisticModel1.name);

        for(ProcessModel processModel : processModelRepository.findAll()) {
            if (processModel == null) continue;
            String processModelId = processModel.getId();
            StateNumberOfModel stateNumberOfModel = statisticModel.stateNumberOfModels.get(processModelId);
            metricService.addMetric(stateNumberOfModel,processModelId);
        }
        String processModel_count = "processModel_count";
        registry.register(processModel_count, new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                StatisticModel s = statisticModelRepository.findAll().get(0);
                System.out.println(s.modelnumber); //输出流程模型数量
                return s.modelnumber;
            }
        });
        GraphiteReporter reporter = forRegistry(registry)
            .prefixedWith("ArtiREST.ProcessModel")
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .filter(MetricFilter.ALL)
            .build(graphite);
        // 设置每隔5秒钟，向Graphite中发送一次指标值
        reporter.start(5, TimeUnit.SECONDS);

    }

    public void dealwithConcurrent(){
        StatisticModel statisticModel = statisticModelRepository.findAll().get(0);
        List<ProcessModel> processModels = processModelRepository.findAll();

        for(String processModelId : statisticModel.stateNumberOfModels.keySet()){
            boolean flag = false;
            for(ProcessModel processModel : processModels){
                if(processModel.getId().equals(processModelId)){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                statisticModel.stateNumberOfModels.remove(processModelId);
            }
        }
        statisticModelRepository.save(statisticModel);


        for(int i = 0; i < processModels.size(); i++){
            int id = 0;
            boolean flag = true; //名字唯一
            for(int j = 0; j < i; j++){
                if(processModels.get(i).getName().equals(processModels.get(j).getName())){
                    flag = false; //有同名
                    id = j; //记住最后一个同名的processModel的下标
                }
            }
            if(flag) processModels.get(i).setNum(1);
            else processModels.get(i).setNum(processModels.get(id).getNum() + 1); //下标从0开始，故表示第几个需+1
            processModels.get(i).setDisplay_name(processModels.get(i).getName() + "-" + processModels.get(i).getNum());
        }
        processModelRepository.save(processModels);

        //处理Process表的冗余
        List<Process> processes = processRepository.findAll();
        for(Process process : processes){
            if(process.getProcessModel() == null){
                processRepository.delete(process);
            }
//            if(process.getEnded() == null) {
//                processRepository.delete(process);
//            }
        }

        //处理ArtifactModel表的冗余
//        List<ArtifactModel> artifactModels = artifactModelRepository.findAll();
//        for(ArtifactModel artifactModel : artifactModels){
//            boolean flag = false;  //这个ArtifactModel在ProcessModel里没找到，应该被删除
//            for(ProcessModel processModel : processModels){
//                for(ArtifactModel artifact : processModel.artifacts){
//                    if(artifact.getId().equals(artifactModel.getId())){
//                        flag = true;
//                        break;
//                    }
//                }
//                if(flag) break;
//            }
//            if(!flag){
//                artifactModelRepository.delete(artifactModel);
//            }
//        }

        //处理Artifact表的冗余
        List<Artifact> artifacts = artifactRepository.findAll();
        List<Process> processes_1 = processRepository.findAll();

        for(Artifact artifact : artifacts){
            boolean flag = false;
            for(Process process : processes_1){
                for(Artifact artifact1 : process.getArtifacts()){
                    if(artifact.getId().equals(artifact1.getId())){
                        flag = true;
                        break;
                    }
                }
                if(flag) break;
            }
            if(!flag){
                artifactRepository.delete(artifact);
            }
        }
    }
}
