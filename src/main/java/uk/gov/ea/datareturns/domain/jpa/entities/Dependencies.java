package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.*;

/**
 * Created by graham on 03/10/16.
 */
@Entity
@Table(name = "dependencies")
public class Dependencies {

    @EmbeddedId
    private DependenciesId dependenciesId;

    @Basic
    @Column(name = "return_type", nullable = false, length = 80)
    public String getReturnType() {
        return dependenciesId.returnType;
    }

    public void setReturnType(String returnType) {
        dependenciesId.returnType = returnType;
    }

    @Basic
    @Column(name = "parameter", nullable = false, length = 150)
    public String getParameter() {
        return dependenciesId.parameter;
    }

    public void setParameter(String parameter) {
        dependenciesId.parameter = parameter;
    }

    @Basic
    @Column(name = "releases_and_transfers", nullable = false, length = 50)
    public String getReleasesAndTransfers() {
        return dependenciesId.releasesAndTransfers;
    }

    public void setReleasesAndTransfers(String releasesAndTransfers) {
        dependenciesId.releasesAndTransfers = releasesAndTransfers;
    }

    @Basic
    @Column(name = "units", nullable = false, length = 10)
    public String getUnits() {
        return dependenciesId.units;
    }

    public void setUnits(String units) {
        dependenciesId.units = units;
    }

    @Override
    public String toString() {
        return "Dependencies{" +
                "dependenciesId=" + dependenciesId +
                '}';
    }
}
