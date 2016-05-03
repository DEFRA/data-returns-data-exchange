package uk.gov.ea.datareturns.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the return_types database table.
 *
 */
@Entity
@Table(name = "return_types")
@NamedQueries({
		@NamedQuery(name = "ReturnType.findAll", query = "SELECT r FROM ReturnType r"),
		@NamedQuery(name = "ReturnType.findAllNames", query = "SELECT r.name FROM ReturnType r"),
		@NamedQuery(name = "ReturnType.findByName", query = "SELECT r FROM ReturnType r WHERE r.name = :name")
})
public class ReturnType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	public ReturnType() {
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