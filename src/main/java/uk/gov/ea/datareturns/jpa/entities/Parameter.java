package uk.gov.ea.datareturns.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the parameters database table.
 *
 */
@Entity
@Table(name = "parameters")
@NamedQueries({
		@NamedQuery(name = "Parameter.findAll", query = "SELECT p FROM Parameter p"),
		@NamedQuery(name = "Parameter.findAllNames", query = "SELECT p.name FROM Parameter p"),
		@NamedQuery(name = "Parameter.findByName", query = "SELECT p FROM Parameter p WHERE p.name = :name")
})
public class Parameter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String definition;

	private String name;

	public Parameter() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getDefinition() {
		return this.definition;
	}

	public void setDefinition(final String definition) {
		this.definition = definition;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}