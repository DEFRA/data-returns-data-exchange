package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.*;

/**
 * The persistent class for the reference_periods database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "reference_periods")
public class ReferencePeriod implements ControlledList {
	@Id
	@SequenceGenerator(name = "reference_periods_id_seq", sequenceName = "reference_periods_id_seq", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="reference_periods_id_seq")
	private Long id;

	private String name;
	private String description;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	@Basic
	@Column(name = "name", nullable = false, length = 200)
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "name", length = 500)
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

		ReferencePeriod that = (ReferencePeriod) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		return description != null ? description.equals(that.description) : that.description == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		return result;
	}
}