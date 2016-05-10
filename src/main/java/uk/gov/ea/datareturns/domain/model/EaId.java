/**
 *
 */
package uk.gov.ea.datareturns.domain.model;

import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.ea.datareturns.domain.model.rules.EaIdType;
import uk.gov.ea.datareturns.domain.model.validation.auditors.UniqueIdentifierAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

/**
 * Models details about an EA Unique Identifier (EA_ID)
 *
 * @author Sam Gardner-Dell
 */
public class EaId implements Comparable<EaId> {
	@NotBlank(message = "{DR9000-Missing}")
	@Pattern(regexp = "(^[A-Za-z][A-Za-z].*|^[0-9]{5,6}$)", message = "{DR9000-Incorrect}")
	@ControlledList(auditor = UniqueIdentifierAuditor.class, message = "{DR9000-Incorrect}")
	@JsonProperty
	private final String identifier;

	private final EaIdType type;

	/**
	 * Create a new {@link EaId} from the given identifier
	 *
	 * @param identifier
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.identifier == null) ? 0 : this.identifier.hashCode());
		result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EaId other = (EaId) obj;
		if (this.identifier == null) {
			if (other.identifier != null) {
				return false;
			}
		} else if (!this.identifier.equals(other.identifier)) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}
		return true;
	}
}