package uk.gov.ea.datareturns.domain.jpa.entities;

import javax.persistence.*;

/**
 * The persistent class for the monitoring_periods database table.
 *
 */
@SuppressWarnings({ "JavaDoc", "unused" })
@Entity
@Table(name = "monitoring_periods")
public class MonitoringPeriod implements ControlledList {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MonitoringPeriod that = (MonitoringPeriod) o;

		if (!id.equals(that.id)) return false;
		return name.equals(that.name);

	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
}