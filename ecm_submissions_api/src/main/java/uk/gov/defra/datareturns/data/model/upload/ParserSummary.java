package uk.gov.defra.datareturns.data.model.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Tracks substitutions made when processing a DEP CSV file - for usage analysis
 *
 * @author Sam Gardner-Dell
 */

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class ParserSummary {
    @Column(name = "submitted_ea_id", length = 12)
    private String submittedEaId;

    @Column(name = "resolved_ea_id", length = 12)
    private String resolvedEaId;

    @Column(name = "site_name", length = 250)
    private String siteName;
}
