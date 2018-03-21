package uk.gov.defra.datareturns.data.model.record;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.dataset.Dataset;
import uk.gov.defra.datareturns.service.MasterDataEntity;
import uk.gov.defra.datareturns.validation.constraints.annotations.ValidReturnPeriod;
import uk.gov.defra.datareturns.validation.constraints.controlledlist.ControlledList;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * ECM Submission Record
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "ecm_record")
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "ecm_record_id_seq")
                  }
)
@Getter
@Setter

// FIXME - record level validation
//@ProhibitTxtValueWithValue
//@RequireValueOrTxtValue
//@ProhibitUnitWithTxtValue
//@RequireUnitWithValue
//@SiteMatchesUniqueIdentifier
//@RequireCommentsForTextValueComment
public class Record extends AbstractBaseEntity {
    @ManyToOne
    @JoinColumn(name = "dataset", updatable = false)
    private Dataset dataset;

    @Basic
    @Column(name = "return_type")
    @NotBlank(message = "DR9010-Missing")
    @ControlledList(entities = MasterDataEntity.RETURN_TYPE, message = "DR9010-Incorrect")
    private String returnType;

    @Basic
    @Column(name = "mon_date")
    @NotNull(message = "DR9020-Missing")
    private Date monitoringDate;

    @Basic
    @Column(name = "mon_point")
    @NotBlank(message = "DR9060-Missing")
    @Length(max = 50, message = "DR9060-Length")
    private String monitoringPoint;

    @Basic
    @Column(name = "parameter")
    @NotBlank(message = "DR9030-Missing")
    @ControlledList(entities = MasterDataEntity.PARAMETER, message = "DR9030-Incorrect")
    private String parameter;

    @Basic
    @Column(name = "numeric_value")
    private BigDecimal numericValue;

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "numeric_equality")
    private Equality numericEquality;

    @Basic
    @Column(name = "text_value")
    @ControlledList(entities = MasterDataEntity.TEXT_VALUE, message = "DR9080-Incorrect")
    private String textValue;

    @Basic
    @Column(name = "qualifier")
    @ControlledList(entities = MasterDataEntity.QUALIFIER, message = "DR9180-Incorrect")
    private String qualifier;

    @Basic
    @Column(name = "unit")
    @ControlledList(entities = MasterDataEntity.UNIT, message = "DR9050-Incorrect")
    private String unit;

    @Basic
    @Column(name = "reference_period")
    @ControlledList(entities = MasterDataEntity.REFERENCE_PERIOD, message = "DR9090-Incorrect")
    private String referencePeriod;

    @Basic
    @Column(name = "method_or_standard")
    @ControlledList(entities = MasterDataEntity.METHOD_OR_STANDARD, message = "DR9100-Incorrect")
    private String methodOrStandard;

    @Basic
    @Column(name = "return_period")
    @ValidReturnPeriod
    private String returnPeriod;

    @Basic
    @Column(name = "comments")
    @Length(max = 255, message = "DR9140-Length")
    private String comments;

    // FIXME: Don't use equality based on primary key!
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Record)) {
            return false;
        }
        if (getId() == null) {
            return false;
        }
        final Record record = (Record) o;
        return Objects.equals(getId(), record.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public enum Equality {
        LESS_THAN, GREATER_THAN, EQUAL;

        public static Equality forSymbol(final String symbol) {
            switch (symbol) {
                case "<":
                    return LESS_THAN;
                case ">":
                    return GREATER_THAN;
                default:
                    return EQUAL;
            }
        }
    }
}
