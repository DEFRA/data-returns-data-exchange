package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.*;

/**
 * The persistent class for the qualifiers database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "qualifiers")
public class Qualifier implements PersistedEntity {

	@Id
	@SequenceGenerator(name = "qualifiers_id_seq", sequenceName = "qualifiers_id_seq", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="qualifiers_id_seq")
	private Long id;

	private String name;
	private String description;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Override
	@Basic
	@Column(name = "name", nullable = false, length = 70)
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "description", length = 100)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Qualifier qualifier = (Qualifier) o;

		if (id != null ? !id.equals(qualifier.id) : qualifier.id != null) return false;
		if (name != null ? !name.equals(qualifier.name) : qualifier.name != null) return false;
		return description != null ? description.equals(qualifier.description) : qualifier.description == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		return result;
	}
}