package uk.gov.ea.datareturns.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the unique_identifiers database table.
 *
 */
@Entity
@Table(name = "unique_identifiers")
@NamedQueries({
	@NamedQuery(name = "UniqueIdentifier.findAll", query = "SELECT u FROM UniqueIdentifier u"),
	@NamedQuery(name = "UniqueIdentifier.findAllIdentifiers", query = "SELECT u.identifier FROM UniqueIdentifier u"),
	@NamedQuery(name = "UniqueIdentifier.findByIdentifier", query = "SELECT u FROM UniqueIdentifier u WHERE u.identifier = :identifier")
})
public class UniqueIdentifier {

	@Id
	private String identifier;

	public UniqueIdentifier() {
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

}