package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * The persistent class for the units database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "units")
public class Unit implements ControlledList {
	@Id
	@SequenceGenerator(name = "units_id_seq", sequenceName = "units_id_seq", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="units_id_seq")
	@JsonIgnore
	private Long id;

	private String description;

	@Column(name = "measure_type")
	private String measureType;

	private String name;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getMeasureType() {
		return this.measureType;
	}

	public void setMeasureType(final String measureType) {
		this.measureType = measureType;
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

		Unit unit = (Unit) o;

		if (!id.equals(unit.id)) return false;
		if (!description.equals(unit.description)) return false;
		if (!measureType.equals(unit.measureType)) return false;
		return name.equals(unit.name);

	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + description.hashCode();
		result = 31 * result + measureType.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
}