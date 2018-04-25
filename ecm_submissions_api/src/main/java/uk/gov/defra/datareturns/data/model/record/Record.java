package uk.gov.defra.datareturns.data.model.record;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.dataset.Dataset;
import uk.gov.defra.datareturns.service.csv.EcmErrorCodes;
import uk.gov.defra.datareturns.validation.constraints.annotations.ProhibitTxtValueWithValue;
import uk.gov.defra.datareturns.validation.constraints.annotations.ProhibitUnitWithTxtValue;
import uk.gov.defra.datareturns.validation.constraints.annotations.RequireCommentsForTextValueComment;
import uk.gov.defra.datareturns.validation.constraints.annotations.RequireUnitWithValue;
import uk.gov.defra.datareturns.validation.constraints.annotations.RequireValueOrTxtValue;
import uk.gov.defra.datareturns.validation.constraints.annotations.ValidReturnPeriod;
import uk.gov.defra.datareturns.validation.service.MasterDataEntity;
import uk.gov.defra.datareturns.validation.validators.id.ValidId;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.util.Date;

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

@ProhibitTxtValueWithValue
@RequireValueOrTxtValue
@ProhibitUnitWithTxtValue
@RequireUnitWithValue
@RequireCommentsForTextValueComment
public class Record extends AbstractBaseEntity {
    @ManyToOne
    @JoinColumn(name = "dataset", updatable = false)
    private Dataset dataset;

    @Basic
    @Column(name = "return_type")
    @NotNull(message = EcmErrorCodes.Missing.RTN_TYPE)
    @ValidId(entity = MasterDataEntity.RETURN_TYPE, message = EcmErrorCodes.Incorrect.RTN_TYPE)
    private Long returnType;

    @Basic
    @Column(name = "mon_date")
    @NotNull(message = EcmErrorCodes.Missing.MON_DATE)
    @Past(message = EcmErrorCodes.Incorrect.MON_DATE)
    @SuppressFBWarnings("EI_EXPOSE_REP")
    private Date monitoringDate;

    @Basic
    @Column(name = "mon_point")
    @NotBlank(message = EcmErrorCodes.Missing.MON_POINT)
    @Length(max = 50, message = EcmErrorCodes.Length.MON_POINT)
    private String monitoringPoint;

    @Basic
    @Column(name = "parameter")
    @NotNull(message = EcmErrorCodes.Missing.PARAMETER)
    @ValidId(entity = MasterDataEntity.PARAMETER, message = EcmErrorCodes.Incorrect.PARAMETER)
    private Long parameter;

    @Basic
    @Column(precision = 30, scale = 15)
    private BigDecimal numericValue;

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "numeric_equality")
    private Equality numericEquality;

    @Basic
    @Column(name = "text_value")
    @ValidId(entity = MasterDataEntity.TEXT_VALUE, message = EcmErrorCodes.Incorrect.TXT_VALUE)
    private Long textValue;

    @Basic
    @Column(name = "qualifier")
    @ValidId(entity = MasterDataEntity.QUALIFIER, message = EcmErrorCodes.Incorrect.QUALIFIER)
    private Long qualifier;

    @Basic
    @Column(name = "unit")
    @ValidId(entity = MasterDataEntity.UNIT, message = EcmErrorCodes.Incorrect.UNIT)
    private Long unit;

    @Basic
    @Column(name = "reference_period")
    @ValidId(entity = MasterDataEntity.REFERENCE_PERIOD, message = EcmErrorCodes.Incorrect.REF_PERIOD)
    private Long referencePeriod;

    @Basic
    @Column(name = "method_or_standard")
    @ValidId(entity = MasterDataEntity.METHOD_OR_STANDARD, message = EcmErrorCodes.Incorrect.METH_STAND)
    private Long methodOrStandard;

    @Basic
    @Column(name = "return_period")
    @ValidReturnPeriod
    private String returnPeriod;

    @Basic
    @Column(name = "comments")
    @Length(max = 255, message = EcmErrorCodes.Length.COMMENTS)
    private String comments;

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
