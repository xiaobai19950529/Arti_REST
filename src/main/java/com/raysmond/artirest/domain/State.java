package com.raysmond.artirest.domain;

import java.time.ZonedDateTime;

/**
 * Created by dashen on 17-5-15.
 */
public class State {

    public String name; //状态的名字
    public ZonedDateTime entertime; //状态的进入时间
    public ZonedDateTime quittime;  //状态的离开时间
    public long duration;

    public StateModel.StateType type;

    public State(){
        this.name = null;
        this.type = null;
        this.entertime = null;
        this.quittime = null;
        this.duration = 0;
    }

    public State(String name, StateModel.StateType type, ZonedDateTime entertime, ZonedDateTime quittime, long duration){
        this.name = name;
        this.type = type;
        this.entertime = entertime;
        this.quittime = quittime;
        this.duration = duration;
    }


}
