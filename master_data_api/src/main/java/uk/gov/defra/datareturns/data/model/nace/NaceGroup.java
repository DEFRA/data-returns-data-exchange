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
 * The persistent class for the NACE groups
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_nace_group")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_nace_group_id_seq") }
)
@Getter @Setter
public final class NaceGroup extends AbstractNaceEntity {
    /** The parent division for this group */
    @ManyToOne(optional = false)
    private NaceDivision division;

    /** The classes within this group */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private List<NaceClass> classes;
}
