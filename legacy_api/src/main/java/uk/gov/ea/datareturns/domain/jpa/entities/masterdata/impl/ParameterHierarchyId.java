package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Compound primary key for dependencies
 */
@Embeddable
public class ParameterHierarchyId implements Serializable {

    @Column(name = "return_type", nullable = false, length = 80)
    protected String returnType;

    @Column(name = "parameter", nullable = false, length = 150)
    protected String parameter;

    @Column(name = "releases_and_transfers", nullable = false, length = 50)
    protected String releasesAndTransfers;

    @Column(name = "units", nullable = false, length = 10)
    protected String units;

    public ParameterHierarchyId() {}

    public ParameterHierarchyId(String returnType, String parameter, String releasesAndTransfers, String units) {
        this.returnType = returnType;
        this.parameter = parameter;
        this.releasesAndTransfers = releasesAndTransfers;
        this.units = units;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParameterHierarchyId that = (ParameterHierarchyId) o;

        if (returnType != null ? !returnType.equals(that.returnType) : that.returnType != null) return false;
        if (parameter != null ? !parameter.equals(that.parameter) : that.parameter != null) return false;
        if (releasesAndTransfers != null ? !releasesAndTransfers.equals(that.releasesAndTransfers) : that.releasesAndTransfers != null)
            return false;
        if (units != null ? !units.equals(that.units) : that.units != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = returnType != null ? returnType.hashCode() : 0;
        result = 31 * result + (parameter != null ? parameter.hashCode() : 0);
        result = 31 * result + (releasesAndTransfers != null ? releasesAndTransfers.hashCode() : 0);
        result = 31 * result + (units != null ? units.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ParameterHierarchyId{" +
                "returnType='" + returnType + '\'' +
                ", parameter='" + parameter + '\'' +
                ", releasesAndTransfers='" + releasesAndTransfers + '\'' +
                ", units='" + units + '\'' +
                '}';
    }
}
