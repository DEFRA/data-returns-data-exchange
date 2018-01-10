package uk.gov.defra.datareturns.data.model.nace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the NACE section codes
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_nace_section")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_nace_section_id_seq")}
)
@Getter
@Setter
public final class NaceSection extends AbstractNaceEntity {
    @OneToMany(mappedBy = "naceSection")
    @Setter(AccessLevel.NONE)
    private Set<NaceDivision> naceDivisions = new HashSet<>();
}
