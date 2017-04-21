package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import javax.persistence.*;

/**
 * Created by graham on 03/10/16.
 */
@Entity
@Table(name = "parameter_hierarchy")
public class ParameterHierarchy {

    @EmbeddedId
    private ParameterHierarchyId parameterHierarchyId;

    @Basic
    @Column(name = "return_type", nullable = false, length = 80)
    public String getReturnType() {
        return parameterHierarchyId.returnType;
    }

    public void setReturnType(String returnType) {
        parameterHierarchyId.returnType = returnType;
    }

    @Basic
    @Column(name = "parameter", nullable = false, length = 150)
    public String getParameter() {
        return parameterHierarchyId.parameter;
    }

    public void setParameter(String parameter) {
        parameterHierarchyId.parameter = parameter;
    }

    @Basic
    @Column(name = "releases_and_transfers", nullable = false, length = 50)
    public String getReleasesAndTransfers() {
        return parameterHierarchyId.releasesAndTransfers;
    }

    public void setReleasesAndTransfers(String releasesAndTransfers) {
        parameterHierarchyId.releasesAndTransfers = releasesAndTransfers;
    }

    @Basic
    @Column(name = "units", nullable = false, length = 50)
    public String getUnits() {
        return parameterHierarchyId.units;
    }

    public void setUnits(String units) {
        parameterHierarchyId.units = units;
    }

    @Override
    public String toString() {
        return "ParameterHierarchy{" +
                "parameterHierarchyId=" + parameterHierarchyId +
                '}';
    }
}
