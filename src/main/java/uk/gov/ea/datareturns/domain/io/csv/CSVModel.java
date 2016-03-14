package uk.gov.ea.datareturns.domain.io.csv;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CSVModel<T extends Object> {
	/**
	 * The header map that iterates in column order.
     * <p>
     * The map keys are column names. The map values are 0-based indices.
     * </p>
	 */
	private Map<String, Integer> headerMap = new LinkedHashMap<>();
	
	/**
	 * Provides a mapping between the Java Object field and the header name to which it is mapped
	 */
	private Map<String, String> pojoFieldToHeaderMap = new LinkedHashMap<>();
	
	
	
	/**
	 * A list of records in document order
	 */
	private List<T> records;
	
	/**
	 * Default constructor 
	 */
	public CSVModel() {
		
	}

	/**
	 * @return the headerMap
	 */
	public Map<String, Integer> getHeaderMap() {
		return headerMap;
	}

	/**
	 * @param headerMap the headerMap to set
	 */
	public void setHeaderMap(Map<String, Integer> headerMap) {
		this.headerMap = headerMap;
	}

	/**
	 * @return the pojoFieldToHeaderMap
	 */
	public Map<String, String> getPojoFieldToHeaderMap() {
		return pojoFieldToHeaderMap;
	}

	/**
	 * @param pojoFieldToHeaderMap the pojoFieldToHeaderMap to set
	 */
	public void setPojoFieldToHeaderMap(Map<String, String> pojoFieldToHeaderMap) {
		this.pojoFieldToHeaderMap = pojoFieldToHeaderMap;
	}

	/**
	 * @return the records
	 */
	public List<T> getRecords() {
		return records;
	}

	/**
	 * @param records the records to set
	 */
	public void setRecords(List<T> records) {
		this.records = records;
	}
}
