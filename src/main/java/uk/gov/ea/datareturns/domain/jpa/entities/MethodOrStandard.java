package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the methods_or_standards database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "methods_or_standards")
public class MethodOrStandard implements Serializable, ControlledList {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

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