package com.raysmond.artirest.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Coordinator.
 */
@Document(collection = "coordinator")
public class Coordinator implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @Field("first_process_id")
    private String firstProcessId;

    @Field("first_process_name")
    private String firstProcessName;

    @Field("first_process_attr")
    private String firstProcessAttr;

    @Field("second_process_id")
    private String secondProcessId;

    @Field("second_process_name")
    private String secondProcessName;

    @Field("second_process_attr")
    private String secondProcessAttr;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstProcessId() {
        return firstProcessId;
    }

    public Coordinator firstProcessId(String firstProcessId) {
        this.firstProcessId = firstProcessId;
        return this;
    }

    public void setFirstProcessId(String firstProcessId) {
        this.firstProcessId = firstProcessId;
    }

    public String getFirstProcessName() {
        return firstProcessName;
    }

    public Coordinator firstProcessName(String firstProcessName) {
        this.firstProcessName = firstProcessName;
        return this;
    }

    public void setFirstProcessName(String firstProcessName) {
        this.firstProcessName = firstProcessName;
    }

    public String getFirstProcessAttr() {
        return firstProcessAttr;
    }

    public Coordinator firstProcessAttr(String firstProcessAttr) {
        this.firstProcessAttr = firstProcessAttr;
        return this;
    }

    public void setFirstProcessAttr(String firstProcessAttr) {
        this.firstProcessAttr = firstProcessAttr;
    }

    public String getSecondProcessId() {
        return secondProcessId;
    }

    public Coordinator secondProcessId(String secondProcessId) {
        this.secondProcessId = secondProcessId;
        return this;
    }

    public void setSecondProcessId(String secondProcessId) {
        this.secondProcessId = secondProcessId;
    }

    public String getSecondProcessName() {
        return secondProcessName;
    }

    public Coordinator secondProcessName(String secondProcessName) {
        this.secondProcessName = secondProcessName;
        return this;
    }

    public void setSecondProcessName(String secondProcessName) {
        this.secondProcessName = secondProcessName;
    }

    public String getSecondProcessAttr() {
        return secondProcessAttr;
    }

    public Coordinator secondProcessAttr(String secondProcessAttr) {
        this.secondProcessAttr = secondProcessAttr;
        return this;
    }

    public void setSecondProcessAttr(String secondProcessAttr) {
        this.secondProcessAttr = secondProcessAttr;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coordinator coordinator = (Coordinator) o;
        if (coordinator.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), coordinator.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Coordinator{" +
            "id=" + getId() +
            ", firstProcessId='" + getFirstProcessId() + "'" +
            ", firstProcessName='" + getFirstProcessName() + "'" +
            ", firstProcessAttr='" + getFirstProcessAttr() + "'" +
            ", secondProcessId='" + getSecondProcessId() + "'" +
            ", secondProcessName='" + getSecondProcessName() + "'" +
            ", secondProcessAttr='" + getSecondProcessAttr() + "'" +
            "}";
    }
}
