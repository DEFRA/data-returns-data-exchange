package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the qualifiers database table.
 *
 */
@Entity
@Table(name = "qualifiers")
@NamedQueries({
		@NamedQuery(name = "Qualifier.findAll", query = "SELECT q FROM Qualifier q"),
		@NamedQuery(name = "Qualifier.findAllNames", query = "SELECT q.name FROM Qualifier q"),
		@NamedQuery(name = "Qualifier.findByName", query = "SELECT q FROM Qualifier q WHERE q.name = :name")
})
public class Qualifier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	public Qualifier() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

}