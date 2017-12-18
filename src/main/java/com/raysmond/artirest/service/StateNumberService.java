package com.raysmond.artirest.service;

import com.raysmond.artirest.domain.Log;
import com.raysmond.artirest.domain.StateModel;
import com.raysmond.artirest.domain.StateNumber;
import com.raysmond.artirest.domain.StatisticModel;
import com.raysmond.artirest.domain.enumeration.LogType;
import com.raysmond.artirest.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dashen on 17-6-1.
 */
@Service
public class StateNumberService {

    @Autowired
    LogRepository logRepository;

    @Autowired
    FindService findService;

    public Map<String,StatisticModel> getprocessNumber() throws Exception{
        List<StateModel> list = findService.pre();

        List<Log> logs = logRepository.findAll();
        Map<String,StatisticModel> statisticModelcount = new LinkedHashMap<>();
        System.out.println(logs.size());
        for(int i = 0; i < logs.size(); i++)
        {
            Log log = logs.get(i);
            //这里把processName固定成"LOAN"
            String processName = "LOAN";
            //String processName = log.getProcessName();
            String artifactId = log.getArtifactId();
            if(log.getType() == LogType.STATE_TRANSITION){  // 状态变迁
                //System.out.println(log.getFromState());
                StatisticModel statisticModel1 = findService.findStatisticModel(artifactId,processName,statisticModelcount,list,log);
                if(statisticModel1.flag == 1){ //如果第一次出现的fromState不是start就直接结束，进行下一个
                    continue;
                }
                String fromState = log.getFromState();
                String toState = log.getToState();

                //statisticModel.statenumber.put(service,statisticModel.statenumber.get(service)-1);
                System.out.println("fromState: " + fromState + " toState: " + toState );
                StateNumber frontnumber = statisticModel1.statenumber.get(fromState);
                StateNumber nextnumber  = statisticModel1.statenumber.get(toState);
                frontnumber.name = fromState;
                frontnumber.number = frontnumber.number - 1;
                //if(frontnumber.number < 0) frontnumber.number = 0;
                nextnumber.name = toState;
                nextnumber.number = nextnumber.number + 1;
                statisticModel1.statenumber.put(fromState,frontnumber);
                statisticModel1.statenumber.put(toState,nextnumber);
            }
        }
//        System.out.println("跑到了这里");
//        for(StatisticModel statisticModel1: statisticModelcount.values()){
//            System.out.println("流程模型: " + statisticModel1.processName);
//            for(Map.Entry<String,StateNumber> statenumber : statisticModel1.statenumber.entrySet()){
//                System.out.println("状态" + statenumber.getKey() + "的流程实例数量为: " + statenumber.getValue().number);
//            }
//        }
        return statisticModelcount;
    }
}
