package com.raysmond.artirest.domain;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;
import java.util.Set;

/**
 * Created by xiaobai on 17-7-25.
 */
public class StateNumberOfModel {
    public String processModelId;
    public Map<String,Integer> statenumber;

    public Integer instance;
    public Integer pending;
    public Integer running;
    public Integer ended;

    public Set<Process> processes;

    public StateNumberOfModel(){

    }

    public String getProcessModelId() {
        return processModelId;
    }

    public void setProcessModelId(String processModelId) {
        this.processModelId = processModelId;
    }

    public Map<String, Integer> getStatenumber() {
        return statenumber;
    }

    public void setStatenumber(Map<String, Integer> statenumber) {
        this.statenumber = statenumber;
    }



}
