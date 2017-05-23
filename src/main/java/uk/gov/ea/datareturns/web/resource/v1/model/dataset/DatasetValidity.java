package uk.gov.ea.datareturns.web.resource.v1.model.dataset;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;

import java.util.*;

/**
 * Container for validation error detail found in the validated model
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(description = "Full dataset validation status data model")
public class DatasetValidity {
    private Map<String, Violation> violations = new LinkedHashMap<>();

    /**
     * Check if a validation error exists
     *
     * @return true if a validation error exists, false otherwise
     */
    @JsonProperty("valid")
    public boolean isValid() {
        return this.violations.isEmpty();
    }

    @JacksonXmlElementWrapper(localName = "violations")
    @JacksonXmlProperty(localName = "violation")
    public List<Violation> getViolations() {
        return new ArrayList<>(violations.values());
    }

    public void addViolation(EntityReference constraint, EntityReference record) {
        String constraintId = constraint.getId();
        Violation violation = violations.get(constraintId);
        if (violation == null) {
            violation = new Violation();
            violation.setConstraint(constraint);
            violations.put(constraintId, violation);
        }
        violation.addRecord(record);
    }

    public static class Violation {
        public EntityReference constraint;

        public Set<EntityReference> records;

        public EntityReference getConstraint() {
            return constraint;
        }

        public void setConstraint(EntityReference constraint) {
            this.constraint = constraint;
        }

        public Set<EntityReference> getRecords() {
            return records;
        }

        public void setRecords(Set<EntityReference> records) {
            this.records = records;
        }

        public void addRecord(EntityReference record) {
            if (this.records == null) {
                records = new LinkedHashSet<>();
            }
            this.records.add(record);
        }
    }
}