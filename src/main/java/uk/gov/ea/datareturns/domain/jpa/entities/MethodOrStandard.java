package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.*;

/**
 * The persistent class for the methods_or_standards database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "methods_or_standards")
public class MethodOrStandard implements ControlledList {

	@Id
	@SequenceGenerator(name = "seq", sequenceName = "methods_or_standards_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq")
	private Long id;

	private String name;
	private String description;
	private String origin;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Basic
	@Column(name = "name", nullable = false, length = 30)
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
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

	@Basic
	@Column(name = "name", length = 200)
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
}