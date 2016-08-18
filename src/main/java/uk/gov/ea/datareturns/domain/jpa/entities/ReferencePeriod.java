package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

/**
 * The persistent class for the reference_periods database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "reference_periods")
public class ReferencePeriod implements ControlledList, AliasingEntity {
	@Id
	@SequenceGenerator(name = "reference_periods_id_seq", sequenceName = "reference_periods_id_seq", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="reference_periods_id_seq")
	private Long id;

	private String name;
	private String notes;
	private String preferred;

    @Transient
	private Set<String> aliases = null;

	@Override
	@JsonIgnore
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	@Basic
	@Column(name = "name", nullable = false, length = 100)
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "notes", length = 250)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String description) {
		this.notes = description;
	}

	@Basic
	@JsonIgnore
	@Column(name = "preferred", length = 100)
	public String getPreferred() {
		return preferred;
	}

	@Override
	public void setPreferred(String preferred) {
		this.preferred = preferred;
	}

	@Override
	public Set<String> getAliases() {
		return this.aliases;
	}

	@Override
	public void setAliases(Set<String> aliases) {
		this.aliases = aliases;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ReferencePeriod that = (ReferencePeriod) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		return notes != null ? notes.equals(that.notes) : that.notes == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (notes != null ? notes.hashCode() : 0);
		return result;
	}
}