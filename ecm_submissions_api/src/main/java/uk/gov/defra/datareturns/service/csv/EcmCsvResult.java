package uk.gov.defra.datareturns.service.csv;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.defra.datareturns.data.model.dataset.Dataset;
import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.data.model.upload.Upload;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class EcmCsvResult {
    private final Map<String, ValidationErrorClass> violationsMap = new HashMap<>();
    private final Map<Record, EcmCsvRecord> sourceRecordMap = new HashMap<>();
    private final Map<EcmCsvRecord, Integer> recordIndexMap = new HashMap<>();
    private final List<EcmCsvRecord> data = new ArrayList<>();
    @Getter
    @Setter
    private Upload upload;

    public EcmCsvResult(final List<EcmCsvRecord> data) {
        this.data.addAll(data);
        for (int i = 0; i < data.size(); i++) {
            this.recordIndexMap.put(data.get(i), i);
        }
    }

    public void addViolation(final ConstraintViolation<?> violation) {
        final List<EcmCsvRecord> relatedRecords;
        if (violation.getLeafBean() instanceof Dataset) {
            final Set<Record> entityRecords = ((Dataset) violation.getLeafBean()).getRecords();
            relatedRecords = entityRecords.stream().map(sourceRecordMap::get).collect(Collectors.toList());
        } else if (violation.getLeafBean() instanceof Record) {
            relatedRecords = Collections.singletonList(sourceRecordMap.get(violation.getLeafBean()));
        } else if (violation.getLeafBean() instanceof EcmCsvRecord) {
            relatedRecords = Collections.singletonList((EcmCsvRecord) violation.getLeafBean());
        } else {
            log.error("Unexpected leaf bean type returned by validator.  Assuming all records are in error");
            relatedRecords = Collections.emptyList();
        }

        for (final EcmCsvRecord record : relatedRecords) {
            includeValidationInstance(violation.getMessageTemplate(), violation.getMessage(), getLineNumber(record));
        }
    }

    public void bind(final Record record, final EcmCsvRecord csvRecord) {
        sourceRecordMap.put(record, csvRecord);

    }

    public boolean hasViolations() {
        return violationsMap != null && !violationsMap.isEmpty();
    }


    /**
     * Retrieve all constraint violations of the validation rules.
     * <p>
     * This method will return errors sorted by the order in which they are encountered.
     *
     * @return a {@link List} of all constraint violations
     */
    public List<ValidationErrorClass> getViolations() {
        final List<ValidationErrorClass> violations = new ArrayList<>(violationsMap.values());
        violations.sort((a, b) -> {
            final Integer aFirstLine = a.getInstances().stream().flatMap(i -> i.getLineNumbers().stream()).sorted().findFirst().orElse(0);
            final Integer bFirstLine = b.getInstances().stream().flatMap(i -> i.getLineNumbers().stream()).sorted().findFirst().orElse(0);
            return aFirstLine.compareTo(bFirstLine);
        });
        return violations;
    }

    public int getIndex(final EcmCsvRecord csvRecord) {
        return this.recordIndexMap.get(csvRecord);
    }

    public int getLineNumber(final EcmCsvRecord csvRecord) {
        return getIndex(csvRecord) + 2;
    }

    public int getIndex(final int lineNumber) {
        return lineNumber - 2;
    }

    public int getIndex(final Record record) {
        return getIndex(sourceRecordMap.get(record));
    }


    public ValidationErrorClass includeValidationClass(final String violationMessageTemplate, final String message) {
        final ValidationErrorClass errorClass = violationsMap
                .computeIfAbsent(violationMessageTemplate, t -> ValidationErrorClass.of(EcmErrorCodes.toErrorCode(t), message));
        return errorClass;
    }

    public ValidationErrorInstance includeValidationInstance(final String violationMessageTemplate, final String message, final int lineNumber) {
        final ValidationErrorClass errorClass = includeValidationClass(violationMessageTemplate, message);

        final EcmCsvRecord rowData = data.get(getIndex(lineNumber));
        final List<EcmCsvField> csvFields = EcmErrorCodes.getFieldDependencies(violationMessageTemplate);
        final Map<String, String> invalidValueMap = new HashMap<>();
        final StringBuilder uniqueErrorKey = new StringBuilder();
        if (csvFields != null) {
            for (final EcmCsvField field : csvFields) {
                final String fieldData = Optional.ofNullable(rowData.getFieldValueForHeading(field.getFieldName())).orElse("");
                uniqueErrorKey.append(field.getFieldName());
                uniqueErrorKey.append("|");
                uniqueErrorKey.append(fieldData);
                invalidValueMap.put(field.getFieldName(), fieldData);
            }
        }

        final ValidationErrorInstance instance = errorClass.getInstancesByErrorValue()
                .computeIfAbsent(uniqueErrorKey.toString(), k -> ValidationErrorInstance.of(invalidValueMap));

        instance.getLineNumbers().add(lineNumber);
        return instance;
    }


}
