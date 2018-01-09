package com.raysmond.artirest.web.rest;

import com.raysmond.artirest.domain.ProcessModel;
import com.raysmond.artirest.domain.StatisticModel;
import com.raysmond.artirest.repository.StatisticModelRepository;
import com.raysmond.artirest.service.StatisticModelService;
import com.raysmond.artirest.web.rest.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api")
public class StatisticModelResource {

    @Autowired
    private StatisticModelService statisticModelService;

    @RequestMapping(value = "/statisticModels",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StatisticModel> getAllStatisticModels()
        throws URISyntaxException {
        List<StatisticModel> statisticModels = statisticModelService.findAll();
        return statisticModels;
    }

    @RequestMapping(value = "/statisticModels/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public StatisticModel getStatisticModelById(@PathVariable String id){
        StatisticModel statisticModel = statisticModelService.findById(id);
        return statisticModel;
    }

}
