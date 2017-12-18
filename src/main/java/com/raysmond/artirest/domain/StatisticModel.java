package com.raysmond.artirest.domain;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by dashen on 17-6-1.
 */
@Component
public class StatisticModel {
    public String processName;
    public Map<String,StateNumber> statenumber = new LinkedHashMap<String,StateNumber>(); //计算停留在每个状态上当前的流程数
    public Map<String,StateCount> statecount = new LinkedHashMap<String,StateCount>(); //计算每个artifact实例中各状态的时间
    public int flag = 0;

    public StatisticModel(){}

    public StatisticModel(String processName, Map<String,StateNumber> statenumber, Map<String,StateCount> statecount){
        this.processName = processName;
        this.statenumber = statenumber;
        this.statecount = statecount;
    }


    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }



    public Map<String, StateCount> getStatecount() {
        return statecount;
    }

    public void setStatecount(Map<String, StateCount> statecount) {
        this.statecount = statecount;
    }
}
