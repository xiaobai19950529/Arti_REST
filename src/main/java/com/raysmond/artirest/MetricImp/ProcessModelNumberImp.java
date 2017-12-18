package com.raysmond.artirest.MetricImp;


import com.codahale.metrics.Gauge;

public class ProcessModelNumberImp implements Gauge<Integer> {
    Integer num;

    public ProcessModelNumberImp(Integer num){
        this.num = num;
    }

    @Override
    public Integer getValue(){
        return num;
//        return metricValue(num);
    }

    private Integer metricValue(Integer num){
        return num;
    }
}
