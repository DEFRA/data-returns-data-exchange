package uk.gov.ea.datareturns.domain.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections4.ComparatorUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.EaId;

/**
 * Response message container to summarise the information which was parsed from the uploaded file.
 *
 * @author Sam Gardner-Dell
 */
public class ParseResult {
	@JsonProperty
	private List<SiteSummary> siteSummaries;

	/**
	 * Default zero-arg constructor (for serialization support)
	 */
	@SuppressWarnings("unused")
	public ParseResult() {}

	/**
	 * Create a new ParseResult from the {@link List} of records that was parsed
	 *
	 * @param records the {@link List} of {@link DataSample} objects which were parsed
	 */
	public ParseResult(final List<DataSample> records) {
		// Mapping of Site_Name to details about a particular EA_ID.  Note that site is optional
		// so identifiers with no site are displayed at the top of the structure with a null site name
		final Map<String, SiteSummary> siteSummariesMap = new TreeMap<>(ComparatorUtils.nullLowComparator(null));
		// Mapping of EA_ID to details
		final Map<EaId, EaIdSummary> eaIdSummaries = new TreeMap<>();

		/*
		 * Iterate the DataSample records to create a summary based on Site_Name
		 */
		for (final DataSample record : records) {
			final EaId eaId = record.getEaId();
			final String siteName = record.getSiteName();

			// Retrieve the set of identifiers belonging to this site.
			SiteSummary siteSummary = siteSummariesMap.get(siteName);
			if (siteSummary == null) {
				siteSummary = new SiteSummary(siteName);
				siteSummariesMap.put(siteName, siteSummary);
			}

			// Retrieve the information recorded about the current EA_ID thus far
			EaIdSummary eaIdSummary = eaIdSummaries.get(eaId);
			if (eaIdSummary == null) {
				eaIdSummary = new EaIdSummary(eaId);
				eaIdSummaries.put(eaId, eaIdSummary);
			}
			// Increment the instant count for this EA_ID
			eaIdSummary.incrementCount();
			// Add this EA_ID into the site mapping
			siteSummary.addIdentifier(eaIdSummary);
			// Convert the mapping to a list of values
			this.siteSummaries = new ArrayList<>(siteSummariesMap.values());
		}
	}

	/**
	 * @return the siteSummaries detailing the sites that were parsed and the unique identifiers for each site
	 */
	public List<SiteSummary> getSiteSummaries() {
		return this.siteSummaries;
	}

	/**
	 * Summary of the information parsed for a particular site name
	 *
	 * @author Sam Gardner-Dell
	 */
	public static class SiteSummary {
		@JsonProperty
		private String site;

		@JsonProperty
		private final Set<EaIdSummary> identifiers = new TreeSet<>(ComparatorUtils.naturalComparator());

		/**
		 * Default zero-arg constructor (for serialization support)
		 */
		@SuppressWarnings("unused")
		public SiteSummary() {}

		/**
		 * Create a new SiteSummary instance for the specified site
		 *
		 * @param site the name of the site to use
		 */
		public SiteSummary(final String site) {
			this.site = site;
		}

		/**
		 * Add a new instance of a unique identifier belonging to this site
		 *
		 * @param eaIdSummary the details about the unique identifier to be added
		 * @return true if the identifier was added, false otherwise
		 */
		public boolean addIdentifier(final EaIdSummary eaIdSummary) {
			return this.identifiers.add(eaIdSummary);
		}
	}

	/**
	 * Stores a count of occurrences for a particular unique identifier
	 *
	 * @author Sam Gardner-Dell
	 */
	public static class EaIdSummary implements Comparable<EaIdSummary> {
		@JsonUnwrapped
		private EaId eaId;
		@JsonProperty
		private long count;

		/**
		 * Default zero-arg constructor (for serialization support)
		 */
		@SuppressWarnings("unused")
		public EaIdSummary() {}

		/**
		 * Create a new EaIdSummary instance for the given unique identifier
		 *
		 * @param eaId the unique identifier to create the details for
		 */
		public EaIdSummary(final EaId eaId) {
			this.eaId = eaId;
			this.count = 0;
		}

		/**
		 * Increment the count of occurrences for this unique identifier
		 */
		public void incrementCount() {
			++this.count;
		}

		@Override
		public int compareTo(final EaIdSummary o) {
			return this.eaId.compareTo(o.eaId);
		}
	}}