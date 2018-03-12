package com.raysmond.artirest.service;

import com.raysmond.artirest.domain.*;
import com.raysmond.artirest.domain.Process;
import com.raysmond.artirest.domain.enumeration.ServiceType;
import com.raysmond.artirest.repository.*;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;

import javax.inject.Inject;

import java.util.*;

/**
 * Service Implementation for managing Process.
 */
@Service
public class ProcessService {

    private final Logger log = LoggerFactory.getLogger(ProcessService.class);

    @Inject
    private ProcessRepository processRepository;

    @Autowired
    private ProcessModelRepository processModelRepository;

    @Autowired
    private ArtifactModelRepository artifactModelRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private ArtifactService artifactService;

    @Autowired
    private LogService logService;

    @Autowired
    private FindService findService;

    @Autowired
    private StatisticModelRepository statisticModelRepository;

    public static final String CACHE_NAME = "artirest.process";

    /**
     * Save a process.
     *
     * @return the persisted entity
     */
    public Process save(Process process) {
        log.debug("Request to save Process : {}", process);
        Process result = processRepository.save(process);
        return result;
    }

    /**
     * get all the processs.
     *
     * @return the list of entities
     */
    public Page<Process> findAll(Pageable pageable) {
        log.debug("Request to get all Processs");
        Page<Process> result = processRepository.findAll(pageable);
        return result;
    }

    public Page<Process> findInstances(String processModelId, Pageable pageable) {
        ProcessModel processModel = processModelRepository.findOne(processModelId);
        Page<Process> processes = processRepository.findByProcessModel(processModel, pageable);
        System.out.println(processes.getTotalElements());
        System.out.println(pageable.toString());
        return processes;
    }

    public Page<Process> findInstancesByCondition(String processModelId, Pageable pageable, List<AttributeOfQuery> attributeOfQueries) {
        ProcessModel processModel = processModelRepository.findOne(processModelId); //根据id找出对应的流程模型
//        Page<Process> processes = processRepository.findByProcessModel(processModel, pageable);
        Set<ArtifactModel> artifactModels = processModel.artifacts;

        List<Process> process_all = processRepository.findAll();
        List<Process> processOfModel = new LinkedList<>();
        for(Process process : process_all){
            if(process.getId().equals(processModelId)){
                processOfModel.add(process);
            }
        }
        //筛选本流程模型下的流程实例
        List<String> processIds = new LinkedList<>();
        List<Artifact> artifactList = new LinkedList<>();
        List<Process> processes = new LinkedList<>(); //存储最终找出来的流程实例

        List<AttributeOfQuery> queries = new LinkedList<>(); //存储有效的查询个数

        for(AttributeOfQuery attributeOfQuery : attributeOfQueries){ //遍历总的查询，找出所有查询条件
            if(attributeOfQuery.getValue() != null && !attributeOfQuery.getValue().equals("")){
                queries.add(attributeOfQuery);
            }
        }

        for (AttributeOfQuery query : queries){
            System.out.println(query.getName() + " " + query.getValue());
        }

        if(queries.size() == 0) {
            Page<Process> page = findInstances(processModelId, pageable);
//            processes = processRepository.findAll();
//
//            int start = pageable.getOffset();
//            int end = (start + pageable.getPageSize()) > processes.size() ? processes.size() : (start + pageable.getPageSize());
//            Page<Process> pages = new PageImpl<Process>(processes.subList(start, end), pageable, processes.size());

            return page;
        }


        boolean first = true;
        for(AttributeOfQuery query : queries){
            if(first) {
                artifactList = artifactRepository.findAll();
            }
            List<Artifact> artifacts = new LinkedList<>();
            for(Artifact artifact : artifactList){
                Set<Attribute> attributes = artifact.getAttributes();
                for(Attribute attribute : attributes){
                    if(attribute.getName().equals(query.getName())){ //如果属性名相等，看类型
                        if(query.getType().equals("Double")){
                            String operator = query.getOperator();
                            String v_query = query.getValue().toString();
                            double value_query = Double.parseDouble(v_query);
                            String v = attribute.getValue().toString();
                            double value;
                            if(v.equals("")) value = 0D;
                            else value = Double.parseDouble(v);
                            switch (operator){
                                case ">": if(value > value_query){
                                    artifacts.add(artifact);
                                } break;
                                case ">=": if(value >= value_query){
                                    artifacts.add(artifact);
                                } break;
                                case "=": if(value == value_query){
                                    artifacts.add(artifact);
                                } break;
                                case "<=": if(value <= value_query){
                                    artifacts.add(artifact);
                                } break;
                                case "<": if(value < value_query){
                                    artifacts.add(artifact);
                                } break;
                                default:
                                    System.out.println("出错了！");
                            }
                        }
                        else if(query.getType().equals("String")){
                            String value = (String)query.getValue();
                            if(attribute.getValue().equals(value)){
                                artifacts.add(artifact);
                            }
                        }
                    }
                }
            }
            artifactList = artifacts;
            first = false;
        }

        System.out.println("artifactsize = " + artifactList.size());
        for(Artifact artifact : artifactList){
            System.out.println(artifact.getProcessId());
            Process process = processRepository.findOne(artifact.getProcessId());
            if(process.getProcessModel().getId().equals(processModelId)){
                processes.add(process);
            }
        }

        int start = pageable.getOffset();
        int end = (start + pageable.getPageSize()) > processes.size() ? processes.size() : (start + pageable.getPageSize());
        System.out.println("start:" + start + " end:" + end + " pageSize:" + pageable.getPageSize());
        Page<Process> page = new PageImpl<Process>(processes.subList(start, end), pageable, processes.size());

        return page;
    }

    /**
     * get one process by id.
     *
     * @return the entity
     */
    @Cacheable(CACHE_NAME)
    public Process findOne(String id) {
        log.debug("Request to get Process : {}", id);
        Process process = processRepository.findOne(id);
//        process.getProcessModel().getId();
//        process.getProcessModel().businessRules.size();
//        process.getProcessModel().artifacts.size();
//        process.getProcessModel().services.size();
//        process.getArtifacts().size();
        return process;
    }

    @CachePut(value = CACHE_NAME, key = "#process.id")
    public Process cacheSave(Process process) {
        log.debug("Save process to cache: {}", process.getId());
        return process;
    }

    /**
     * delete the  process by id.
     */
    @CacheEvict(CACHE_NAME)
    public void delete(String id) {
        log.debug("Request to delete Process : {}", id);
        //删除流程同时需要删除该流程下的artifact from artifact表
        //不仅如此，还得需重新统计该流程所属流程模型下的数量
        StatisticModel statisticModel = statisticModelRepository.findAll().get(0);
        Process process = processRepository.findOne(id);
        StateNumberOfModel stateNumberOfModel = statisticModel.stateNumberOfModels.get(process.getProcessModel().getId()); //找出该流程模型的统计情况
        System.out.println(process.getIsRunning());
        System.out.println(process.getEnded());
        if(process.getIsRunning() == true){
            stateNumberOfModel.running--;
        }
        else if(process.getEnded() == true){
            stateNumberOfModel.ended--;
        }
        else{
            stateNumberOfModel.pending--;
        }
        stateNumberOfModel.instance--;
        for(Artifact artifact : process.getArtifacts()){
            artifactRepository.delete(artifact);
            stateNumberOfModel.statenumber.put(artifact.getCurrentState(),stateNumberOfModel.statenumber.get(artifact.getCurrentState()) - 1);
        }
        statisticModelRepository.save(statisticModel);
        processRepository.delete(id);
    }

    public List<ServiceModel> availableServices(Process process) {
        List<ServiceModel> services = new ArrayList<>();

        for (Artifact artifact : process.getArtifacts()) {
            services.addAll(artifactService.availableServices(artifact, process.getProcessModel().getId()));
        }

        if (process.getArtifacts().isEmpty()) {
            services.addAll(artifactService.availableBeginServices(process.getProcessModel()));
        }

        return services;
    }

    public Process createProcessInstance(ProcessModel model,String customerName) {
        Process process = new Process();
        process.setName(model.getName()+"-"+customerName);
        process.setProcessModel(model);
        process.setCustomerName(customerName);
        process.setIsRunning(false);
        process.setEnded(false);
        processRepository.save(process);

        //新加代码
        //如果有流程新建，先将对应流程模型的开始状态的流程实例数量+1，加完存回数据库
        StatisticModel statisticModel = statisticModelRepository.findAll().get(0);
        StateNumberOfModel stateNumberOfModel = statisticModel.stateNumberOfModels.get(model.getId());
        String startState = findService.findStartState(process);
        System.out.println("开始状态:" + startState);
        stateNumberOfModel.statenumber.put(startState, stateNumberOfModel.statenumber.get(startState) + 1);
        System.out.println("新建的流程id:" + process.getId());
        stateNumberOfModel.processes.add(process);
        stateNumberOfModel.instance++;
        stateNumberOfModel.pending++;

        statisticModelRepository.delete(model.getId());
        statisticModelRepository.save(statisticModel);

        return process;
    }


    public Artifact newArtifactFromModel(ArtifactModel model, Process process) {
        Artifact artifact = new Artifact();
        artifact.setArtifactModel(model);
        artifact.setName(model.getName());
        artifact.setProcessId(process.getId());

        StateModel startState = model.getStartState();
        artifact.setCurrentState(startState == null ? "" : startState.name);


        for (AttributeModel attr : model.attributes) {
            switch (attr.getType()) {
                case "Integer":
                    artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), 0));
                    break;
                case "Double":
                    artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), 0D));
                    break;
                case "Long":
                    artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), 0L));
                    break;
                case "String":
                    artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), null));
                    break;
                case "Date":
                    artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), null));
                    break;
                default:
                    if (attr.getType().startsWith("List<")) {
                        String itemType = attr.getType().substring(5, attr.getType().length() - 1);
                        switch (itemType) {
                            case "Integer":
                                artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), new ArrayList<Integer>()));
                                break;
                            case "Double":
                                artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), new ArrayList<Double>()));
                                break;
                            case "Long":
                                artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), new ArrayList<Long>()));
                                break;
                            case "Date":
                                artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), new ArrayList<Date>()));
                                break;
                            case "String":
                                artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), new ArrayList<String>()));
                                break;
                            default:

                        }
                    }
            }

        }

        return artifact;
    }


    public Artifact createArtifact(String processId, String artifactModelId) {
        ArtifactModel artifactModel = artifactModelRepository.findOne(artifactModelId);
        Process process = processRepository.findOne(processId);
        Artifact artifact = newArtifactFromModel(artifactModel,process);
        artifactRepository.save(artifact);

        Process instance = processRepository.findOne(processId);
        instance.getArtifacts().add(artifact);

        // comment here to enable cache
         processRepository.save(instance);

        return artifact;
    }

    public ServiceModel findService(String serviceName, ProcessModel processModel) {
        for (ServiceModel service : processModel.services) {
            if (service.name.equals(serviceName)) {
                return service;
            }
        }

        return null;
    }

    public ArtifactModel findArtifactModel(String artifactName, ProcessModel processModel) {
        for (ArtifactModel artifact : processModel.artifacts) {
            if (artifact.getName().equals(artifactName)) {
                return artifact;
            }
        }

        return null;
    }

    public Set<BusinessRuleModel> findServiceRelatedRules(String serviceName, ProcessModel processModel) {
        Set<BusinessRuleModel> rules = new HashSet<>();

        for (BusinessRuleModel rule : processModel.businessRules) {
            if (rule.action.service.equals(serviceName)) {
                rules.add(rule);
            }
        }

        return rules;
    }

    /**
     * 目前只允许: 一个流程实例里一个Artifact的名字只能有一个实例
     */
    public Artifact findArtifactByName(Process process, String artifactName) {
        Artifact artifact = null;

        for (Artifact artifact1 : process.getArtifacts()) {
            if (artifact1.getName().equals(artifactName)) {
                artifact = artifact1;
                break;
            }
        }

        return artifact;
    }

    public Artifact createProcessArtifact(Process process, String artifactName) throws Exception {
        ArtifactModel artifactModel = findArtifactModel(artifactName, process.getProcessModel());

        if (artifactModel == null) {
            throw new Exception("Illegal artifact model: " + artifactName);
        }

        Artifact artifact = newArtifactFromModel(artifactModel, process);

        artifact.setCurrentState(artifactModel.getStartState().name);
        artifact = artifactRepository.save(artifact);

        process.getArtifacts().add(artifact);

        // comment here to enable cache
        processRepository.save(process);

        return artifact;
    }

    public Process setArtifactAttributes(Process process, Artifact inputArtifact, ServiceModel serviceModel) throws Exception {
        Artifact artifact = findArtifactByName(process, inputArtifact.getName());

        if (artifact == null) {
            artifact = createProcessArtifact(process, inputArtifact.getName());
        }

        for (Attribute attribute : inputArtifact.getAttributes()) {
            if (serviceModel.inputParams.contains(attribute.getName())) {
                artifact.setAttribute(attribute.getName(), attribute);
            }
        }

        process.getArtifacts().add(artifact);

        return process;
    }

    public AttributeModel findAttributeModel(ProcessModel processModel, String artifactName, String attributeName) {
        ArtifactModel artifactModel = findArtifactModel(artifactName, processModel);
        AttributeModel attributeModel = null;

        if (artifactModel != null) {
            for (AttributeModel attribute : artifactModel.attributes) {
                if (attribute.getName().equals(attributeName)) {
                    attributeModel = attribute;
                    break;
                }
            }
        }

        return attributeModel;
    }

    private boolean verifyAtomConditions(Process process, ServiceModel service, Set<BusinessRuleModel.Atom> atoms) {
        boolean satisfied = true;

        for (BusinessRuleModel.Atom atom : atoms) {
            Artifact a = findArtifactByName(process, atom.artifact);

            if (atom.type.equals(BusinessRuleModel.AtomType.INSTATE)) {
                satisfied = satisfied
                    && a != null
                    && a.getCurrentState() != null
                    && a.getCurrentState().equals(atom.state);
            }

            if (atom.type.equals(BusinessRuleModel.AtomType.ATTRIBUTE_DEFINED)) {
                satisfied = satisfied
                    && a != null
                    && a.getAttribute(atom.attribute) != null
                    && a.getAttribute(atom.attribute).getValue() != null;
            }

            if (atom.type.equals(BusinessRuleModel.AtomType.SCALAR_COMPARISON)) {
                satisfied = satisfied
                    && a != null
                    && a.getAttribute(atom.attribute) != null
                    && a.getAttribute(atom.attribute).getValue() != null;

                if (!satisfied) {
                    break;
                }

                Attribute attribute = a.getAttribute(atom.attribute);
                AttributeModel attributeModel = findAttributeModel(process.getProcessModel(), atom.artifact, atom.attribute);

                switch (atom.operator) {
                    case EQUAL:
                        satisfied = satisfied && attribute.getValue().equals(atom.value);
                        break;
                    case LESS:
                        switch (attributeModel.getType()) {
                            case "Integer":
                                satisfied = satisfied && ((Integer) attribute.getValue() < (Integer) atom.value);
                                break;
                            case "Long":
                                satisfied = satisfied && ((Long) attribute.getValue() < (Long) atom.value);
                                break;
                            case "Double":
                                satisfied = satisfied && ((Double) attribute.getValue() < (Double) atom.value);
                                break;
                            default:
                                ;
                        }
                        break;
                    case LARGER:
                        switch (attributeModel.getType()) {
                            case "Integer":
                                satisfied = satisfied && ((Integer) attribute.getValue() > (Integer) atom.value);
                                break;
                            case "Long":
                                satisfied = satisfied && ((Long) attribute.getValue() > (Long) atom.value);
                                break;
                            case "Double":
                                satisfied = satisfied && ((Double) attribute.getValue() > (Double) atom.value);
                                break;
                            default:
                                ;
                        }
                        break;
                    default:
                        ;
                }
            }

            if (!satisfied) {
                break;
            }
        }


        return satisfied;
    }


    public Process invokeService(String serviceName, Process process, Artifact artifact) throws Exception {
        ServiceModel serviceModel = findService(serviceName, process.getProcessModel());

        process = setArtifactAttributes(process, artifact, serviceModel);

        if (serviceModel.type == ServiceType.HUMAN_TASK) {
            return invokeHumanService(process, artifact, serviceModel);
        } else if (serviceModel.type == ServiceType.INVOKE_SERVICE) {
            // TODO
        }

        return process;
    }

    public void afterInvokingService(Process process){
        if (isProcessEnded(process)){
            process.getArtifacts().forEach(artifact -> {
                artifactRepository.save(artifact);
            });
            processRepository.save(process);
        }
    }


    private Process invokeHumanService(Process process, Artifact artifact, ServiceModel service) throws Exception {
        Set<BusinessRuleModel> rules = findServiceRelatedRules(service.name, process.getProcessModel());

        BusinessRuleModel firstRuleSatisfied = null;
        for (BusinessRuleModel rule : rules) {
            if (verifyAtomConditions(process, service, rule.preConditions)) {
                firstRuleSatisfied = rule;
                break;
            }
        }

        if (!rules.isEmpty() && firstRuleSatisfied == null) {
            throw new Exception("PreConditions of business rules for service " + service.name + " are not satisfied.");

        }
        //processRepository.save(process);
        if (firstRuleSatisfied != null) {
            log.debug("first business rule: {}", firstRuleSatisfied.name);
        }

        // comment here to enable cache
        processRepository.save(process);

        artifact = findArtifactByName(process, artifact.getName());

        logService.callService(process.getId(), service.name);
        logService.updateArtifact(process.getId(), artifact.getId(), service.name);


        if (firstRuleSatisfied != null) {
            doTransitions(process, firstRuleSatisfied, service);

        }

        // 后置条件没有计算, 问题:
        // 如果后置条件不满足怎么办?

        // return processRepository.findOne(process.getId());

        return process;
    }


    /**
     * Perform state transitions after invoking a service
     */
    private void doTransitions(Process process, BusinessRuleModel rule, ServiceModel service) {
        if (rule != null && !rule.action.transitions.isEmpty()) {
            for (BusinessRuleModel.Transition transition : rule.action.transitions) {
                for (Artifact artifact1 : process.getArtifacts()) {
                    if (artifact1.getName().equals(transition.artifact) && artifact1.getCurrentState().equals(transition.fromState)) {

                        // Do transition
                        artifact1.setCurrentState(transition.toState);

                        // comment here to enable cache
                        artifactRepository.save(artifact1);

                        ArtifactModel artifactModel = new ArtifactModel();
                        for(ArtifactModel artifact : process.getProcessModel().artifacts){
                            artifactModel = artifact; //取最后一个
                        }
                        boolean flag = false;
                        for(StateModel endState : artifactModel.endStates){
                            System.out.println(endState.name + "==" + transition.toState);
                            if(endState.name.equals(transition.toState)){
                                process.setIsRunning(false);
                                process.setEnded(true);
                                flag = true;
                                break;
                            }
                        }
                        if(!flag) process.setIsRunning(true);
                        processRepository.save(process);
                        //新增代码
                        //将数据库中 fromState实例数-1 , toState实例数+1
                        updateMetric(process,transition);

                        logService.stateTransition(process.getId(), artifact1.getId(), transition.fromState, transition.toState, service.name);
                    }
                }
            }
        }
    }

    /**
     * If the process is ended
     */
    public boolean isProcessEnded(Process process) {
        if (process.getArtifacts().isEmpty()) {
            return false;
        }

        for (Artifact artifact : process.getArtifacts()) {
            if (artifact.getCurrentState() != null) {
                StateModel stateModel = artifactService.findState(artifact.getCurrentState(), artifact.getName(), process.getProcessModel());
                if (stateModel == null || stateModel.type != StateModel.StateType.FINAL) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * If state transition happened, Update metric
     */
    public void updateMetric(Process process, BusinessRuleModel.Transition transition){
        StatisticModel statisticModel = statisticModelRepository.findAll().get(0);
        StateNumberOfModel s = statisticModel.stateNumberOfModels.get(process.getProcessModel().getId());
        s.statenumber.put(transition.fromState, s.statenumber.get(transition.fromState) - 1);
        s.statenumber.put(transition.toState, s.statenumber.get(transition.toState) + 1);
        //对实例数的改动
        String startState = findService.findStartState(process);
        List<String> endStates = findService.findEndState(process);
        if(transition.fromState.equals(startState) && !endStates.contains(transition.toState)){
            s.pending--;
            s.running++;
        }
        else if(!transition.fromState.equals(startState) && endStates.contains(transition.toState)){
            s.running--;
            s.ended++;
        }
        else if(transition.fromState.equals(startState) && endStates.contains(transition.toState)){
            s.pending--;
            s.ended++;
        }

        statisticModelRepository.delete(statisticModel.getId());
        statisticModelRepository.save(statisticModel);
    }
}
