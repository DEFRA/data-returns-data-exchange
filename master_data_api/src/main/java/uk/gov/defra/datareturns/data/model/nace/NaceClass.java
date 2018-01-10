package uk.gov.defra.datareturns.data.model.nace;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * The persistent class for the NACE classes
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_nace_class")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_nace_class_id_seq")}
)
@Getter
@Setter
public final class NaceClass extends AbstractNaceEntity {
    /**
     * The parent group for this class
     */
    @ManyToOne(optional = false)
    private NaceGroup naceGroup;
}
