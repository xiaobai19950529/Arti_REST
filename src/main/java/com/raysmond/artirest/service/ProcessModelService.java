package com.raysmond.artirest.service;

import com.raysmond.artirest.domain.ProcessModel;
import com.raysmond.artirest.repository.ProcessModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing ProcessModel.
 */
@Service
public class ProcessModelService {

    private final Logger log = LoggerFactory.getLogger(ProcessModelService.class);

    @Autowired
    private ProcessModelRepository processModelRepository;

    public static final String CACHE_NAME = "artirest.process_model";

    /**
     * Save a processModel.
     * @return the persisted entity
     */
    public ProcessModel save(ProcessModel processModel) {
        log.debug("Request to save ProcessModel : {}", processModel);
        ProcessModel result = processModelRepository.save(processModel);
        return result;
    }

    /**
     *  get all the processModels.
     *  @return the list of entities
     */
    public Page<ProcessModel> findAll(Pageable pageable) {
        log.debug("Request to get all ProcessModels");
        Page<ProcessModel> result = processModelRepository.findAll(pageable);
        return result;
    }

    /**
     *  get one processModel by id.
     *  @return the entity
     */
    @Cacheable(value = CACHE_NAME)
    public ProcessModel findOne(String id) {
        log.debug("----- Request to get ProcessModel : {}", id);
        ProcessModel processModel = processModelRepository.findOne(id);
        return processModel;
    }

    /**
     *  delete the  processModel by id.
     */
    public void delete(String id) {
        log.debug("Request to delete ProcessModel : {}", id);
        ProcessModel processModel = processModelRepository.findOne(id);
        processModelRepository.delete(processModel);
    }

    public void modifyNameBeforeCreate(ProcessModel processModel){
        List<ProcessModel> processModels = processModelRepository.findAll();
        ProcessModel end_model = null;  //按时间排序后同名的最后一个流程模型； 数据库本身是按照创建时间排序的
        if(processModel.getName() == null){
            processModel.setName("小白大神");
            System.out.println(processModel.getName());
        }
        for(ProcessModel model : processModels){
            if(model.getName().equals(processModel.getName())){
                end_model = model;
            }
        }
        if(end_model == null) processModel.setNum(1);
        else processModel.setNum(end_model.getNum() + 1);
        processModel.setDisplay_name(processModel.getName() + "-" + processModel.getNum());
    }
}
