package uk.gov.ea.datareturns.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.model.rules.EaIdType;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.UniqueIdentifierAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

import javax.validation.constraints.Pattern;

/**
 * Models details about an EA Unique Identifier (EA_ID)
 *
 * @author Sam Gardner-Dell
 */
public class EaId implements Comparable<EaId> {
    @NotBlank(message = "{DR9000-Missing}")
    @Pattern(regexp = "(^[A-Za-z]{2}[0-9]{4}[A-Za-z]{2}|^[0-9]{5,6}$)", message = "{DR9000-Incorrect}")
    @ControlledList(auditor = UniqueIdentifierAuditor.class, message = "{DR9000-Incorrect}")
    @JsonProperty
    private String identifier;

    private EaIdType type;

    /**
     * Default constructor (for serialization purposes only)
     */
    public EaId() {

    }

    /**
     * Create a new {@link EaId} from the given identifier
     *
     * @param identifier the String representation of the unique identifier.
     */
    public EaId(final String identifier) {
        this.identifier = identifier;
        this.type = EaIdType.forUniqueId(identifier);
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * @return the type
     */
    public EaIdType getType() {
        return this.type;
    }

    /**
     * Determine if the identifier is a numeric type
     *
     * @return true if the identifier is numeric, false if the identifier is alphanumeric
     */
    @JsonIgnore
    public boolean isNumeric() {
        return EaIdType.LOWER_NUMERIC.equals(this.type)
                || EaIdType.UPPER_NUMERIC.equals(this.type);
    }

    /**
     * Determine if the identifier is an alphanumeric type
     *
     * @return true if the identifier is alphanumeric, false if the identifier is numeric
     */
    @JsonIgnore
    public boolean isAlphaNumeric() {
        return !isNumeric();
    }

    /**
     * Compare this {@link EaId} to the specified {@link EaId} to determine the natural sort order.
     *
     * Rules for sorting {@link EaId}s:
     * - Numeric Unique Identifiers appear before alphanumeric identifiers and are sorted by natural integer sort order
     * - Alphanumeric Unique identifiers appear next and are sorted using natural lexicographical sort order.
     *
     * @param o the {@link EaId} to compare to this instance
     * @return an {@link Integer} according to default Java compareTo rules.
     */
    @Override
    public int compareTo(final EaId o) {
        if (isNumeric() && o.isNumeric()) {
            // Numeric comparison
            final Long thisId = NumberUtils.toLong(this.identifier);
            final Long otherId = NumberUtils.toLong(o.identifier);
            return thisId.compareTo(otherId);
        } else if (isNumeric() && o.isAlphaNumeric()) {
            return -1;
        } else if (isAlphaNumeric() && o.isNumeric()) {
            return 1;
        }
        // Default alpha comparison
        return this.identifier.compareTo(o.identifier);
    }

    /**
     * Determine if two {@link EaId}s are equal.  This method checks the identifier value only.
     *
     * @param o the {@link EaId} to check equality against
     * @return true if the two identifiers are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EaId eaId = (EaId) o;
        return identifier != null ? identifier.equals(eaId.identifier) : eaId.identifier == null;
    }

    /**
     * Generate a hashcode based on the identifier value
     *
     * @return the generated hashcode
     */
    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }
}