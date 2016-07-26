package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the units database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "units")
@NamedQueries({
		@NamedQuery(name = "Unit.findAll", query = "SELECT u FROM Unit u"),
		@NamedQuery(name = "Unit.findAllNames", query = "SELECT u.name FROM Unit u"),
		@NamedQuery(name = "Unit.findByName", query = "SELECT u FROM Unit u WHERE u.name = :name")
})

public class Unit implements ControlledList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String description;

	@Column(name = "measure_type")
	private String measureType;

	private String name;

	public Unit() {
	}

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

}