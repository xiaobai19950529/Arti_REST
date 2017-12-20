//package com.raysmond.artirest.web.rest;
//
//import com.raysmond.artirest.domain.State;
//import com.raysmond.artirest.domain.StateCount;
//import com.raysmond.artirest.domain.StatisticModel;
//import com.raysmond.artirest.service.CountStateTimeService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
///**
// * Created by xiaobai on 17-7-18.
// */
//@RestController
//@RequestMapping("/api")
//@CrossOrigin(origins = "*")     //跨源
//public class StatetimeResource {
//
//    @Autowired
//    private CountStateTimeService countStateTimeService;
//
//    @RequestMapping(value = "/statetime",
//            method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public Map<String,StatisticModel> searchstatetime(){
//        Map<String,StatisticModel> s = countStateTimeService.searchstatetime();
//        for(StatisticModel statisticModel1: s.values()){
//            System.out.println("流程模型: " + statisticModel1.processName);
//            for(StateCount state :statisticModel1.statecount.values()){
//                System.out.println("ArtifactId: " + state.artifactId);
//                for(Map.Entry<String,State> statetime : state.stateruntime.entrySet()){
//                    System.out.println("状态" + statetime.getKey() +  "的进入时间: " + statetime.getValue().entertime +
//                        "  离开时间: " + statetime.getValue().quittime + "\n"
//                    );
//                }
//                System.out.println();
//            }
//
//        }
//        return countStateTimeService.searchstatetime();
//    }
//}
