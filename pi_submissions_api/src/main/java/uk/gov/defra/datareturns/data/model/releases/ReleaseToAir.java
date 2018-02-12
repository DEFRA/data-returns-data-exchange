package uk.gov.defra.datareturns.data.model.releases;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.validation.validators.id.ValidId;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * PI Releases to air
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "pi_release_to_air")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uniq_release_to_air_substance", columnNames = {"submission_id", "substanceId"})
})
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "pi_release_to_air_id_seq")}
)
@Audited
@Getter
@Setter
public class ReleaseToAir extends AbstractReleaseEntity {
    @Basic
    @ValidId(resourceCollectionUris = "parameterGroups/3/parameters")
    private int substanceId;
}
