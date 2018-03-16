package com.raysmond.artirest.repository;

import com.raysmond.artirest.domain.Coordinator;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Coordinator entity.
 */
@SuppressWarnings("unused")
public interface CoordinatorRepository extends MongoRepository<Coordinator, String> {

}
