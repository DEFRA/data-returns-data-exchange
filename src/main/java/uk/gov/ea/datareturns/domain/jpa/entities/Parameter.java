package uk.gov.ea.datareturns.domain.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

/**
 * The persistent class for the parameters database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "parameters")
public class Parameter implements AliasingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Long id;

	private String name;
	private String cas;
	private String preferred;
	private String type;

    @Transient
    Set<String> aliases = null;

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
	@Column(name = "name", nullable = false, length = 150)
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	@JsonIgnore
	@Basic
	@Column(name = "preferred", length = 150)
	public String getPreferred() {
		return preferred;
	}

	@Override
	public void setPreferred(String preferred) {
		this.preferred = preferred;
	}

	@JsonIgnore
	@Basic
    @Column(name = "type", length = 100)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "cas", length = 50)
    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
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

		Parameter parameter = (Parameter) o;

		if (id != null ? !id.equals(parameter.id) : parameter.id != null) return false;
		if (name != null ? !name.equals(parameter.name) : parameter.name != null) return false;
		if (cas != null ? !cas.equals(parameter.cas) : parameter.cas != null) return false;
		if (preferred != null ? !preferred.equals(parameter.preferred) : parameter.preferred != null) return false;
		return type != null ? type.equals(parameter.type) : parameter.type == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (cas != null ? cas.hashCode() : 0);
		result = 31 * result + (preferred != null ? preferred.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}
}