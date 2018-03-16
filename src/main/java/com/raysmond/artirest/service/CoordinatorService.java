package com.raysmond.artirest.service;

import com.raysmond.artirest.domain.Coordinator;
import com.raysmond.artirest.repository.CoordinatorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing Coordinator.
 */
@Service
public class CoordinatorService {

    private final Logger log = LoggerFactory.getLogger(CoordinatorService.class);

    @Autowired
    private CoordinatorRepository coordinatorRepository;

    public static final String CACHE_NAME = "artirest.coordinator";

    /**
     * Save a coordinator.
     * @return the persisted entity
     */
    public Coordinator save(Coordinator coordinator) {
        log.debug("Request to save Coordinator : {}", coordinator);
        Coordinator result = coordinatorRepository.save(coordinator);
        return result;
    }

    /**
     *  get all the coordinators.
     *  @return the list of entities
     */
    public Page<Coordinator> findAll(Pageable pageable) {
        log.debug("Request to get all Coordinator");
        Page<Coordinator> result = coordinatorRepository.findAll(pageable);
        return result;
    }

    /**
     *  get one coordinator by id.
     *  @return the entity
     */
    @Cacheable(value = CACHE_NAME)
    public Coordinator findOne(String id) {
        log.debug("----- Request to get Coordinator : {}", id);
        Coordinator coordinator = coordinatorRepository.findOne(id);
        return coordinator;
    }

    /**
     *  delete the coordinator by id.
     */
    public void delete(String id) {
        log.debug("Request to delete Coordinator : {}", id);
        Coordinator coordinator = coordinatorRepository.findOne(id);
        coordinatorRepository.delete(coordinator);
    }
}
