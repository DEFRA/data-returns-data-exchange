package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.*;

/**
 * The persistent class for the unique_identifiers database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "unique_identifiers")
public class UniqueIdentifier implements PersistedEntity {

	@Id
	@SequenceGenerator(name = "unique_identifiers_id_seq", sequenceName = "unique_identifiers_id_seq", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="unique_identifiers_id_seq")
	private Long id;
	private String name;

	public Long getId() {
		return this.id;
	}
	public void setId(final Long id) {
		this.id = id;
	}

	@Basic
	@Column(name = "name", nullable = false, length = 10)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UniqueIdentifier uniqueIdentifier = (UniqueIdentifier) o;

		if (!id.equals(uniqueIdentifier.id)) return false;
		return name.equals(uniqueIdentifier.name);

	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
}