package uk.gov.defra.datareturns.data.model.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Tracks substitutions made when processing a DEP CSV file - for usage analysis
 *
 * @author Sam Gardner-Dell
 */

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Substitution {
    @Column(name = "field", length = 20)
    private String field;

    @Column(name = "value", length = 250)
    private String value;

    @Column(name = "resolved", length = 250)
    private String resolved;
}
