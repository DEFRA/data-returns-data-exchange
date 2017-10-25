package uk.gov.ea.datareturns.web.resource.v1.model.dataset;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Response message container to summarise the information which was parsed from the uploaded file.
 *
 * @author Sam Gardner-Dell
 */
public class DatasetSubstitutions {
    public static final String KEY_FORMAT = "%s-%s-%s";
    private Map<String, Substitution> instances;

    /**
     * Default zero-arg constructor (for serialization support)
     */
    public DatasetSubstitutions() {
        this.instances = new LinkedHashMap<>();
    }

    public void addSubstitution(String recordId, String fieldName, String submitted, String substituted) {
        String mapKey = String.format(KEY_FORMAT, fieldName, submitted, substituted);

        Substitution sub = instances.get(mapKey);
        if (sub == null) {
            sub = new Substitution();
            sub.setFieldName(fieldName);
            sub.setSubmitted(submitted);
            sub.setSubstituted(substituted);
            instances.put(mapKey, sub);
        }
        sub.addRecord(recordId);
    }

    @JacksonXmlElementWrapper(localName = "substitutions")
    @JacksonXmlProperty(localName = "substitution")
    public Collection<Substitution> getSubstitutions() {
        return instances.values();
    }

    public void setSubstitutions(List<Substitution> substitutionList) {
        this.instances = substitutionList.stream().collect(Collectors.toMap(
                s -> String.format(KEY_FORMAT, s.getFieldName(), s.getSubmitted(), s.getSubstituted()),
                s -> s,
                (s1, s2) -> s1, // Merge strategy
                LinkedHashMap::new));
    }

    /**
     * Details of a specific substituted
     *
     * @author Sam Gardner-Dell
     */
    @JsonPropertyOrder(value = { "field", "submitted", "substituted", "records" })
    public static class Substitution {
        /**
         * The name of the field whose value was substituted
         */
        @JsonProperty("field")
        private String fieldName;

        /**
         * The value that was submitted
         */
        @JsonProperty("submitted")
        private String submitted;

        /**
         * The value that the submitted value was substituted to
         */
        @JsonProperty("substituted")
        private String substituted;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "record")
        private List<EntityReference> records;

        /**
         * Default zero-arg constructor (for serialization support)
         */
        @SuppressWarnings("unused")
        public Substitution() {
            this.records = new ArrayList<>();
        }

        public Substitution(String fieldName, String submitted, String substituted) {
            this();
            this.fieldName = fieldName;
            this.submitted = submitted;
            this.substituted = substituted;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getSubmitted() {
            return submitted;
        }

        public void setSubmitted(String submitted) {
            this.submitted = submitted;
        }

        public String getSubstituted() {
            return substituted;
        }

        public void setSubstituted(String substituted) {
            this.substituted = substituted;
        }

        public List<EntityReference> getRecords() {
            return records;
        }

        public void setRecords(List<EntityReference> records) {
            this.records = records;
        }

        public void addRecord(String recordId) {
            EntityReference ref = new EntityReference(recordId, "/url/to/" + recordId);
            this.records.add(ref);
        }
    }
}