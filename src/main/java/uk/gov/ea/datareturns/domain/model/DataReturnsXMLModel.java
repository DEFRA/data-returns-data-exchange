package uk.gov.ea.datareturns.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "returns")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataReturnsXMLModel {
	@XmlElement(name = "return", required = true)
	private List<MonitoringDataRecord> returns = new ArrayList<>();

	public DataReturnsXMLModel() {
	}

	/**
	 * @return the returns
	 */
	public List<MonitoringDataRecord> getReturns() {
		return returns;
	}

	/**
	 * @param returns
	 *            the returns to set
	 */
	public void setReturns(List<MonitoringDataRecord> returns) {
		this.returns = returns;
	}

}