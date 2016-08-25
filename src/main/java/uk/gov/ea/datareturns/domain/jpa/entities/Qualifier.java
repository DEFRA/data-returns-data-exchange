package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * The persistent class for the qualifiers database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "qualifiers")
public class Qualifier implements ControlledListEntity {

	@JsonIgnore
	@Id
	@SequenceGenerator(name = "qualifiers_id_seq", sequenceName = "qualifiers_id_seq", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="qualifiers_id_seq")
	private Long id;

	private String name;
	private String notes;
	private String type;
	private String SingleOrMultiple;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Override
	@Basic
	@Column(name = "name", nullable = false, length = 100)
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
	@Column(name = "type", length = 50)
	public String getType() {
		return type;
	}

	public void setType(String suggested_category) {
		this.type = suggested_category;
	}

    @Basic
    @Column(name = "singleormultiple", length = 20)
	public String getSingleOrMultiple() {
		return SingleOrMultiple;
	}

	public void setSingleOrMultiple(String singleOrMultiple) {
		SingleOrMultiple = singleOrMultiple;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Qualifier qualifier = (Qualifier) o;

		if (id != null ? !id.equals(qualifier.id) : qualifier.id != null) return false;
		if (name != null ? !name.equals(qualifier.name) : qualifier.name != null) return false;
		if (notes != null ? !notes.equals(qualifier.notes) : qualifier.notes != null) return false;
		if (type != null ? !type.equals(qualifier.type) : qualifier.type != null) return false;
		return SingleOrMultiple != null ? SingleOrMultiple.equals(qualifier.SingleOrMultiple) : qualifier.SingleOrMultiple == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (notes != null ? notes.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (SingleOrMultiple != null ? SingleOrMultiple.hashCode() : 0);
		return result;
	}
}