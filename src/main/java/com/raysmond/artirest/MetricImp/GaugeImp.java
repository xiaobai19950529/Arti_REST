package com.raysmond.artirest.MetricImp;

import com.codahale.metrics.Gauge;
import io.swagger.models.auth.In;

public class GaugeImp implements Gauge<Integer>{
    String state;

    public GaugeImp(String state){
        this.state = state;
    }

    @Override
    public Integer getValue(){
       return metricValue(state);
    }

    private Integer metricValue(String state){
        if(state.equals("applied")) return 2;
        if(state.equals("approved")) return 3;
        if(state.equals("confirmed")) return 4;

        return 0;
    }
}
