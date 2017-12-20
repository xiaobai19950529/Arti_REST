//package com.raysmond.artirest.web.rest;
//
//import com.raysmond.artirest.domain.StatisticModel;
//import com.raysmond.artirest.service.FindService;
//import com.raysmond.artirest.service.StateNumberService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
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
//public class ProcessInstanceNumberResource {
//
//    @Autowired
//    StateNumberService stateNumberService;
//
//    @Autowired
//    FindService findService;
//
//    @RequestMapping(value = "/processnumber",
//            method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public Map<String,StatisticModel> getprocessNumber() throws Exception{
//        System.out.println("有没有看到我");
//        System.out.println("stateNumberService:"+stateNumberService.getprocessNumber().size());
//        return stateNumberService.getprocessNumber();
//    }
//
//    @RequestMapping(value = "/processModelnumber",
//            method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public void getprocessModelNumber() throws Exception{
//        return findService.sendmetrics();
//    }
//
//}
