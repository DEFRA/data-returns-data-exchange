package uk.gov.defra.datareturns.data.model.qualifier;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * The persistent class for the qualifiers database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_qualifier")
@Cacheable
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_qualifier_id_seq") }
)
@Getter @Setter
public class Qualifier extends AbstractMasterDataEntity implements MasterDataEntity {
    @Basic
    @Column(name = "notes", length = 100)
    private String notes;

    @Basic
    @Column(name = "type", length = 50)
    private String type;

    @Basic
    @Column(name = "singleormultiple", length = 20)
    private String singleOrMultiple;
}
