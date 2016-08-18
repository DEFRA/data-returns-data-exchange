package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@JsonIgnore
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Parameter parameter = (Parameter) o;

		if (!id.equals(parameter.id)) return false;
		if (!definition.equals(parameter.definition)) return false;
		return name.equals(parameter.name);

	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + definition.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
}