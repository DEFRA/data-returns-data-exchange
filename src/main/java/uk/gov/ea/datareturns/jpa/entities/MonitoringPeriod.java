package uk.gov.ea.datareturns.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the monitoring_periods database table.
 *
 */
@Entity
@Table(name = "monitoring_periods")
@NamedQueries({
	@NamedQuery(name = "MonitoringPeriod.findAll", query = "SELECT m FROM MonitoringPeriod m"),
	@NamedQuery(name = "MonitoringPeriod.findAllNames", query = "SELECT m.name FROM MonitoringPeriod m"),
	@NamedQuery(name = "MonitoringPeriod.findByName", query = "SELECT m FROM MonitoringPeriod m WHERE m.name = :name")
})
public class MonitoringPeriod {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	public MonitoringPeriod() {
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