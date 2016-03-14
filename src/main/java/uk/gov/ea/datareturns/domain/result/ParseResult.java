package uk.gov.ea.datareturns.domain.result;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Simple class to return the result of a successful parse to the client
 */
public class ParseResult {
	@JacksonXmlProperty(localName = "PermitNumber")
	private String permitNumber;

	@JacksonXmlProperty(localName = "SiteName")
	private String siteName;

	@JacksonXmlProperty(localName = "ReturnType")
	private String returnType;

	/**
	 * @return the permitNumber
	 */
	public String getPermitNumber() {
		return permitNumber;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @param permitNumber
	 *            the permitNumber to set
	 */
	public void setPermitNumber(String permitNumber) {
		this.permitNumber = permitNumber;
	}

	/**
	 * @param siteName
	 *            the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 * @param returnType
	 *            the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
}
