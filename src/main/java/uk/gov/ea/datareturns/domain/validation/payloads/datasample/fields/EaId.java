package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias;
import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.common.entityfields.FieldValue;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.rules.EaIdType;

/**
 * Models details about an EA Unique Identifier (EA_ID)
 *
 * @author Sam Gardner-Dell
 */
public class EaId implements FieldValue<EaId>, Comparable<EaId> {
    public static final String FIELD_NAME = "EA_ID";

    @NotBlank(message = "DR9000-Missing")
    @ControlledList(entities = { UniqueIdentifier.class, UniqueIdentifierAlias.class }, message = "DR9000-Incorrect")
    private String identifier;

    private EaIdType type;

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

    @Override
    public String getInputValue() {
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
    public boolean isNumeric() {
        return EaIdType.LOWER_NUMERIC.equals(this.type) || EaIdType.UPPER_NUMERIC.equals(this.type);
    }

    /**
     * Determine if the identifier is an alphanumeric type
     *
     * @return true if the identifier is alphanumeric, false if the identifier is numeric
     */
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
}