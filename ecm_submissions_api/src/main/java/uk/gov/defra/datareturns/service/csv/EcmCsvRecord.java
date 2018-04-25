package uk.gov.defra.datareturns.service.csv;

import com.univocity.parsers.annotations.Parsed;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import uk.gov.defra.datareturns.data.model.dataset.Dataset;
import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.exceptions.ProcessingException;
import uk.gov.defra.datareturns.validation.constraints.annotations.ValidMonitoringDate;
import uk.gov.defra.datareturns.validation.constraints.annotations.ValidPermitSiteRelationship;
import uk.gov.defra.datareturns.validation.constraints.annotations.ValidReturnPeriod;
import uk.gov.defra.datareturns.service.csv.fields.MonitoringDate;

import javax.validation.constraints.Pattern;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;

@Getter
@Setter
@ValidPermitSiteRelationship
public class EcmCsvRecord {
    private static final String VALUE_PATTERN_STR = "(?<Equality>[<>]?)\\s*(?<Number>-?(?:\\d{1,15}\\.)?\\d{1,15})";
    private static final java.util.regex.Pattern VALUE_PATTERN = java.util.regex.Pattern.compile(VALUE_PATTERN_STR);

    private static final Map<String, Field> PARSED_FIELD_MAP = new HashMap<>();

    static {
        for (final Field field : EcmCsvRecord.class.getDeclaredFields()) {
            final Parsed parsed = field.getDeclaredAnnotation(Parsed.class);
            if (parsed != null) {
                for (final String fieldName : parsed.field()) {
                    PARSED_FIELD_MAP.put(fieldName, field);
                }
                field.setAccessible(true);
            }
        }
    }

    @Parsed(field = "EA_ID")
    @NotEmpty(message = EcmErrorCodes.Missing.EA_ID)
    private String eaId;
    @Parsed(field = "Site_Name")
    @NotEmpty(message = EcmErrorCodes.Missing.SITE_NAME)
    private String siteName;
    @Parsed(field = "Rtn_Type")
    @NotEmpty(message = EcmErrorCodes.Missing.RTN_TYPE)
    private String returnType;
    @Parsed(field = "Mon_Date")
    @NotEmpty(message = EcmErrorCodes.Missing.MON_DATE)
    @ValidMonitoringDate
    private String monitoringDate;
    @Parsed(field = "Mon_Point")
    @NotBlank(message = EcmErrorCodes.Missing.MON_POINT)
    @Length(max = 50, message = EcmErrorCodes.Length.MON_POINT)
    private String monitoringPoint;
    @Parsed(field = "Parameter")
    @NotEmpty(message = EcmErrorCodes.Missing.PARAMETER)
    private String parameter;
    @Parsed(field = "Value")
    @Pattern(regexp = VALUE_PATTERN_STR, message = EcmErrorCodes.Incorrect.VALUE)
    private String value;
    @Parsed(field = "Txt_Value")
    private String textValue;
    @Parsed(field = "Qualifier")
    private String qualifier;
    @Parsed(field = "Unit")
    private String unit;
    @Parsed(field = "Ref_Period")
    private String referencePeriod;
    @Parsed(field = "Meth_Stand")
    private String methodOrStandard;
    @Parsed(field = "Rtn_Period")
    @ValidReturnPeriod
    private String returnPeriod;
    @Parsed(field = "Comments")
    @Length(max = 255, message = EcmErrorCodes.Length.COMMENTS)
    private String comments;

    public String getFieldValueForHeading(final String heading) {
        try {
            final Field field = PARSED_FIELD_MAP.get(heading);
            if (field != null) {
                return Objects.toString(field.get(this), null);
            }
        } catch (final IllegalAccessException e) {
            throw new ProcessingException(e);
        }
        return null;
    }


    /**
     * Create a {@link Record} entity from this ECM CSV record.
     * <p>
     * NOTE:  If the data for a field cannot be resolved, then certain default values are used to avoid further validation errors.
     *
     * @param parentDataset
     * @param resolver
     * @return
     */
    public Record toPersisentEntity(final Dataset parentDataset, final MasterDataIdResolver resolver) {
        final Record record = new Record();

        record.setDataset(parentDataset);
        record.setReturnType(resolver.resolveId(EcmCsvField.Rtn_Type, getReturnType()));

        final MonitoringDate monDate = new MonitoringDate(getMonitoringDate());
        if (monDate.isParsed()) {
            record.setMonitoringDate(Date.from(monDate.getInstant()));
        } else {
            record.setMonitoringDate(Date.from(Instant.EPOCH));
        }
        record.setMonitoringPoint(getMonitoringPoint());
        record.setParameter(resolver.resolveId(EcmCsvField.Parameter, getParameter()));

        if (StringUtils.isNotEmpty(value)) {
            final Matcher valueMatcher = VALUE_PATTERN.matcher(value);
            if (valueMatcher.matches()) {
                final Record.Equality equality = Record.Equality.forSymbol(valueMatcher.group("Equality"));
                final BigDecimal numericValue = new BigDecimal(valueMatcher.group("Number"));
                record.setNumericEquality(equality);
                record.setNumericValue(numericValue);
            } else {
                record.setNumericValue(new BigDecimal(0));
            }
        }

        record.setTextValue(resolver.resolveId(EcmCsvField.Txt_Value, getTextValue()));
        record.setQualifier(resolver.resolveId(EcmCsvField.Qualifier, getQualifier()));
        record.setUnit(resolver.resolveId(EcmCsvField.Unit, getUnit()));
        record.setReferencePeriod(resolver.resolveId(EcmCsvField.Ref_Period, getReferencePeriod()));
        record.setMethodOrStandard(resolver.resolveId(EcmCsvField.Meth_Stand, getMethodOrStandard()));
        record.setReturnPeriod(getReturnPeriod());
        record.setComments(getComments());
        return record;
    }


    public interface MasterDataIdResolver {
        Long resolveId(EcmCsvField field, String inputValue);
    }
}
