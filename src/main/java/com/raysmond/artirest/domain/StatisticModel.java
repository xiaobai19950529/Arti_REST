package com.raysmond.artirest.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.inject.Named;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by dashen on 17-6-1.
 */
@Document(collection = "statistic_model")
public class StatisticModel implements Serializable {

    @Id
    private String id;


    @Field("name")
    public String name = "ArtiREST";

    @Field("model_number")
    public Integer modelnumber;

    @XmlElementWrapper(name = "stateNumberofModels")
    @XmlElement(name = "stateNumberofModel")
    public Map<String,StateNumberOfModel> stateNumberOfModels = new LinkedHashMap<String,StateNumberOfModel>(); //计算停留在每个状态上当前的流程数
//    public Map<String,StateCount> statecount = new LinkedHashMap<String,StateCount>(); //计算每个artifact实例中各状态的时间

    public StatisticModel(){}

    public StatisticModel(String ModelName, Map<String,StateNumberOfModel> stateNumberOfModels){
        this.name = ModelName;
        this.stateNumberOfModels = stateNumberOfModels;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, StateNumberOfModel> getStateNumberOfModels() {
        return stateNumberOfModels;
    }

    public void setStateNumberOfModels(Map<String, StateNumberOfModel> stateNumberOfModels) {
        this.stateNumberOfModels = stateNumberOfModels;
    }
}
