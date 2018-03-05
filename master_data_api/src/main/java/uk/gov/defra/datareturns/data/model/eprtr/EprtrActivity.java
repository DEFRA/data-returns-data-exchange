package uk.gov.defra.datareturns.data.model.eprtr;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;

import javax.persistence.*;

@Entity(name = "md_eprtr_activity")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_eprtr_activity_id_seq")}
)
@Getter
@Setter
public class EprtrActivity extends AbstractMasterDataEntity {

    @ManyToOne(optional = false)
    private EprtrSector eprtrSector;

    @Basic(optional = false)
    @Column(length = 500, unique = true)
    private String description;

    @Basic
    @Column(length = 500)
    private String threshold;

}
