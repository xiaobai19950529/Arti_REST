package com.raysmond.artirest.service;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.raysmond.artirest.MetricImp.ProcessModelNumberImp;
import com.raysmond.artirest.domain.Log;
import com.raysmond.artirest.domain.Process;
import com.raysmond.artirest.domain.ProcessModel;
import com.raysmond.artirest.domain.enumeration.LogType;
import com.raysmond.artirest.repository.LogRepository;

import com.raysmond.artirest.repository.ProcessModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Raysmond<i@raysmond.com>
 */
@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private ProcessModelRepository processModelRepository;

    @Autowired
    MetricRegistry registry;

    @Autowired
    Integer number;

    public Log updateArtifact(String processId, String artifactId, String service) {
        Log log = new Log();
        log.setArtifactId(artifactId);
        log.setProcessId(processId);
        log.setService(service);
        log.setType(LogType.UPDATE_ARTIFACT);
        log.setTitle("Artifact (" + artifactId + ") was updated.");
        logRepository.save(log);
        return log;
    }

    public Log callService(String processId, String service) {
        Log log = new Log();
        log.setTitle("Service (" + service + ") was invoked.");
        log.setProcessId(processId);
        log.setType(LogType.CALL_SERVICE);
        logRepository.save(log);
        return log;
    }

    public Log stateTransition(String processId, String artifactId, String fromState, String toState, String service) {
        Log log = new Log();
        log.setArtifactId(artifactId);
        log.setProcessId(processId);
        log.setService(service);
        log.setTitle("The state of artifact (" + artifactId + ") transited from \"" + fromState + "\" to \"" + toState + "\".");
        log.setType(LogType.STATE_TRANSITION);
        log.setFromState(fromState);
        log.setToState(toState);
        logRepository.save(log);

        //registry.register(MetricRegistry.name(InitialService.class,"ProcessModelNumberImp"), processModelNumberImp);

        number = 3;

//        registry.remove("com.raysmond.artirest.service.InitialService.number");
//        registry.register(MetricRegistry.name(InitialService.class,"number"),new Gauge<Integer>(){
//            @Override
//            public Integer getValue() {
//                System.out.println(number);
//                return number;
//            }
//        });

        return log;
    }
}
