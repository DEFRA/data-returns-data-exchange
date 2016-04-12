package uk.gov.ea.datareturns.jpa.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the methods_or_standards database table.
 *
 */
@Entity
@Table(name = "methods_or_standards")
@NamedQueries({
	@NamedQuery(name = "MethodOrStandard.findAll", query = "SELECT m FROM MethodOrStandard m"),
	@NamedQuery(name = "MethodOrStandard.findByName", query = "SELECT m FROM MethodOrStandard m WHERE m.name = :name")
})

public class MethodOrStandard implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	public MethodOrStandard() {
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