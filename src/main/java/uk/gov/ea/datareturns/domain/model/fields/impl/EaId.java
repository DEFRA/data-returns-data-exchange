package uk.gov.ea.datareturns.domain.model.fields.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.model.rules.EaIdType;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.UniqueIdentifierAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

import java.util.Optional;

/**
 * Models details about an EA Unique Identifier (EA_ID)
 *
 * @author Sam Gardner-Dell
 */
public class EaId extends AbstractEntityValue<UniqueIdentifierDao, DataSample, UniqueIdentifier> implements Comparable<EaId> {
    private static final UniqueIdentifierDao DAO = EntityDao.getDao(UniqueIdentifierDao.class);

    @NotBlank(message = MessageCodes.Missing.EA_ID)
    @ControlledList(auditor = UniqueIdentifierAuditor.class, message = MessageCodes.ControlledList.EA_ID)
    private String identifier;

    private EaIdType type;

    /**
     * Create a new {@link EaId} from the given identifier
     *
     * @param identifier the String representation of the unique identifier.
     */
    @JsonCreator
    public EaId(final String identifier) {
        super(identifier);
        this.identifier = identifier;
        if (getEntity() != null) {
            this.type = EaIdType.forUniqueId(getEntity().getName());
        }
    }

    @JsonIgnore
    protected UniqueIdentifier findEntity(String inputValue) {
        return getDao().getByNameOrAlias(Key.explicit(inputValue));
    }

    @Override protected UniqueIdentifierDao getDao() {
        return DAO;
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
    @JsonIgnore
    public boolean isNumeric() {
        return EaIdType.LOWER_NUMERIC.equals(this.type) || EaIdType.UPPER_NUMERIC.equals(this.type);
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

    @Override public String transform(DataSample record) {
        Key lookup = Key.explicit(this.getInputValue());
        return Optional.ofNullable(getDao().getPreferred(lookup)).map(ControlledListEntity::getName).orElse(null);
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

        if (getEntity() != null) {
            return getEntity().equals(eaId.getEntity());
        } else {
            return identifier != null ? identifier.equals(eaId.identifier) : eaId.identifier == null;
        }
    }

    /**
     * Generate a hashcode based on the identifier value
     *
     * @return the generated hashcode
     */
    @Override
    public int hashCode() {
        if (getEntity() != null) {
            return getEntity().getName().hashCode();
        } else {
            return identifier != null ? identifier.hashCode() : 0;
        }
    }

}