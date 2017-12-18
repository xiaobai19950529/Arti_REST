package com.raysmond.artirest.domain;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by dashen on 17-6-5.
 */
public class StateCount {
    public String artifactId;
    public Map<String,State> stateruntime = new LinkedHashMap<String,State>();//计算停留在每个状态的运行时间

    public StateCount(){}

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public Map<String, State> getStateruntime() {
        return stateruntime;
    }

    public void setStateruntime(Map<String, State> stateruntime) {
        this.stateruntime = stateruntime;
    }
}
