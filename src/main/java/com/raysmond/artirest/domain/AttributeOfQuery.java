package com.raysmond.artirest.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.context.annotation.Bean;

public class AttributeOfQuery {

    private String name;

    private String type;

    @JsonSerialize
    private Object value;

    private String operator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
