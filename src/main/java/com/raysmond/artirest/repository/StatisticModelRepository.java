package com.raysmond.artirest.repository;

import com.raysmond.artirest.domain.StatisticModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatisticModelRepository extends MongoRepository<StatisticModel,String>{
}
