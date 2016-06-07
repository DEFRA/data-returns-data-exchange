package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the reference_periods database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "reference_periods")
@NamedQueries({
		@NamedQuery(name = "ReferencePeriod.findAll", query = "SELECT r FROM ReferencePeriod r"),
		@NamedQuery(name = "ReferencePeriod.findAllNames", query = "SELECT r.name FROM ReferencePeriod r"),
		@NamedQuery(name = "ReferencePeriod.findByName", query = "SELECT r FROM ReferencePeriod r WHERE r.name = :name")
})
public class ReferencePeriod {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	public ReferencePeriod() {
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