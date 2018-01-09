package com.raysmond.artirest.web.rest;

import com.raysmond.artirest.domain.TotalInstance;
import com.raysmond.artirest.service.TotalInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
@RequestMapping("/api")
public class TotalInstanceResource {

    @Autowired
    private TotalInstanceService totalInstanceService;



}
