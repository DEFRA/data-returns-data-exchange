package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * The persistent class for the return_periods database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "return_periods")
public class ReturnPeriod implements ControlledList {
	@Id
	@SequenceGenerator(name = "return_periods_id_seq", sequenceName = "return_periods_id_seq", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="return_periods_id_seq")
	@JsonIgnore
	private Long id;

	private String name;

	public String definition;

	public String example;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	@Basic
	@Column(name = "name", nullable = false, length = 20)
	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "definition", nullable = false, length = 600)
	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String description) {
		this.definition = description;
	}

	@Basic
	@Column(name = "example", nullable = false, length = 20)
	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ReturnPeriod that = (ReturnPeriod) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (definition != null ? !definition.equals(that.definition) : that.definition != null) return false;
		return example != null ? example.equals(that.example) : that.example == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (definition != null ? definition.hashCode() : 0);
		result = 31 * result + (example != null ? example.hashCode() : 0);
		return result;
	}
}