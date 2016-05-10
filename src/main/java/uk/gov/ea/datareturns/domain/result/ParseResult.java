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

import uk.gov.ea.datareturns.domain.model.EaId;
import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;

/**
 * Simple class to return the result of a successful parse to the client
 */
public class ParseResult {
	@JsonProperty
	private List<IdentifierMappings> mappings;

	public ParseResult() {
	}

	public ParseResult(final List<MonitoringDataRecord> records) {
		// Mapping of Site_Name to details about a particular EA_ID
		final Map<String, IdentifierMappings> siteIdentifierMap = new TreeMap<>(ComparatorUtils.nullLowComparator(null));
		// Mapping of EA_ID to details
		final Map<EaId, IdentifierDetails> eaIdInfoMap = new TreeMap<>();

		for (final MonitoringDataRecord record : records) {
			final EaId eaId = record.getEaId();
			final String siteName = record.getSiteName();

			IdentifierMappings sitePermitMapping = siteIdentifierMap.get(siteName);
			if (sitePermitMapping == null) {
				sitePermitMapping = new IdentifierMappings(siteName);
				siteIdentifierMap.put(siteName, sitePermitMapping);
			}

			IdentifierDetails idInfo = eaIdInfoMap.get(eaId);
			if (idInfo == null) {
				idInfo = new IdentifierDetails(eaId, 0);
				eaIdInfoMap.put(eaId, idInfo);
			}
			idInfo.incrementCount();

			sitePermitMapping.addIdentifier(idInfo);
			this.mappings = new ArrayList<IdentifierMappings>(siteIdentifierMap.values());
		}
	}

	/**
	 * @return the mappings
	 */
	public List<IdentifierMappings> getMappings() {
		return this.mappings;
	}

	public static class IdentifierMappings {
		@JsonProperty
		private String site;

		@JsonProperty
		private final Set<IdentifierDetails> identifiers = new TreeSet<>(ComparatorUtils.naturalComparator());

		public IdentifierMappings() {
		}

		public IdentifierMappings(final String site) {
			this.site = site;
		}

		public boolean addIdentifier(final IdentifierDetails identifierDetails) {
			return this.identifiers.add(identifierDetails);
		}
	}

	public static class IdentifierDetails implements Comparable<IdentifierDetails> {
		@JsonUnwrapped
		private EaId eaId;

		@JsonProperty
		private long count = 0;

		public IdentifierDetails() {
		}

		public IdentifierDetails(final EaId eaId, final long count) {
			this.eaId = eaId;
			this.count = count;
		}

		public void incrementCount() {
			++this.count;
		}

		@Override
		public int compareTo(final IdentifierDetails o) {
			return this.eaId.compareTo(o.eaId);
		}
	}
}