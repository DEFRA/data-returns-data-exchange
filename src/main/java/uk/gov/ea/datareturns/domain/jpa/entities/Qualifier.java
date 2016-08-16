package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.*;

/**
 * The persistent class for the qualifiers database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "qualifiers")
public class Qualifier implements ControlledList {

	@Id
	@SequenceGenerator(name = "qualifiers_id_seq", sequenceName = "qualifiers_id_seq", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="qualifiers_id_seq")
	private Long id;

	private String name;
	private String notes;
	private String suggested_category;

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
	@Column(name = "notes", length = 100)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String description) {
		this.notes = description;
	}

	@Basic
	@Column(name = "suggested_category", length = 20)
	public String getSuggested_category() {
		return suggested_category;
	}

	public void setSuggested_category(String suggested_category) {
		this.suggested_category = suggested_category;
	}
}