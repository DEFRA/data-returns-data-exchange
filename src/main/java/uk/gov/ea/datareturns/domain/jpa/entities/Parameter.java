package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.*;

/**
 * The persistent class for the parameters database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "parameters")
public class Parameter implements ControlledList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String definition;

	private String name;

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