//package com.raysmond.artirest.service;
//
//import com.raysmond.artirest.domain.*;
//import com.raysmond.artirest.domain.enumeration.LogType;
//import com.raysmond.artirest.repository.LogRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.time.ZonedDateTime;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import static com.raysmond.artirest.domain.StateModel.StateType.FINAL;
//
///**
// * Created by dashen on 17-6-1.
// */
//@Service
//public class CountStateTimeService {
//
//    @Autowired
//    LogRepository logRepository;
//
//    @Autowired
//    FindService findService;
//
//    public Map<String,StatisticModel> searchstatetime(){
//        //将ZoneDateTime格式转为String类型
//        //ZonedDateTime date = ZonedDateTime.now();
//        //System.out.println(DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm:ss").format(date));
//
//        List<StateModel> list = findService.pre(); //生命周期顺序的排放列表
//
//        List<Log> logs = logRepository.findAll(); //应增量读取数据库
//        Map<String,StatisticModel> statisticModelcount = new LinkedHashMap<>(); //每个流程模型Id对应一个统计模型
//
//        for(int i = 0; i < logs.size(); i++)
//        {
//            Log log = logs.get(i);
//            //这里把processName固定成"LOAN"
//            String processName = "LOAN";
//            //String processName = processName;
//            String artifactId = log.getArtifactId();
//            if(log.getType().equals("UPDATE_ARTIFACT")){ //首次出现artifactId,记录开始状态的进入时间
//                StatisticModel statistic1 = findService.findStatisticModel(artifactId,
//                        processName,statisticModelcount,list,log);
//            }
//            else if(log.getType() == LogType.STATE_TRANSITION){
//                StatisticModel statisticModel1 = findService.findStatisticModel(artifactId,processName,statisticModelcount,list,log);
//                if(statisticModel1.flag == 1){ //如果第一次出现的fromState不是start就直接结束，进行下一个
//                    continue;
//                }
//                else{
//                    StatisticModel statistic2 = findService.findStatisticModel(artifactId,
//                            processName,statisticModelcount,list,log);
//                    String fromState = log.getFromState();
//                    String toState = log.getToState();
//
//                    //保存toState的状态和时间,fromState的状态和时间,计算时间差,作为停留在fromState的时间
//                    //遍历找到对应的流程实例的Id
//                    State front = statistic2.statecount.get(artifactId).stateruntime.get(fromState);
//                    front.name = fromState;
//                    front.quittime = log.getCreatedAt();
//
//                    State next = statistic2.statecount.get(artifactId).stateruntime.get(toState);
//                    next.name = toState;
//                    next.entertime = log.getCreatedAt();
//                    next.quittime = null;
//
//                    //System.out.println(front == next);
//                    statistic2.statecount.get(artifactId).stateruntime.put(fromState,front);
//                    statistic2.statecount.get(artifactId).stateruntime.put(toState,next);
//
//                    if(next.type == FINAL){
//                        System.out.println(next.name + " " + next.type);
//                        next.quittime = log.getCreatedAt();
//                        statistic2.statecount.get(artifactId).stateruntime.put(toState,next);
//                    }
//
//                    //String time = front.quittime - front.entertime;
//                    //statisticModel.stateruntime.put();
//                }
//            }
//        }
//
//        for(StatisticModel statisticModel1: statisticModelcount.values()){
//            //System.out.println("流程模型: " + statisticModel1.processName);
//            for(StateCount state :statisticModel1.statecount.values()){
//               // System.out.println("ArtifactId: " + state.artifactId);
//                for(Map.Entry<String,State> statetime : state.stateruntime.entrySet()){
//                    State s = statetime.getValue();
//                    long d;
//                    if(s.quittime == null) {
//                        d = 0;
//                    }
//                    else{
//                        ZonedDateTime entertime = s.entertime;
//                        ZonedDateTime quittime = s.quittime;
//                        d = getTime(entertime,quittime);
//                    }
//                    s.duration = d;
//                    state.stateruntime.put(statetime.getKey(),s);
//                    statisticModel1.statecount.put(state.artifactId,state);
////                    System.out.println("状态" + statetime.getKey() +  "的进入时间: " + statetime.getValue().entertime +
////                            "  离开时间: " + statetime.getValue().quittime + "\n" +
////                            "停留时间为: " + d);
//                }
//                System.out.println();
//            }
//
//        }
//        return statisticModelcount;
//    }
//
//    public long getTime(ZonedDateTime starttime,ZonedDateTime endtime){
//        Duration duration = Duration.between(starttime,endtime);
//        long seconds = duration.toMillis();
//        return seconds;
//    }
//
//}
