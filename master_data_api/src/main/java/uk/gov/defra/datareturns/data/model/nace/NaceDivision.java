package uk.gov.defra.datareturns.data.model.nace;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * The persistent class for the NACE divisions
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_nace_division")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_nace_division_id_seq") }
)
@Getter @Setter
public final class NaceDivision extends AbstractNaceEntity {
    /** The parent section for this division */
    @ManyToOne(optional = false)
    private NaceSection section;

    /** The groups within this division */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id")
    private List<NaceGroup> groups;
}
